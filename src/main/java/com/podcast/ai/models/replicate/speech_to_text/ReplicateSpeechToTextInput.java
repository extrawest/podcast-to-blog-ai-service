package com.podcast.ai.models.replicate.speech_to_text;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Actual structure for 'openai/whisper' model
 */
@ToString
@NoArgsConstructor
@Data
public class ReplicateSpeechToTextInput {
    private String audio;
    private String model = "large-v3";
    private String language = "auto";
    private Boolean translate = false;
    private Integer temperature = 0;
    private String transcription = "plain text";
    @JsonProperty("suppress_tokens")
    private String suppressTokens = "-1";
    @JsonProperty("logprob_threshold")
    private Integer logprobThreshold = -1;
    @JsonProperty("no_speech_threshold")
    private Double noSpeechThreshold = 0.6;
    @JsonProperty("condition_on_previous_text")
    private Boolean conditionOnPreviousText = true;
    @JsonProperty("compression_ratio_threshold")
    private Double compressionRatioThreshold = 2.4;
    @JsonProperty("temperature_increment_on_fallback")
    private Double temperatureIncrementOnFallback = 0.2;

    public ReplicateSpeechToTextInput(String audio) {
        this.audio = audio;
    }
}
