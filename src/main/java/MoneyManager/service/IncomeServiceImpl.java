package MoneyManager.service;

import MoneyManager.exception.APIException;
import MoneyManager.exception.ResourceNotFoundException;
import MoneyManager.model.Category;
import MoneyManager.model.Income;
import MoneyManager.payload.IncomeDTO;
import MoneyManager.payload.IncomeDTOResponse;
import MoneyManager.repository.CategoryRepository;
import MoneyManager.repository.IncomeRepository;
import MoneyManager.repository.ProfileRepository;
import MoneyManager.security.service.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class IncomeServiceImpl implements IncomeService{
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    IncomeRepository incomeRepository;

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "incomeId", "incomeId",
            "incomeName", "incomeName",
            "incomeAmount", "incomeAmount",
            "categoryName", "category.categoryName",
            "date", "date");

    public IncomeDTO createIncome(IncomeDTO incomeDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No Profile is logged in !!");
        }

        Long profileId = userDetails.getProfileId();

        Category categoryFromDb = categoryRepository.findByCategoryNameIgnoreCaseAndProfile_ProfileId(incomeDTO.getCategoryName(),profileId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryName",incomeDTO.getCategoryName()));

        if(!"Income".equalsIgnoreCase(categoryFromDb.getCategoryType())){
            throw new APIException("Selected category is not an Income category !!");
        }

        Income newIncome = modelMapper.map(incomeDTO, Income.class);
        newIncome.setIncomeId(null);
        newIncome.setCategory(categoryFromDb);
        newIncome.setProfile(profileRepository.getReferenceById(profileId));
        return modelMapper.map(incomeRepository.save(newIncome),IncomeDTO.class);
    }

    public IncomeDTOResponse getAllIncomeForGivenDate(LocalDate fromDate, LocalDate toDate){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        LocalDate to = toDate==null ? LocalDate.now(): toDate;
        if(fromDate.isAfter(to)){
            throw new APIException("startDate cannot be after endDate");
        }

        List<Income> incomes = incomeRepository.findByProfile_ProfileIdAndDateBetweenOrderByDateDesc(profileId,fromDate, to);
        List<IncomeDTO> incomeDTOS = incomes.stream().map(income -> modelMapper.map(income,IncomeDTO.class)).toList();
        IncomeDTOResponse response = new IncomeDTOResponse();
        response.setResponse(incomeDTOS);
        return response;
    }

    @Override
    public IncomeDTO deleteIncome(Long incomeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();
        Income incomeFormDb = incomeRepository.findByProfile_ProfileIdAndIncomeId(profileId, incomeId)
                .orElseThrow(()->new ResourceNotFoundException("Income","incomeId",incomeId));
        IncomeDTO deletedIncome = modelMapper.map(incomeFormDb,IncomeDTO.class);
        incomeRepository.delete(incomeFormDb);
        return deletedIncome;
    }

    @Override
    public List<IncomeDTO> getLatest5Incomes(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        List<Income> incomes = incomeRepository.findTop5ByProfile_ProfileIdOrderByCreatedAtDesc(profileId);
        return incomes.stream().map(income -> modelMapper.map(income,IncomeDTO.class)).toList();
    }

    @Override
    public BigDecimal getTotalIncomeAmount(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        BigDecimal totalIncomeAmount = incomeRepository.findTotalIncomeAmountByProfileId(profileId);
        return totalIncomeAmount!=null ? totalIncomeAmount: BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalIncomeAmountForSelectedDate(LocalDate fromDate, LocalDate tillDate){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        LocalDate toDate = tillDate==null ? LocalDate.now(): tillDate;
        if(fromDate.isAfter(toDate)){
            throw new APIException("startDate cannot be after endDate");
        }

        BigDecimal totalIncomeAmount = incomeRepository.findTotalIncomeAmountByProfileIdAndDateBetween(profileId,fromDate, toDate);
        return totalIncomeAmount!=null ? totalIncomeAmount: BigDecimal.ZERO;
    }

    @Override
    public List<IncomeDTO> filterIncomes(LocalDate fromDate, LocalDate endDate, String keyword, String sortBy, String sortOrder){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        LocalDate tillDate = endDate == null ? LocalDate.now() : endDate;
        if(fromDate.isAfter(tillDate)){
            throw new APIException("fromDate cannot be after endDate");
        }

        String sortField = SORT_FIELDS.get(sortBy);
        if(sortField == null){
            throw new APIException("sortBy should be one of: incomeId, incomeName, incomeAmount, categoryName, date");
        }
        if(!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc")){
            throw new APIException("sortOrder should be asc or desc");
        }

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        String searchKeyword = keyword == null ? "" : keyword.trim();

        List<Income> incomes = incomeRepository
                .findByProfile_ProfileIdAndDateBetweenAndIncomeNameContainingIgnoreCase(
                        profileId, fromDate, tillDate, searchKeyword, sort);

        return incomes.stream().map(income -> modelMapper.map(income, IncomeDTO.class)).toList();
    }
}
