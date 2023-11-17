package com.mth.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.mth.demo.exception.dataexportexceptions.DateRangeMissingException;
import com.mth.demo.exception.dataexportexceptions.DateRangeTooBigException;
import com.mth.demo.models.dto.export.DateRangeDTO;
import com.mth.demo.services.DataExportService;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataExportApiControllerTest {
    @Test
    void exportDataAsZIP_endDateNotGiven_setEndDateToNow() throws DateRangeTooBigException, DateRangeMissingException, IOException {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = null;
        LocalDate testStartDate = LocalDate.now().minusDays(10);
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act
        String response = String.valueOf(testController.exportDataAsZIP(testDateRangeDTO, null));

        // Assert
        assertTrue(response.contains(LocalDate.now().toString()));
    }

    @Test
    void exportDataAsZIP_startDateNotGiven_returnErrorMessage() {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = LocalDate.now().minusDays(10);
        LocalDate testStartDate = null;
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act and assert
        Assertions.assertThrows(DateRangeMissingException.class,
                () -> testController.exportDataAsZIP(testDateRangeDTO, null));
    }

    @Test
    void exportDataAsZIP_dateRangeTooBig_returnErrorMessage() {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = LocalDate.now();
        LocalDate testStartDate = LocalDate.now().minusDays(100);
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act and assert
        Assertions.assertThrows(DateRangeTooBigException.class,
                () -> testController.exportDataAsZIP(testDateRangeDTO, null));
    }

    @Test
    void exportDataAsZIP_startDateIsAfterEndDate_changeDates() throws DateRangeTooBigException, DateRangeMissingException, IOException {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = LocalDate.now().minusDays(10);
        LocalDate testStartDate = LocalDate.now();
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act
        String response = String.valueOf(testController.exportDataAsZIP(testDateRangeDTO, null));

        // Assert
        assertNotEquals(response.contains(testStartDate + "_" + testEndDate), testStartDate + "_" + testEndDate);
    }

    @Test
    void exportDataAsCSV_endDateNotGiven_setEndDateToNow() throws DateRangeTooBigException, DateRangeMissingException, IOException {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = null;
        LocalDate testStartDate = LocalDate.now().minusDays(10);
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act
        String response = String.valueOf(testController.exportDataAsCSV(testDateRangeDTO, null));

        // Assert
        assertTrue(response.contains(LocalDate.now().toString()));
    }

    @Test
    void exportDataAsCSV_startDateNotGiven_returnErrorMessage() {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = LocalDate.now().minusDays(10);
        LocalDate testStartDate = null;
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act and assert
        Assertions.assertThrows(DateRangeMissingException.class,
                () -> testController.exportDataAsCSV(testDateRangeDTO, null));
    }

    @Test
    void exportDataAsCSV_dateRangeTooBig_returnErrorMessage() {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = LocalDate.now();
        LocalDate testStartDate = LocalDate.now().minusDays(100);
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act and assert
        Assertions.assertThrows(DateRangeTooBigException.class,
                () -> testController.exportDataAsCSV(testDateRangeDTO, null));
    }

    @Test
    void exportDataAsCSV_startDateIsAfterEndDate_changeDates() throws DateRangeTooBigException, DateRangeMissingException, IOException {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = LocalDate.now().minusDays(10);
        LocalDate testStartDate = LocalDate.now();
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act
        String response = String.valueOf(testController.exportDataAsCSV(testDateRangeDTO, null));

        // Assert
        assertNotEquals(response.contains(testStartDate + "_" + testEndDate), testStartDate + "_" + testEndDate);
    }

    @Test
    void exportDataAsJSON_endDateNotGiven_setEndDateToNow() throws DateRangeTooBigException, DateRangeMissingException, IOException {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = null;
        LocalDate testStartDate = LocalDate.now().minusDays(10);
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act
        String response = String.valueOf(testController.exportDataAsJSON(testDateRangeDTO, null));

        // Assert
        assertTrue(response.contains(LocalDate.now().toString()));
    }

    @Test
    void exportDataAsJSON_startDateNotGiven_returnErrorMessage() {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = LocalDate.now().minusDays(10);
        LocalDate testStartDate = null;
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act and assert
        Assertions.assertThrows(DateRangeMissingException.class,
                () -> testController.exportDataAsJSON(testDateRangeDTO, null));
    }

    @Test
    void exportDataAsJSON_dateRangeTooBig_returnErrorMessage() {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = LocalDate.now();
        LocalDate testStartDate = LocalDate.now().minusDays(100);
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act and assert
        Assertions.assertThrows(DateRangeTooBigException.class,
                () -> testController.exportDataAsJSON(testDateRangeDTO, null));
    }

    @Test
    void exportDataAsJSON_startDateIsAfterEndDate_changeDates() throws DateRangeTooBigException, DateRangeMissingException, IOException {
        // Arrange
        var dataExportServiceMock = mock(DataExportService.class);
        var testController = new DataExportApiController(dataExportServiceMock);
        LocalDate testEndDate = LocalDate.now().minusDays(10);
        LocalDate testStartDate = LocalDate.now();
        var testDateRangeDTO = new DateRangeDTO(testStartDate, testEndDate);

        // Act
        String response = String.valueOf(testController.exportDataAsJSON(testDateRangeDTO, null));

        // Assert
        assertNotEquals(response.contains(testStartDate + "_" + testEndDate), testStartDate + "_" + testEndDate);
    }
}