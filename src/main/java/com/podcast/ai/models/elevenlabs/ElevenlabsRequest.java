package com.podcast.ai.models.elevenlabs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ElevenlabsRequest {
    private String text;
    @JsonProperty("model_id")
    private String modelId = "eleven_monolingual_v1";
    @JsonProperty("voice_settings")
    private VoiceSettings voiceSettings = new VoiceSettings();

    public ElevenlabsRequest(String text) {
        this.text = text;
    }
}
