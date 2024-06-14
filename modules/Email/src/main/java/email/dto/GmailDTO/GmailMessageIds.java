package email.dto.GmailDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GmailMessageIds {

    @JsonProperty("messages")
    private List<GmailMessage> gmailMessageList;

    @Data
    public static class GmailMessage{
        @JsonProperty("id")
        private String id;

        @JsonProperty("threadId")
        private String threadId;
    }
}
