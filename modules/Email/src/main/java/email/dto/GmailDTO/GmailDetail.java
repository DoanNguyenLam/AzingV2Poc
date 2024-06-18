package email.dto.GmailDTO;

import lombok.Data;

import java.util.List;

@Data
public class GmailDetail {
    private String id;
    private String threadId;
    private String snippet;
    private Payload payload;
    private String[] labelIds;

    @Data
    public static class Payload{
        private List<Headers> headers;
        private List<Parts> parts;
        private Body body;
    }

    @Data
    public static class Parts{
        private String partId;
        private String mimeType;
        private List<Headers> headers;
        private Body body;

    }

    @Data
    public static class Headers{
        private String name;
        private String value;
    }

    @Data
    public static class Body{
        private Integer size;
        private String data;
    }
}
