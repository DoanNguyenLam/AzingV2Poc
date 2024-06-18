package email.dto;

import lombok.Data;

import java.util.List;

@Data
public class LabelResponseDTO {
    private List<LabelDTO> labels;

}
