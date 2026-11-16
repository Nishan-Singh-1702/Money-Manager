package MoneyManager.controller;

import MoneyManager.config.AppConstant;
import MoneyManager.payload.ExpenseDTO;
import MoneyManager.payload.ExpenseDTOResponse;
import MoneyManager.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpenseController {
    @Autowired
    ExpenseService expenseService;

    @PostMapping("/public/expense")
    public ResponseEntity<ExpenseDTO> createExpenseAgainstLoggedInProfile(@Valid @RequestBody ExpenseDTO expenseDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.createExpense(expenseDTO));
    }

    @GetMapping("/public/expenses")
    public ResponseEntity<ExpenseDTOResponse> getAllExpenseForGivenDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate){
        return ResponseEntity.ok(expenseService.getAllExpenseForGivenDate(fromDate, toDate));
    }

    @DeleteMapping("/public/expenseId/{expenseId}")
    public ResponseEntity<ExpenseDTO> deleteExpense(@PathVariable Long expenseId){
        return ResponseEntity.ok(expenseService.deleteExpense(expenseId));
    }

    @GetMapping("/public/latest/expenses")
    public ResponseEntity<List<ExpenseDTO>> getLatest5Expenses(){
        return ResponseEntity.ok(expenseService.getLatest5Expenses());
    }

    @GetMapping("/public/expense/totalAmount")
    public ResponseEntity<BigDecimal> getTotalExpenseAmount(){
        return ResponseEntity.ok(expenseService.getTotalExpenseAmount());
    }

    @GetMapping("/public/expense/totalExpense")
    public ResponseEntity<BigDecimal> getTotalExpenseAmountForSelectedDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tillDate){
        return ResponseEntity.ok(expenseService.getTotalExpenseAmountForSelectedDate(fromDate,tillDate));
    }

}
