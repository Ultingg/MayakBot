package ru.kumkuat.application.gameModule.service.XLSXServices;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.kumkuat.application.gameModule.collections.PinnedMessage;
import ru.kumkuat.application.gameModule.collections.Reply;
import ru.kumkuat.application.gameModule.collections.Scene;
import ru.kumkuat.application.gameModule.collections.Trigger;
import ru.kumkuat.application.gameModule.models.Geolocation;
import ru.kumkuat.application.gameModule.service.AudioService;
import ru.kumkuat.application.gameModule.service.GeolocationDatabaseService;
import ru.kumkuat.application.gameModule.service.PictureService;
import ru.kumkuat.application.gameModule.service.StickerService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@PropertySource(value = "file:../resources/externalsecret.yml")
public class XLSXScenarioReaderService {

    private final PictureService pictureService;
    private final GeolocationDatabaseService geolocationDatabaseService;
    private final AudioService audioService;
    private final StickerService stickerService;
    private String path = "../resources/input.xlsx";

    private FileInputStream file;
    private Workbook workbook;

    public XLSXScenarioReaderService(PictureService pictureService, GeolocationDatabaseService geolocationDatabaseService,
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
                    switch (cellCount) {//перебераем еолонки по порякдку
                        case 0:
                            if (numberOfScene != cell.getNumericCellValue()) {
                                numberOfScene = cell.getNumericCellValue();
                                scenes.add(tempScene);
                                tempScene = new Scene();
                            }
                            break;
                        case 1:
                            setSceneTrigger(tempScene, cell);
                            break;
                        case 2:
                            tempReply = new Reply();
                            tempReply.setBotName(cell.getStringCellValue());
                            break;
                        case 3:
                            type = cell.getStringCellValue();
                            break;
                        case 4:
                            setSceneReplyValueByType(tempReply, type, cell);
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

    private void setSceneReplyValueByType(Reply tempReply, String type, Cell cell) {
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
        if(type.equals("pinned")) {
            setPinnedMessageToReply(tempReply, cell);
        }
    }

    private void setPinnedMessageToReply(Reply tempReply, Cell cell) {
        PinnedMessage pinnedMessage = new PinnedMessage();
        String value = cell.getStringCellValue();
        if (value.contains("picture")) {
            long pictureId = pictureService.setPictureIntoDB(value);
            pinnedMessage.setPictureValue(pictureId);
        } else {
            pinnedMessage.setTextValue(value);
        }
        tempReply.setPinnedMessage(pinnedMessage);
    }

    private void setSceneTrigger(Scene tempScene, Cell cell) {
        if (cell.getCellType() == CellType.STRING ||
                (cell.getCellType() == CellType.NUMERIC && cell.getNumericCellValue() != 0.0)) {
            Trigger newTrigger = new Trigger();
            if (cell.getCellType() == CellType.STRING) {
                if (cell.getStringCellValue().equals("picture")) {
                    newTrigger.setHasPicture(true);
                }
                if (cell.getStringCellValue().contains("geolocation")) {
                    String triggerToDB = cell.getStringCellValue().replace("geolocation, ", "");
                    Geolocation geolocationToDB = geolocationSpliterator(triggerToDB);
                    geolocationDatabaseService.setGeolocationIntoDB(geolocationToDB);
                    newTrigger.setGeolocationId(1L);
                } else {
                    newTrigger.setText(cell.getStringCellValue());
                }
            }
            if (cell.getCellType() == CellType.NUMERIC)
                newTrigger.setText(String.valueOf(cell.getNumericCellValue()));
            tempScene.setTrigger(newTrigger);
        }
    }

    public Geolocation geolocationSpliterator(String geolocationString) {
        Geolocation geolocation = new Geolocation();
        String[] array = geolocationString.trim().split(", ");
        geolocation.setLatitude(Double.valueOf(array[1]));
        geolocation.setLongitude(Double.valueOf(array[2]));
        geolocation.setFullName(array[0]);
        return geolocation;
    }

}
