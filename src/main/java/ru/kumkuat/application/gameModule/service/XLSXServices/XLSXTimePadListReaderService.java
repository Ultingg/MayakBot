package ru.kumkuat.application.gameModule.service.XLSXServices;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.kumkuat.application.gameModule.promocode.Model.TimePadOrder;
import ru.kumkuat.application.gameModule.promocode.Service.TimePadOrderService;
import ru.kumkuat.application.gameModule.service.GeneralXLSXReader;

import java.io.File;
import java.io.FileInputStream;

@Component(value = "timePadOrder")
@PropertySource(value = "file:../resources/externalsecret.yml")
public class XLSXTimePadListReaderService extends GeneralXLSXReader {

    private final TimePadOrderService timePadUserService;

    private FileInputStream file;
    private Workbook workbook;

    public XLSXTimePadListReaderService(TimePadOrderService timePadUserService) {
        this.timePadUserService = timePadUserService;
    }

    @Override
    public void fillHeaderProperty() {
        matchPropertyToHeader.put("email", "E-mail");
        matchPropertyToHeader.put("orderStatus", "Статус заказа");
        matchPropertyToHeader.put("orderNumber", "Номер заказа");
        matchPropertyToHeader.put("amountTickets", "Количество билетов");
        matchPropertyToHeader.put("firstName", "Имя");
        matchPropertyToHeader.put("lastName", "Фамилия");
        matchPropertyToHeader.put("time", "Дата");
    }

    @Override
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
                    TimePadOrder newTimpadUser = new TimePadOrder();
                    for (var prop : matchPropertyToHeader.keySet()) {
                        var headerValue = matchPropertyToHeader.get(prop);
                        var columnIndex = getCellColumnNumber(headerValue, headerRow);
                        var cellValue = getCellValue(row, columnIndex);
                        SetFieldValue(TimePadOrder.class, newTimpadUser, prop, cellValue);
                    }
                    newTimpadUser.setIsNotified(false);

                    if (!timePadUserService.isOrderExistsByOrderNumber(newTimpadUser.getOrderNumber())) {
                        timePadUserService.save(newTimpadUser);
                        counter++;
                    }
                }
            }
        }
        return counter;
    }

    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell.getCellType().equals(CellType.STRING)) {
            return cell.getStringCellValue();
        } else if (cell.getCellType().equals(CellType.NUMERIC)) {
            return String.valueOf((int) cell.getNumericCellValue());
        } else {
            return "";
        }
    }
}
