package ru.kumkuat.application.GameModule.Collections;


import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Setter
@Getter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class Scene {

    private Long id;
    private List<Reply> replyCollection;
    private Trigger trigger;




}
