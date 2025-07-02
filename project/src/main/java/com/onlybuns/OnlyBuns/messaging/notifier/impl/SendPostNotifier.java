package com.onlybuns.OnlyBuns.messaging.notifier.impl;

import com.onlybuns.OnlyBuns.messaging.message.PostSendMessage;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.messaging.notifier.ISendPostNotifier;
import com.onlybuns.OnlyBuns.util.constants.SendPostConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendPostNotifier implements ISendPostNotifier {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void postSendMessage(Post post) {
        PostSendMessage message = new PostSendMessage(post.getId(), post.getText(), post.getCreatedDate(), post.getAccount().getUserName());

        log.info(String.format("Sending post to advertising agencies: TEXT - [%s], DATE AND TIME - [%s], USERNAME - [%s]", message.getText(),message.getCreatedDate(), message.getUserName()));

        rabbitTemplate.convertAndSend(SendPostConstants.EXCHANGE, SendPostConstants.ROUTING_KEY_CREATE, message);
    }
}
