package ru.kumkuat.application.GameModule.Collections;


import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Getter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class Scene {
    private int id;
    private int replyCollectionId;
    private String trigger;
}
