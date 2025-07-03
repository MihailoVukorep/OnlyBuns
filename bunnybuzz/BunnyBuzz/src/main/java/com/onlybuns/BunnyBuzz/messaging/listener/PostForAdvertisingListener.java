package com.onlybuns.BunnyBuzz.messaging.listener;

import com.onlybuns.BunnyBuzz.messaging.message.PostReceivedMessage;
import com.onlybuns.BunnyBuzz.utils.PostAdvertisingConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostForAdvertisingListener {

    @RabbitListener(queues = PostAdvertisingConstants.QUEUE_CREATE)
    public void handleAdvertisePost(PostReceivedMessage postForAdvertising) {
        log.info(String.format("Successfully received post for advertising message: TEXT - [%s], DATE AND TIME - [%s], USERNAME - [%s]", postForAdvertising.getText(), postForAdvertising.getCreatedDate(), postForAdvertising.getUserName()));
    }
}
