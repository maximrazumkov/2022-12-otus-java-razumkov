package ru.otus.dto;

import java.lang.reflect.Method;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestClassWrapperDto {
    Class<?> clazz;
    List<Method> beforeMethod;
    List<Method> testMethod;
    List<Method> afterMethod;
    StatisticDto statisticDto;
}
