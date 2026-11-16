package MoneyManager.controller;

import MoneyManager.payload.IncomeDTO;
import MoneyManager.payload.IncomeDTOResponse;
import MoneyManager.service.IncomeService;
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
public class IncomeController {
    @Autowired
    IncomeService incomeService;

    @PostMapping("/public/income")
    public ResponseEntity<IncomeDTO> createIncomeAgainstLoggedInProfile(@Valid @RequestBody IncomeDTO incomeDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(incomeService.createIncome(incomeDTO));
    }

    @GetMapping("/public/incomes")
    public ResponseEntity<IncomeDTOResponse> getAllIncomeForGivenDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate){

        return ResponseEntity.ok(incomeService.getAllIncomeForGivenDate(fromDate, toDate));
    }

    @DeleteMapping("/public/incomeId/{incomeId}")
    public ResponseEntity<IncomeDTO> deleteIncome(@PathVariable Long incomeId){
        return ResponseEntity.ok(incomeService.deleteIncome(incomeId));
    }

    @GetMapping("/public/latest/incomes")
    public ResponseEntity<List<IncomeDTO>> getLatest5Incomes(){
        return ResponseEntity.ok(incomeService.getLatest5Incomes());
    }

    @GetMapping("/public/income/totalAmount")
    public ResponseEntity<BigDecimal> getTotalIncomeAmount(){
        return ResponseEntity.ok(incomeService.getTotalIncomeAmount());
    }

    @GetMapping("/public/income/totalIncome")
    public ResponseEntity<BigDecimal> getTotalIncomeAmountForSelectedDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tillDate){
        return ResponseEntity.ok(incomeService.getTotalIncomeAmountForSelectedDate(fromDate,tillDate));
    }
}
