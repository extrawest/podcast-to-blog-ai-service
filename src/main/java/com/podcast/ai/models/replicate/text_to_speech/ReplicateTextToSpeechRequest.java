package com.podcast.ai.models.replicate.text_to_speech;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Actual structure for 'lucataco/xtts-v2' model
 */
@ToString(callSuper = true)
@NoArgsConstructor
@Data
public class ReplicateTextToSpeechRequest {
    private String version = "684bc3855b37866c0c65add2ff39c78f3dea3f4ff103a436465326e0f438d55e";
    private ReplicateTextToSpeechInput input;

    public ReplicateTextToSpeechRequest(String text) {
        this.input = new ReplicateTextToSpeechInput(text);
    }
}
