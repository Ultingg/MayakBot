package ru.kumkuat.application.gameModule.api.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {

    private String username;
    private Long telegramUserId;
    private Long telegramChatId;
    private String telegramChatLink;
    private boolean hasPayed;
    private boolean isTriggered;
    private boolean isPlaying;
    private int sceneId;
}
