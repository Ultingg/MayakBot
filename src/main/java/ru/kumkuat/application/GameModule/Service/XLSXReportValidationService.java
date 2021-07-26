package ru.kumkuat.application.GameModule.Service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.kumkuat.application.GameModule.Models.BGUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

@Component
public class XLSXReportValidationService {

    String path = "../resources/report.xlsx";

    public SendDocument writeReportBGUserNotRegitred(List<BGUser> bgUsers) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Unregistred");

        int rowNum = 0;
        Cell cell;
        Row row;
        creatHeader(workbook, sheet);

        for (BGUser bgUser : bgUsers) {
            rowNum++;
            row = sheet.createRow(rowNum);

            cell = row.createCell(0, NUMERIC);
            cell.setCellValue(bgUser.getId());

            cell = row.createCell(1, STRING);
            cell.setCellValue(bgUser.getTelegramUserName());

            cell = row.createCell(2, STRING);
            cell.setCellValue(bgUser.getEmail());
        }
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOutputStream;
        File file = new File(path);
        InputFile inputFile = new InputFile();
        SendDocument sendDocument = new SendDocument();


        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            inputFile.setMedia(file);
            sendDocument.setDocument(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sendDocument;
    }

    private void creatHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
        Cell cell;
        Row row = sheet.createRow(0);

        cell = row.createCell(0, NUMERIC);
        cell.setCellValue("Id");

        cell = row.createCell(1, STRING);
        cell.setCellValue("Username");

        cell = row.createCell(2, STRING);
        cell.setCellValue("E-mail");
    }
}
