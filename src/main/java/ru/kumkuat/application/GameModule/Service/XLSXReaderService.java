package ru.kumkuat.application.GameModule.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.kumkuat.application.GameModule.Collections.Reply;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Collections.Trigger;
import ru.kumkuat.application.GameModule.Models.Geolocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@PropertySource(value = "file:../resources/externalsecret.yml")
public class XLSXReaderService {

    private final PictureService pictureService;
    private final GeolocationDatabaseService geolocationDatabaseService;
    private final AudioService audioService;
    private final StickerService stickerService;
    //@Value("${excle.path}")
    private String path = "..\\resources\\input.xlsx";

    private FileInputStream file;
    private Workbook workbook;

    public XLSXReaderService(PictureService pictureService, GeolocationDatabaseService geolocationDatabaseService,
                             AudioService audioService, StickerService stickerService) throws IOException {
        this.pictureService = pictureService;
        this.geolocationDatabaseService = geolocationDatabaseService;
        this.audioService = audioService;
        this.stickerService = stickerService;
        this.file = new FileInputStream(new File(path));
        this.workbook = new XSSFWorkbook(file);
    }


    public List<Scene> parseXLXS() {
        boolean header = true;
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<Scene> scenes = new ArrayList<>();
        double numberOfScene = 0;
        Scene tempScene = new Scene();
        Reply tempReply = new Reply();
        String type = "";
        for (Row row : sheet) {
            if (!header) {

                Iterator<Cell> cellIterator = row.cellIterator();
                int numberO = row.getPhysicalNumberOfCells();
                for (int cellCount = 0; cellCount < numberO; cellCount++) {

                    Cell cell = cellIterator.next();
                    switch (cellCount) {
                        case 0:
                            if (numberOfScene != cell.getNumericCellValue()) {
                                numberOfScene = cell.getNumericCellValue();
                                scenes.add(tempScene);
                                tempScene = new Scene();
                            }
                            break;
                        case 1:
                            if (cell.getCellType() == CellType.STRING ||
                                    (cell.getCellType() == CellType.NUMERIC && cell.getNumericCellValue() != 0.0)) {
                                Trigger newTrigger = new Trigger();
                                if (cell.getCellType() == CellType.STRING)
                                    newTrigger.setText(cell.getStringCellValue());
                                if (cell.getCellType() == CellType.NUMERIC)
                                    newTrigger.setText(String.valueOf(cell.getNumericCellValue()));
                                tempScene.setTrigger(newTrigger);
                            }
                            break;
                        case 2:
                            tempReply = new Reply();
                            tempReply.setBotName(cell.getStringCellValue());
                            break;
                        case 3:
                            type = cell.getStringCellValue();
                            break;
                        case 4:
                            if (type.equals("message")) {
                                tempReply.setTextMessage(cell.getStringCellValue());
                            }
                            if (type.equals("picture")) {
                                String picturePath = cell.getStringCellValue();
                                long pictureId = pictureService.setPictureIntoDB(picturePath);
                                tempReply.setPictureId(pictureId);
                            }
                            if (type.equals("geolocation")) {
                                Geolocation geolocation = geolocationSpliterator(cell.getStringCellValue());
                                long geolocationId = geolocationDatabaseService.setGeolocationIntoDB(geolocation);
                                tempReply.setGeolocationId(geolocationId);
                            }
                            if (type.equals("audio")) {
                                String audioPath = cell.getStringCellValue();
                                long audioId = audioService.setAudioIntoDB(audioPath);
                                tempReply.setAudioId(audioId);
                            }
                            if (type.equals("sticker")) {
                                String stickerPath = cell.getStringCellValue();
                                long stickerId = stickerService.setStickerToDB(stickerPath);
                                tempReply.setStickerId(stickerId);
                            }
                            break;
                        case 5:
                            tempReply.setTiming((int) cell.getNumericCellValue());

                            tempScene.insertReply(tempReply);
                            break;
                    }
                }
            }
            header = false;
        }
        return scenes;
    }

    public Geolocation geolocationSpliterator(String geolocationString) {
        Geolocation geolocation = new Geolocation();
        String[] array = geolocationString.split("/");
        geolocation.setLatitude(Double.valueOf(array[1]));
        geolocation.setLongitude(Double.valueOf(array[2]));
        geolocation.setFullName(array[0]);
        return geolocation;
    }
}
