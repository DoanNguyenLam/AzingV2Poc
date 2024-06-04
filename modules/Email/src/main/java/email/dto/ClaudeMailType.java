package email.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClaudeMailType {
    SUMMARY("SUMMARY", "SUMMARY"),
    SUGGESTION("SUGGESTION", "SUGGESTION");

    private final String key;
    private final String value;
}
