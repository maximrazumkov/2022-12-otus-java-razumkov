package ru.otus.processor;

import ru.otus.exception.EvenSecondException;
import ru.otus.model.Message;
import ru.otus.provider.SecondProvider;

public class ProcessorThrowExceptionEveryEvenSecond implements Processor {

    private final static String EVEN_SECOND_ERROR = "SecondProvider returned even second";

    private final SecondProvider secondProvider;

    public ProcessorThrowExceptionEveryEvenSecond(SecondProvider secondProvider) {
        this.secondProvider = secondProvider;
    }

    @Override
    public Message process(Message message) {
        int remainderOfTheDivision = secondProvider.getSecond() % 2;
        if (remainderOfTheDivision == 0) {
            throw new EvenSecondException(EVEN_SECOND_ERROR);
        }
        return message;
    }
}
