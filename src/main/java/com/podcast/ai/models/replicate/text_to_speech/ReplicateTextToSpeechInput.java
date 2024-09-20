package com.podcast.ai.models.replicate.text_to_speech;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Actual structure for 'lucataco/xtts-v2' model
 */
@ToString(callSuper = true)
@NoArgsConstructor
@Data
public class ReplicateTextToSpeechInput {
    private String text;
    private String speaker = "https://replicate.delivery/pbxt/Jt79w0xsT64R1JsiJ0LQRL8UcWspg5J4RFrU6YwEKpOT1ukS/male.wav";
    private String language = "en";
    @JsonProperty("cleanup_voice")
    private Boolean cleanupVoice = false;

    public ReplicateTextToSpeechInput(String text) {
        this.text = text;
    }
}
