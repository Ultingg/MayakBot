package ru.kumkuat.application.GameModule.Collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class ReplyCollection {


    private List<Reply> replyList;

    public Reply getReply(Long id) {
        return replyList.get(id.intValue());
    }

    public boolean setReplay(Reply replay) {
        return replyList.add(replay);
    }

    public void deleteReplay(Long id) {
        replyList.remove(id.intValue());
    }

    public int size() {
        return replyList.size();
    }

    public boolean addAll(Collection<Reply> collection) {
        return replyList.addAll(collection);
    }

    @PostConstruct
    public void collectionInit() {
        replyList = new ArrayList<>();

        Reply reply = Reply.builder().id(1L).textMessage("Здравствуй, как здорово что ты наконец-то в Петербурге!").build();
        Reply reply1 = Reply.builder().id(2L).textMessage("Перейди дорогу через светофор").build();


        replyList = Arrays.asList(reply, reply1);

    }

}
