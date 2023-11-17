package com.mth.demo.models.dto.finance;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncomeDTO {

    Double income;

    Boolean recurring;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate transactionDate;

}