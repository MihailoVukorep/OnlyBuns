package com.onlybuns.BunnyBuzz.config.messaging;

import com.onlybuns.BunnyBuzz.utils.PostAdvertisingConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostRabbitMQConfig {

    @Bean
    public Queue createQueue() {
        return new Queue(PostAdvertisingConstants.QUEUE_CREATE);
    }

    @Bean
    public FanoutExchange exchange() {
        return new FanoutExchange (PostAdvertisingConstants.EXCHANGE);
    }

    @Bean
    public Binding createPostBinding() {
        return BindingBuilder.bind(createQueue())
                .to(exchange());
    }
}
