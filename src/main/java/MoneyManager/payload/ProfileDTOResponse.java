package MoneyManager.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTOResponse {
    private List<ProfileDTO> response;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElement;
    private Integer totalPage;
    private Boolean lastPage;
}
