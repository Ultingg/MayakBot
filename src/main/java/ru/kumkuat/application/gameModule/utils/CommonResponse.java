package ru.kumkuat.application.gameModule.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@Setter
public class CommonResponse implements Serializable {
    private boolean success;
    private String[] errors;

    public CommonResponse() {
    }

    public CommonResponse(boolean success, String[] errors) {
        this.success = success;
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "success='" + success + '\'' +
                ", errors=" + Arrays.toString(errors) +
                '}';
    }
}
