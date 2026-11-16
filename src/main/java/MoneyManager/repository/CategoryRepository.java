package MoneyManager.repository;

import MoneyManager.model.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Boolean existsByCategoryNameIgnoreCaseAndProfile_ProfileId(@NotBlank(message = "Category name should not be blank") String categoryName, Long profileId);

    List<Category> findByProfile_ProfileId(Long profileId);

    Optional<List<Category>> findByCategoryTypeIgnoreCaseAndProfile_ProfileId(String categoryType, Long profileId);

    Optional<Category> findByCategoryIdAndProfile_ProfileId(Long categoryId, Long profileId);

    Optional<Category> findByCategoryNameIgnoreCaseAndProfile_ProfileId(@NotBlank(message = "Category name should not be blank") String categoryName, Long profileId);
}
