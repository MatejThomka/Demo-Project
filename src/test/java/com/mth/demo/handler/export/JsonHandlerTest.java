package com.mth.demo.handler.export;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mth.demo.repositories.FinanceRepository;
import com.mth.demo.services.UserService;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JsonHandlerTest {
    @Mock
    private FinanceRepository financeRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private JsonHandler jsonHandler;

    @Test
    void writeToJson_IOExceptionThrown_OutputStreamCalledOnceAndExceptionPropagated() throws IOException {
        // Arrange
        Long userId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);

        OutputStream outputStream = mock(OutputStream.class);
        doThrow(IOException.class).when(outputStream).write(any(byte[].class), anyInt(), anyInt());

        // Act and assert
        assertThrows(IOException.class, () -> jsonHandler.writeToJson(outputStream, userId, startDate, endDate));
        verify(outputStream, times(1)).write(any(byte[].class), anyInt(), anyInt());
    }
}
