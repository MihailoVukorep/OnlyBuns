package com.onlybuns.BunnyBuzz.messaging.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostReceivedMessage implements Serializable{
        private Long id;

        private String text;

        private LocalDateTime createdDate;

        private String userName;
}
