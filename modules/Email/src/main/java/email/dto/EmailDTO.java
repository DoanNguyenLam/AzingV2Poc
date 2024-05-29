package email.dto;

import lombok.Getter;
import lombok.Setter;

public class EmailDTO {
    @Getter
    @Setter
    private String subject;

    @Getter
    @Setter
    private String data;

    @Getter
    @Setter
    private String date;
}
