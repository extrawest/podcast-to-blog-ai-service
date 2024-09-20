package com.podcast.ai.models.replicate.text;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReplicateTextChatMessage {
    private String title;
    private String summary;
}
