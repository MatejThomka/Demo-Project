package com.mth.demo.models.dto.finance;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetIncomeDTO {

    Long id;

    Double income;

    Boolean recurring;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate transactionDate;

    String incomeType;

    String fileName;

}