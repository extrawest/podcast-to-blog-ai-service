package com.podcast.ai.models.replicate.image;

import com.podcast.ai.models.replicate.common.ReplicateResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Actual structure for 'bytedance/sdxl-lightning-4step' model
 */
@ToString(callSuper = true)
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ReplicateGenerateImageResponse extends ReplicateResponse {
    private ReplicateGenerateImageInput input;
    private List<String> output;
}
