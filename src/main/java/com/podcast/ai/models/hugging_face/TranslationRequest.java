package com.podcast.ai.models.hugging_face;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TranslationRequest {
    private String inputs;
}
