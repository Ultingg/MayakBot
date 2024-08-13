package ru.kumkuat.application.gameModule.collections;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PinnedMessage {
    private PinnedMessageType type;
    private String text;
    private long pictureId;

    public void setPictureValue(long pictureId) {
        this.setType(PinnedMessageType.PICTURE);
        this.setPictureId(pictureId);
    }
    public void setTextValue(String text) {
        this.setType(PinnedMessageType.TEXT);
        this.setText(text);
    }


    public boolean hasPicture(){
        return pictureId != 0;
    }

    public boolean hasText(){
        return text != null;
    }
}
