package email.dto;

import email.dto.GmailDTO.GmailDetail;
import email.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private List<LabelDTO> labels;

}
