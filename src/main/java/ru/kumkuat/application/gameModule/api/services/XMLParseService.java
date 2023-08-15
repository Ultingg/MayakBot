package ru.kumkuat.application.gameModule.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.gameModule.service.GeneralXLSXReader;

@Slf4j
@Service
public class XMLParseService {


    private String path = "../resources/input_bg_usersu.xlsx";


    @Qualifier(value = "timePadOrder")
    private final GeneralXLSXReader xLSXTimePadListReaderService;

    public XMLParseService(GeneralXLSXReader xLSXTimePadListReaderService) {
        this.xLSXTimePadListReaderService = xLSXTimePadListReaderService;
    }

    public String parseXmlFile() {
        log.info("API method parseXMLFile started");
        int amount = 0;
        try {
            xLSXTimePadListReaderService.fillHeaderProperty();
            amount = xLSXTimePadListReaderService.XLSXBGParser(path);
            log.info("API method parseXMLFile finished. Was added " + amount + " users.");
        } catch (Exception e) {
            return e.getMessage();
        }

        return amount > 0 ? "Successfully adedd " + amount + "  users." : "Zero users added;";
    }
}
