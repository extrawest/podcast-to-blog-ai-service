package com.podcast.ai.models.replicate.text;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Actual structure for 'meta-llama-3-70b-instruct' model
 */
@ToString
@NoArgsConstructor
@Data
public class ReplicateTextChatInput {
    private String prompt;
    @JsonProperty("top_k")
    private Double topK = 0.0;
    @JsonProperty("top_p")
    private Double topP = 0.9;
    @JsonProperty("max_tokens")
    private Integer maxTokens = 512;
    @JsonProperty("min_tokens")
    private Integer minTokens = 0;
    private Double temperature = 0.6;
    @JsonProperty("system_prompt")
    private String systemPrompt = "You are a helpful assistant";
    @JsonProperty("length_penalty")
    private Integer lengthPenalty = 1;
    @JsonProperty("stop_sequences")
    private String stopSequences = "<|end_of_text|>,<|eot_id|>";
    @JsonProperty("prompt_template")
    private String promptTemplate = "<|begin_of_text|><|start_header_id|>system<|end_header_id|>\\n\\nYou are a helpful assistant<|eot_id|><|start_header_id|>user<|end_header_id|>\\n\\n{prompt}<|eot_id|><|start_header_id|>assistant<|end_header_id|>\\n\\n";
    @JsonProperty("presence_penalty")
    private Double presencePenalty = 1.15;
    @JsonProperty("log_performance_metrics")
    private Boolean logPerformanceMetrics = false;

    public ReplicateTextChatInput(String prompt) {
        this.prompt = prompt;
    }
}
