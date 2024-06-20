package email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateLabelReqDTO {
    private List<String> addLabelIds;
    private List<String> removeLabelIds;
}
