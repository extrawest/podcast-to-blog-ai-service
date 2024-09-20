package com.podcast.ai.models.replicate.text;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Actual structure for 'meta-llama-3-70b-instruct' model
 */
@ToString
@NoArgsConstructor
@Data
public class ReplicateTextChatRequest {
    private ReplicateTextChatInput input;

    public ReplicateTextChatRequest(String prompt) {
        this.input = new ReplicateTextChatInput(prompt);
    }
}
