package MoneyManager.controller;

import MoneyManager.payload.CategoryDTO;
import MoneyManager.payload.CategoryDTOResponse;
import MoneyManager.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @PostMapping("/admin/create")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryDTO));
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryDTOResponse> getLoggedInProfileCategories(){
        return ResponseEntity.ok(categoryService.getLoggedInProfileCategories());
    }

    @GetMapping("/public/categories/categoryType/{categoryType}")
    public ResponseEntity<CategoryDTOResponse> getCategoriesByTypeForLoggedInProfile(@PathVariable String categoryType){
        return ResponseEntity.ok(categoryService.getCategoriesByTypeForLoggedInProfile(categoryType));
    }

    @PutMapping("/public/category/categoryId/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategoryForLoggedInProfile(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId ){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateCategoryForLoggedInProfile(categoryId, categoryDTO));
    }
}
