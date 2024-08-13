package ru.kumkuat.application.gameModule.service.resources;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Кэш класс чтобы быстрее работать с фалйами.
 */
@Component
public class FileCacheService {


    private Map<Long, InputFile> fileCache = new HashMap<>();

    public boolean isFileInCache(Long id){
        return fileCache.containsKey(id);
    }
    public void putFile(Long id, InputFile file){
        fileCache.put(id, file);
    }

    public InputFile getFile(Long id){
        return fileCache.get(id);
    }
}
