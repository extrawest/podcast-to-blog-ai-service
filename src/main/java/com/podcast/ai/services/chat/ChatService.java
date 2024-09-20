package com.podcast.ai.services.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {
    private final AdvancedRagService advancedRagService;

    public String advancedRag(ClientChatRequest chatRequest) {
        return advancedRagService.generateAnswer(chatRequest);
    }
}
