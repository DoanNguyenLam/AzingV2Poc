package email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ClaudeResponseDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("role")
    private String role;

    @JsonProperty("model")
    private String model;

    @JsonProperty("content")
    private List<Content> content;

    @JsonProperty("stop_reason")
    private String stopReason;

    @JsonProperty("stop_sequence")
    private Object stopSequence;

    @JsonProperty("usage")
    private Usage usage;

    @Data
    public static class Content {

        @JsonProperty("type")
        private String type;

        @JsonProperty("text")
        private String text;
    }

    @Data
    public static class Usage {

        @JsonProperty("input_tokens")
        private int inputTokens;

        @JsonProperty("output_tokens")
        private int outputTokens;
    }
}
