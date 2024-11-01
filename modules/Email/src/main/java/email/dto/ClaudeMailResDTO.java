package email.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClaudeMailResDTO {
    private ClaudeMailType type;
    private String content;
}
