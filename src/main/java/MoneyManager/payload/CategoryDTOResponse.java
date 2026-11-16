package MoneyManager.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTOResponse {
    private List<CategoryDTO> response;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElement;
    private Integer totalPage;
    private Boolean lastPage;
}
