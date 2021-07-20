package ru.kumkuat.application.GameModule.Service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.kumkuat.application.GameModule.Models.BGUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

@Component
@PropertySource(value = "file:../resources/externalsecret.yml")
public class XLSXBGListReaderService {

    private final BGUserService bgUserService;
    private final UserService userService;

    private FileInputStream file;
    private Workbook workbook;

    public XLSXBGListReaderService(BGUserService bgUserService, UserService userService) throws IOException {
        this.bgUserService = bgUserService;
        this.userService = userService;
    }


    public void XLSXBGParser(String pathDataFile) throws IOException {
        var dataFile = new File(pathDataFile);
        if(dataFile.exists()){
            this.file = new FileInputStream(dataFile);
            this.workbook = new XSSFWorkbook(file);
            boolean header = true;
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (!header) {
                    BGUser newBGUser = new BGUser();
                    User newTelegramUser = new User();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    int lengthOfRow = row.getPhysicalNumberOfCells();
                    for (int cellNumber = 0; cellNumber < lengthOfRow; cellNumber++) {
                        Cell cell = cellIterator.next();
                        switch (cellNumber) {
                            case 5:
                                newBGUser.setCodeTicket(cell.getStringCellValue());
                                break;
                            case 15:
                                newBGUser.setEmail(cell.getStringCellValue());
                                break;
                            case 13:
                                //newTelegramUser.setLastName(cell.getStringCellValue());
                                break;
                            case 14:
                                //newTelegramUser.setFirstName(cell.getStringCellValue());
                                break;
                            case 18:
                                //String convertedUsername = convertTelegramUserName(cell.getStringCellValue());
                                newBGUser.setTelegramUserName(cell.getStringCellValue());
                                //newTelegramUser.setUserName(convertedUsername);
                                break;
                            case 19:
                                newBGUser.setPreferredTime(cell.getStringCellValue());
                                break;
                            case 20:
                                newBGUser.setStartWith(cell.getStringCellValue());
                                break;
                        }
                    }
                    bgUserService.setBGUserToDB(newBGUser);
                }
                header = false;
            }
        }
    }

    private String convertTelegramUserName(String username) {
        return username.substring(1);
    }
}
