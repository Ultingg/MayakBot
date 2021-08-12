package ru.kumkuat.application.GameModule.Service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.kumkuat.application.GameModule.Models.BGUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
@PropertySource(value = "file:../resources/externalsecret.yml")
public class XLSXBGListReaderService {

    private final BGUserService bgUserService;
    private final UserService userService;
    private final Map<String, String> matchPropertyToHeader = new HashMap<>();

    private FileInputStream file;
    private Workbook workbook;

    public XLSXBGListReaderService(BGUserService bgUserService, UserService userService) throws IOException {
        this.bgUserService = bgUserService;
        this.userService = userService;

        matchPropertyToHeader.put("email", "E-mail");
        matchPropertyToHeader.put("preferredTime", "Желаемое время начала");
        matchPropertyToHeader.put("telegramUserName", "Имя пользователья в Telegram");
        matchPropertyToHeader.put("FirstName", "Имя");
        matchPropertyToHeader.put("SecondName", "Фамилия");
        matchPropertyToHeader.put("codeTicket", "Код");
        matchPropertyToHeader.put("startWith", "Хочу начать вместе с...");
    }

    private <T> void SetFieldValue(Class<T> cls, T obj, String name, String value) {
        for (var field :
                cls.getDeclaredFields()) {
            if (field.getName().equals(name)) {
                try {
                    field.setAccessible(true);
                    if (field.getType().equals(String.class)) {
                        field.set( obj, value);
                    } else if (field.getType().equals(Integer.TYPE)) {
                        field.setInt( obj, Integer.parseInt(value));
                    } else if (field.getType().equals(Long.TYPE)) {
                        field.setLong( obj, Long.parseLong(value));
                    }
                } catch (IllegalAccessException ex) {

                }
            }
        }
    }

    private Row getHeader(Sheet sheet) throws Exception {
        for (Row row : sheet) {
            Iterator<Cell> cellIterator = row.cellIterator();
            //int lengthOfRow = row.getPhysicalNumberOfCells();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (matchPropertyToHeader.containsValue(cell.getStringCellValue())) {
                    return row;
                }
            }
        }
        throw new Exception("Header is not found!");
    }
    private Integer getCellColumnNumber(String headerValue, Row header) throws Exception{
        Iterator<Cell> cellIterator = header.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if ( cell.getStringCellValue().equals(headerValue)) {
                return cell.getColumnIndex();
            }
        }
        throw new Exception("Header is not contains this value!");
    }

    public int XLSXBGParser(String pathDataFile) throws Exception {
        int counter = 0;
        var dataFile = new File(pathDataFile);
        if (dataFile.exists()) {
            this.file = new FileInputStream(dataFile);
            this.workbook = new XSSFWorkbook(file);
            boolean header = false;
            Sheet sheet = workbook.getSheetAt(0);
            var headerRow = getHeader(sheet);
            for (Row row : sheet) {
                if (headerRow.equals(row)) {
                    header = true;
                    continue;
                }
                if (header) {
                    BGUser newBGUser = new BGUser();
                    for (var prop:
                            matchPropertyToHeader.keySet()) {
                        var headerValue = matchPropertyToHeader.get(prop);
                        var columnIndex = getCellColumnNumber(headerValue, headerRow);
                        var cellValue = row.getCell(columnIndex).getStringCellValue();
                        SetFieldValue(BGUser.class, newBGUser, prop, cellValue);
                    }
                    newBGUser.setIsNotified(false);
                    newBGUser.setTelegramUserName(convertTelegramUserName(newBGUser.getTelegramUserName()));
                    newBGUser.setStartWith(convertTelegramUserName(newBGUser.getStartWith()));
                    if (!bgUserService.isBGUserExistByUsername(newBGUser.getTelegramUserName())) {
                        bgUserService.setBGUserToDB(newBGUser);
                        bgUserService.calculateAndSetStartTimeForBGUser(newBGUser);
                        counter++;
                    }
                }
            }
        }
        return counter;
    }

    private String convertTelegramUserName(String username) {
        String[] array = username.split(" ");
        String finalUsername = Arrays.stream(array)
                .filter(string -> string.contains("@"))
                .findFirst()
                .orElse("");
        finalUsername = finalUsername.replace("@", "");
        return finalUsername;
    }
}
