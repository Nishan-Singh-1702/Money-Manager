package MoneyManager.service;

import MoneyManager.exception.APIException;
import MoneyManager.exception.ResourceNotFoundException;
import MoneyManager.model.Category;
import MoneyManager.payload.CategoryDTO;
import MoneyManager.payload.CategoryDTOResponse;
import MoneyManager.repository.CategoryRepository;
import MoneyManager.repository.ProfileRepository;
import MoneyManager.security.service.UserDetailsImpl;
import org.hibernate.annotations.CreationTimestamp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }

        Long profileId = userDetails.getProfileId();

        if(categoryRepository.existsByCategoryNameIgnoreCaseAndProfile_ProfileId(categoryDTO.getCategoryName(),profileId)){
            throw new APIException("Category already exists !!");
        }
        Category newCategory = modelMapper.map(categoryDTO,Category.class);
        newCategory.setProfile(profileRepository.getReferenceById(profileId));

        return modelMapper.map(categoryRepository.save(newCategory),CategoryDTO.class);
    }

    @Override
    public CategoryDTOResponse getLoggedInProfileCategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        List<Category> categories = categoryRepository.findByProfile_ProfileId(profileId);
        List<CategoryDTO>categoryDTOS = categories.stream().map(category -> modelMapper.map(category,CategoryDTO.class)).toList();
        CategoryDTOResponse response = new CategoryDTOResponse();
        response.setResponse(categoryDTOS);

        return response;
    }

    @Override
    public CategoryDTOResponse getCategoriesByTypeForLoggedInProfile(String categoryType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        List<Category> categories = categoryRepository.findByCategoryTypeIgnoreCaseAndProfile_ProfileId(categoryType, profileId)
                .orElseThrow(()->new ResourceNotFoundException("Mo category found for type: "+categoryType));

        List<CategoryDTO> categoryDTOS = categories.stream().map(category -> modelMapper.map(category,CategoryDTO.class)).toList();
        CategoryDTOResponse response = new CategoryDTOResponse();
        response.setResponse(categoryDTOS);
        return response;
    }

    @Override
    public CategoryDTO updateCategoryForLoggedInProfile(Long categoryId, CategoryDTO categoryDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        Category categoryFromDb = categoryRepository.findByCategoryIdAndProfile_ProfileId(categoryId, profileId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryName", categoryDTO.getCategoryName()));

        categoryFromDb.setCategoryName(categoryDTO.getCategoryName());
        categoryFromDb.setCategoryType(categoryDTO.getCategoryType());
        categoryFromDb.setCategoryIcon(categoryDTO.getCategoryIcon());

        return modelMapper.map(categoryRepository.save(categoryFromDb),CategoryDTO.class);
    }
}
