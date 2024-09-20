package com.podcast.ai.models.replicate.text_to_speech;

import com.podcast.ai.models.replicate.common.ReplicateResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Actual structure for 'lucataco/xtts-v2' model
 */
@ToString(callSuper = true)
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ReplicateTextToSpeechResponse extends ReplicateResponse {
    private ReplicateTextToSpeechInput input;
    private String output;
}
