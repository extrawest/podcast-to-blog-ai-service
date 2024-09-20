package com.podcast.ai.models.replicate.speech_to_text;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Actual structure for 'openai/whisper' model
 */
@ToString
@NoArgsConstructor
@Data
public class ReplicateSpeechToTextOutput {
    private List<ReplicateSpeechToTextSegment> segments;
    private String translation;
    private String transcription;
    @JsonProperty("detected_language")
    private String detectedLanguage;
}
