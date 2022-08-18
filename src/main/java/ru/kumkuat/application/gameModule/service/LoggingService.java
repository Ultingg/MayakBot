package ru.kumkuat.application.gameModule.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingService {
    public void dumpInfo(String message, Object object) {
      if(log.isInfoEnabled()) {
          try {
              log.info(message, new ObjectMapper().writeValueAsString(object));
          } catch (JsonProcessingException e) {
              log.info("Exception while LoggingService serializing to dumpinfo {}", object);
              log.info(e.getMessage());
          }
      }
    }
}
