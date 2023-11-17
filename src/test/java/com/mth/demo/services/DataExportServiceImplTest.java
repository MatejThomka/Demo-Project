package com.mth.demo.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.mth.demo.models.entities.finance.Finance;
import com.mth.demo.repositories.FinanceRepository;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
class DataExportServiceImplTest {
    @Test
    void writeToCsv_IOExceptionThrown_OutputStreamCalledOnceAndExceptionPropagated() throws IOException {
        // Arrange
        OutputStream outputStream = mock(OutputStream.class);
        doThrow(IOException.class).when(outputStream).write(any(byte[].class), anyInt(), anyInt());

        // Act and assert
        Long userId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        assertThrows(IOException.class, () -> DataExportServiceImplTest.writeToCsv(outputStream, userId, startDate, endDate));
        verify(outputStream, times(1)).write(any(byte[].class), anyInt(), anyInt());
    }

    @Test
    void writeToJson_IOExceptionThrown_OutputStreamCalledOnceAndExceptionPropagated() throws IOException {
        // Arrange
        OutputStream outputStream = mock(OutputStream.class);
        doThrow(IOException.class).when(outputStream).write(any(byte[].class), anyInt(), anyInt());

        // Act and assert
        Long userId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        assertThrows(IOException.class, () -> DataExportServiceImplTest.writeToJson(outputStream, userId, startDate, endDate));
        verify(outputStream, times(1)).write(any(byte[].class), anyInt(), anyInt());
    }

    private static void writeToCsv(OutputStream outputStream, Long userId, LocalDate startDate, LocalDate endDate) throws IOException {
        var financeRepository = mock(FinanceRepository.class);
        List<Finance> finances = financeRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(outputStream), CSVFormat.DEFAULT.withHeader("id", "income", "incomeType", "expense", "expenseType", "transactionDate", "recurring"));
        for (Finance finance : finances) {
            csvPrinter.printRecord(finance.getId(), finance.getIncome(), finance.getIncomeType(), finance.getExpense(), finance.getCategory(), finance.getTransactionDate(), finance.getRecurring());
        }
        csvPrinter.flush();
    }

    private static void writeToJson(OutputStream outputStream, Long userId, LocalDate startDate, LocalDate endDate) throws IOException {
        var financeRepository = mock(FinanceRepository.class);
        List<Finance> finances = financeRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(outputStream, JsonEncoding.UTF8);
        jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
        jsonGenerator.writeStartArray();
        for (Finance finance : finances) {
            if (finance.getIncome() == 0.0) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeNumberField("id", finance.getId());
                jsonGenerator.writeNumberField("expense", finance.getExpense());
                jsonGenerator.writeStringField("expenseType", finance.getCategory().toString());
                jsonGenerator.writeStringField("transactionDate", finance.getTransactionDate().toString());
                jsonGenerator.writeEndObject();
            } else if (finance.getExpense() == 0.0) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeNumberField("id", finance.getId());
                jsonGenerator.writeNumberField("income", finance.getIncome());
                jsonGenerator.writeStringField("incomeType", finance.getIncomeType().toString());
                jsonGenerator.writeStringField("transactionDate", finance.getTransactionDate().toString());
                jsonGenerator.writeBooleanField("recurring", finance.getRecurring());
                jsonGenerator.writeEndObject();
            }
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.close();
    }
}