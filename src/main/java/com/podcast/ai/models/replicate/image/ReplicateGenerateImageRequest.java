package com.podcast.ai.models.replicate.image;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Actual structure for 'bytedance/sdxl-lightning-4step' model
 */
@ToString(callSuper = true)
@NoArgsConstructor
@Data
public class ReplicateGenerateImageRequest {
    private String version = "5599ed30703defd1d160a25a63321b4dec97101d98b4674bcc56e41f62f35637";
    private ReplicateGenerateImageInput input;

    public ReplicateGenerateImageRequest(String prompt) {
        this.input = new ReplicateGenerateImageInput(prompt);
    }
}
