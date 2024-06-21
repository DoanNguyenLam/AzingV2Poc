package email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLabelReqDTO {
    private List<String> addLabelIds;
    private List<String> removeLabelIds;
}
