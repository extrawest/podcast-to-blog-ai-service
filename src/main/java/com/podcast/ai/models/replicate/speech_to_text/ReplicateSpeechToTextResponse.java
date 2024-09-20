package com.podcast.ai.models.replicate.speech_to_text;

import com.podcast.ai.models.replicate.common.ReplicateResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Actual structure for 'openai/whisper' model
 */
@ToString(callSuper = true)
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ReplicateSpeechToTextResponse extends ReplicateResponse {
    private ReplicateSpeechToTextInput input;
    private ReplicateSpeechToTextOutput output;
}
