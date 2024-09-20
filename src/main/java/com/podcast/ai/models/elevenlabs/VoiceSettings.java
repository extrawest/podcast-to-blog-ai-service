package com.podcast.ai.models.elevenlabs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class VoiceSettings {
    private double stability = 0.5;
    @JsonProperty("similarity_boost")
    private double similarityBoost = 0.5;
}
