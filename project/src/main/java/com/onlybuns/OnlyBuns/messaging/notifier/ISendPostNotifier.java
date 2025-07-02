package com.onlybuns.OnlyBuns.messaging.notifier;

import com.onlybuns.OnlyBuns.model.Post;

public interface ISendPostNotifier {

    void postSendMessage(Post post);
}
