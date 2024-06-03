package email.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDTO {

    private String subject;

    private String data;

    private String date;

    private Integer threadId;
    private Integer id;
    private String snippet;
    private String internalDate;
    private String bodyText;
    private String bodyHtml;
}
