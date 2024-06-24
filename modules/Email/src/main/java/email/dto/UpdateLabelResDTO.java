package email.dto;
import lombok.Data;
import java.util.List;

@Data
public class UpdateLabelResDTO {
    private String id;
    private String threadId;
    private List<String> labelIds;
}