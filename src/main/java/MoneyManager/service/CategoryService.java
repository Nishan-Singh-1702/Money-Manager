package MoneyManager.service;

import MoneyManager.payload.CategoryDTO;
import MoneyManager.payload.CategoryDTOResponse;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTOResponse getLoggedInProfileCategories();
    CategoryDTOResponse getCategoriesByTypeForLoggedInProfile(String categoryType);
    CategoryDTO updateCategoryForLoggedInProfile(Long categoryId, CategoryDTO categoryDTO);
}
