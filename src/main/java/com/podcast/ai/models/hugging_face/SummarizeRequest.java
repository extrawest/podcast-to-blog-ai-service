package com.podcast.ai.models.hugging_face;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SummarizeRequest {
    private String inputs;
}
