package ru.kumkuat.application.gameModule.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class GeneralXLSXReader {

    protected final Map<String, String> matchPropertyToHeader = new HashMap<>();


    public abstract void fillHeaderProperty();


    protected <T> void SetFieldValue(Class<T> cls, T obj, String name, String value) {
        for (var field :
                cls.getDeclaredFields()) {
            if (field.getName().equals(name)) {
                try {
                    field.setAccessible(true);
                    if (field.getType().equals(String.class)) {
                        field.set( obj, value);
                    } else if (field.getType().toString().equals("class java.lang.Integer")) {
                        field.set( obj, Integer.parseInt(value));
                    } else if (field.getType().toString().equals("class java.lang.Long")) {
                        field.set( obj, Long.parseLong(value));
                    }
                } catch (IllegalAccessException ex) {

                }
            }
        }
    }


    protected Row getHeader(Sheet sheet) throws Exception {
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

    protected Integer getCellColumnNumber(String headerValue, Row header) throws Exception{
        Iterator<Cell> cellIterator = header.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if ( cell.getStringCellValue().equals(headerValue)) {
                return cell.getColumnIndex();
            }
        }
        throw new Exception("Header is not contains this value!");
    }

    public abstract int XLSXBGParser(String pathDataFile) throws Exception;
}
