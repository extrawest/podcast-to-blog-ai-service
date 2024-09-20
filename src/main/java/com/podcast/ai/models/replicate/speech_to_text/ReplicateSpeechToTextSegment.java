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
public class ReplicateSpeechToTextSegment {
    private Integer id;
    private Double end;
    private Double seek;
    private String text;
    private Double start;
    private List<Integer> tokens;
    @JsonProperty("avg_logprob")
    private Double avgLogprob;
    private Double temperature;
    @JsonProperty("no_speech_prob")
    private Double noSpeechProb;
    @JsonProperty("compression_ratio")
    private Double compressionRatio;
}
