package ru.otus.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.exception.EvenSecondException;
import ru.otus.model.Message;
import ru.otus.provider.SecondProvider;

public class ProcessorThrowExceptionEveryEvenSecondTest {

    private Processor processor;
    private SecondProvider secondProvider;

    @BeforeEach
    void setUp(){
        secondProvider = Mockito.mock(SecondProvider.class);
        processor = new ProcessorThrowExceptionEveryEvenSecond(secondProvider);
    }

    @Test
    @DisplayName("не должен кидать исключение, когда SecondProvider возвращает нечетную секунду")
    void shouldNotThrowExceptionWhenSecondProviderReturnOddSecond() {
        given(secondProvider.getSecond()).willReturn(3);
        var message = new Message.Builder(1L).build();
        var messageResult = processor.process(message);
        verify(secondProvider).getSecond();
        assertThat(message).isEqualTo(messageResult);
    }

    @Test
    @DisplayName("должен кидать нужное исключение, когда SecondProvider возвращает четную секунду")
    void shouldThrowExceptionWhenSecondProviderReturnEvenSecond() {
        given(secondProvider.getSecond()).willReturn(2);
        var message = new Message.Builder(1L).build();
        assertThrows(EvenSecondException.class, () -> processor.process(message));
    }
}
