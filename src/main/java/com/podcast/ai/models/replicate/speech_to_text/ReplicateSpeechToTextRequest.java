package com.podcast.ai.models.replicate.speech_to_text;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Actual structure for 'openai/whisper' model
 */
@ToString
@NoArgsConstructor
@Data
public class ReplicateSpeechToTextRequest {
    private String version = "cdd97b257f93cb89dede1c7584e3f3dfc969571b357dbcee08e793740bedd854";
    private ReplicateSpeechToTextInput input;

    public ReplicateSpeechToTextRequest(String audioFilePath) {
        this.input = new ReplicateSpeechToTextInput(audioFilePath);
    }
}
