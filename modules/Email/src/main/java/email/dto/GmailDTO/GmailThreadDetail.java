package email.dto.GmailDTO;

import lombok.Data;

import java.util.List;

@Data
public class GmailThreadDetail {
    List<GmailDetail> messages;
}
