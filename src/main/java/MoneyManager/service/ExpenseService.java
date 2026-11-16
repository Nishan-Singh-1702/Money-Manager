package MoneyManager.service;

import MoneyManager.model.Expense;
import MoneyManager.payload.ExpenseDTO;
import MoneyManager.payload.ExpenseDTOResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseDTO createExpense(ExpenseDTO expenseDTO);
    ExpenseDTOResponse getAllExpenseForGivenDate(LocalDate fromDate, LocalDate toDate);
    ExpenseDTO deleteExpense(Long expenseId);
    List<ExpenseDTO> getLatest5Expenses();
    BigDecimal getTotalExpenseAmount();
    BigDecimal getTotalExpenseAmountForSelectedDate(LocalDate fromDate, LocalDate tillDate);
    List<ExpenseDTO> filterExpenses(LocalDate fromDate, LocalDate endDate, String keyword, String sortBy, String sortOrder);

}
