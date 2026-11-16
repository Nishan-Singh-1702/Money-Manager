package MoneyManager.service;

import MoneyManager.exception.APIException;
import MoneyManager.exception.ResourceNotFoundException;
import MoneyManager.model.Category;
import MoneyManager.model.Expense;
import MoneyManager.payload.ExpenseDTO;
import MoneyManager.payload.ExpenseDTOResponse;
import MoneyManager.repository.CategoryRepository;
import MoneyManager.repository.ExpenseRepository;
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
public class ExpenseServiceImpl implements ExpenseService{
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProfileRepository profileRepository;

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "expenseId", "expenseId",
            "expenseName", "expenseName",
            "expenseAmount", "expenseAmount",
            "categoryName", "category.categoryName",
            "date", "date");

    @Override
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No profile is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

//        List<Category> categoryList = categoryRepository.findByProfile_ProfileId(profileId);
//        Category categoryFromDb = categoryList.stream()
//                .filter(category -> category.getCategoryName().equalsIgnoreCase(expenseDTO.getCategoryName()))
//                .findFirst()
//                .orElseThrow(()->new ResourceNotFoundException("Category","categoryName",expenseDTO.getCategoryName()));

        Category categoryFromDb = categoryRepository.findByCategoryNameIgnoreCaseAndProfile_ProfileId(expenseDTO.getCategoryName(),profileId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryName",expenseDTO.getCategoryName()));

        if(!"Expense".equalsIgnoreCase(categoryFromDb.getCategoryType())) {
            throw new APIException("Selected category is not an Expense category !!");
        }

        Expense newExpense = modelMapper.map(expenseDTO, Expense.class);
        newExpense.setExpenseId(null);
        newExpense.setCategory(categoryFromDb);
        newExpense.setProfile(profileRepository.getReferenceById(profileId));
        return modelMapper.map(expenseRepository.save(newExpense), ExpenseDTO.class);
    }

    @Override
    public ExpenseDTOResponse getAllExpenseForGivenDate(LocalDate fromDate, LocalDate toDate){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        LocalDate to = toDate==null ? LocalDate.now(): toDate;
        if(fromDate.isAfter(to)){
            throw new APIException("startDate cannot be after endDate");
        }

        List<Expense> expenses = expenseRepository.findByProfile_ProfileIdAndDateBetweenOrderByDateDesc(profileId, fromDate, to);
        List<ExpenseDTO> expenseDTOS = expenses.stream().map(expense->modelMapper.map(expense,ExpenseDTO.class)).toList();
        ExpenseDTOResponse response = new ExpenseDTOResponse();
        response.setResponse(expenseDTOS);
        return response;
    }

    @Override
    public ExpenseDTO deleteExpense(Long expenseId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        Expense expenseFromDb = expenseRepository.findByProfile_ProfileIdAndExpenseId(profileId,expenseId)
                .orElseThrow(()->new ResourceNotFoundException("Expense","expenseId",expenseId));

        ExpenseDTO deletedExpense = modelMapper.map(expenseFromDb,ExpenseDTO.class);
        expenseRepository.delete(expenseFromDb);
        return deletedExpense;
    }

    public List<ExpenseDTO> getLatest5Expenses(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        List<Expense> expenses = expenseRepository.findTop5ByProfile_ProfileIdOrderByCreatedAtDesc(profileId);
        return expenses.stream().map(expense -> modelMapper.map(expense,ExpenseDTO.class)).toList();
    }

    @Override
    public BigDecimal getTotalExpenseAmount(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        BigDecimal totalExpenseAmount = expenseRepository.findTotalExpenseAmountByProfileId(profileId);
        return totalExpenseAmount!=null ? totalExpenseAmount: BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalExpenseAmountForSelectedDate(LocalDate fromDate, LocalDate tillDate){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        LocalDate toDate = tillDate==null ? LocalDate.now(): tillDate;

        if(fromDate.isAfter(toDate)){
            throw new APIException("startDate cannot be after endDate");
        }

        BigDecimal totalExpenseAmount = expenseRepository.findTotalExpenseAmountByProfileIdAndDateBetween(profileId,fromDate, toDate);
        return totalExpenseAmount!=null ? totalExpenseAmount: BigDecimal.ZERO;
    }



    @Override
    public List<ExpenseDTO> filterExpenses(LocalDate fromDate, LocalDate endDate, String keyword, String sortBy, String sortOrder){

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
            throw new APIException("sortBy should be one of: expenseId, expenseName, expenseAmount, categoryName, date");
        }
        if(!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc")){
            throw new APIException("sortOrder should be asc or desc");
        }

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        String searchKeyword = keyword == null ? "" : keyword.trim();

        List<Expense> expenses = expenseRepository
                .findByProfile_ProfileIdAndDateBetweenAndExpenseNameContainingIgnoreCase(
                        profileId, fromDate, tillDate, searchKeyword, sort);

        return expenses.stream().map(expense -> modelMapper.map(expense, ExpenseDTO.class)).toList();
    }
}
