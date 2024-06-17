package email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GPTRequestDTO {
    @JsonProperty("model")
    private String model = "gpt-3.5-turbo";

    @JsonProperty("messages")
    private List<Message> messages;

    public GPTRequestDTO(List<Message> messages) {
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
