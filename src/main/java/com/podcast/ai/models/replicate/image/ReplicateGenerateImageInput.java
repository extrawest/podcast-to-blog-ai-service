package com.podcast.ai.models.replicate.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Actual structure for 'bytedance/sdxl-lightning-4step' model
 */
@ToString(callSuper = true)
@NoArgsConstructor
@Data
public class ReplicateGenerateImageInput {
    private String prompt;
    private Integer width = 1024;
    private Integer height = 1024;
    private String scheduler = "K_EULER";
    @JsonProperty("num_outputs")
    private Integer numOutputs = 1;
    @JsonProperty("guidance_scale")
    private Integer guidanceScale = 0;
    @JsonProperty("negative_prompt")
    private String negativePrompt = "worst quality, low quality";
    @JsonProperty("num_inference_steps")
    private Integer numInferenceSteps = 4;

    public ReplicateGenerateImageInput(String prompt) {
        this.prompt = prompt;
    }
}
