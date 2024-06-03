package email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ClaudeRequestDTO {
    @JsonProperty("model")
    private String model = "claude-3-opus-20240229";

    @JsonProperty("max_tokens")
    private int maxTokens = 4096;

    @JsonProperty("messages")
    private List<Message> messages;

    public ClaudeRequestDTO(List<Message> messages) {
     this.messages = messages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private String content;
    }
}
