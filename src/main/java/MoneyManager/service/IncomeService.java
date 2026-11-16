package MoneyManager.service;

import MoneyManager.payload.IncomeDTO;
import MoneyManager.payload.IncomeDTOResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeService {
    IncomeDTO createIncome(IncomeDTO incomeDTO);
    IncomeDTOResponse getAllIncomeForGivenDate(LocalDate fromDate, LocalDate toDate);
    IncomeDTO deleteIncome(Long expenseId);
    List<IncomeDTO> getLatest5Incomes();
    BigDecimal getTotalIncomeAmount();
    BigDecimal getTotalIncomeAmountForSelectedDate(LocalDate fromDate, LocalDate tillDate);
    List<IncomeDTO> filterIncomes(LocalDate fromDate, LocalDate endDate, String keyword, String sortBy, String sortOrder);
}
