package com.podcast.ai.models.replicate.text;

import com.podcast.ai.models.replicate.common.ReplicateResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Actual structure for 'meta-llama-3-70b-instruct' model
 */
@ToString(callSuper = true)
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ReplicateTextChatResponse extends ReplicateResponse {
    private ReplicateTextChatInput input;
    private List<String> output;
}
