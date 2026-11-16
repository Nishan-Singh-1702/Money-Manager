package MoneyManager.payload;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeDTO {
    private Long incomeId;

    @NotBlank(message = "Income name should not be blank")
    private String incomeName;

    private String icon;

    private LocalDate date;

    @NotBlank(message = "Category name should not be blank")
    private String categoryName;

    @NotNull(message = "Amount should not be null")
    @Positive(message = "Amount should be greater than zero")
    @Column(precision = 19, scale = 2)
    private BigDecimal incomeAmount;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
