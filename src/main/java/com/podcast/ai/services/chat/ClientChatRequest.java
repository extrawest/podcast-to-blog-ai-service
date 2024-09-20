package com.podcast.ai.services.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ClientChatRequest {
    private String podcastGuid;
    private String userMsg;
    private boolean newChatThread;
}