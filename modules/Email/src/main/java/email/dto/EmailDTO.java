package email.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDTO {
    private String id;
    private String threadId;
    private String subject;
    private String snippet;
    private String sendFrom;
    private String sendTo;
    private String date;
    private String bodyPlainText;
    private String bodyHtml;
}
