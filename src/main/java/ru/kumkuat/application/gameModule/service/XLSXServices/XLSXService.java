package ru.kumkuat.application.gameModule.service.XLSXServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class XLSXService {
    private static final String TIME_PAD_PATH = "../resources/input_bg_usersti2.xlsx";
    private final XLSXTimePadListReaderService timePadReader;

    final Logger logger = LoggerFactory.getLogger(XLSXService.class);

    public XLSXService(XLSXTimePadListReaderService timePadReader) {
        this.timePadReader = timePadReader;
    }

    public void parseTimePadFile() {
        timePadReader.fillHeaderProperty();
        try {
            timePadReader.XLSXBGParser(TIME_PAD_PATH);
        } catch (Exception e) {
            logger.error("Error while parse timepad: " + e.getMessage());
        }
    }
}
