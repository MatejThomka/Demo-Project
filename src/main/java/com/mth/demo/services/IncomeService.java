package com.mth.demo.services;

import com.mth.demo.exception.PfpException;
import com.mth.demo.exception.UnauthorizedException;
import com.mth.demo.exception.dataexportexceptions.DateRangeMissingException;
import com.mth.demo.exception.dataexportexceptions.DateRangeTooBigException;
import com.mth.demo.exception.financeexceptions.FinanceNotFoundException;
import com.mth.demo.models.dto.finance.FinancesMonthlyDTO;
import com.mth.demo.models.dto.finance.GetAllIncomeDTO;
import com.mth.demo.models.dto.finance.GetIncomeDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface IncomeService {

    GetIncomeDTO createNewIncome(String income, MultipartFile file, String request) throws FinanceNotFoundException, IOException;

    GetAllIncomeDTO getAllIncomes(String request) throws FinanceNotFoundException;

    GetIncomeDTO updateIncome(Long incomeId, String income, MultipartFile file, String request) throws UnauthorizedException, FinanceNotFoundException, IOException;

    void deleteIncome(Long incomeId, String request) throws UnauthorizedException, FinanceNotFoundException;

    double getTotalIncomeBetweenDates(String request, LocalDate startDate, LocalDate endDate) throws DateRangeMissingException, DateRangeTooBigException;

    List<FinancesMonthlyDTO> monthlyIncomes(String request) throws PfpException;

}