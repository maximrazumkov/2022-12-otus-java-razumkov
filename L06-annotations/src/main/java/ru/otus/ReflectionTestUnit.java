package ru.otus;

import static ru.otus.util.ReflectionHelper.callMethod;
import static ru.otus.util.ReflectionHelper.getClassForName;
import static ru.otus.util.ReflectionHelper.instantiate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;
import ru.otus.dto.StatisticDto;
import ru.otus.dto.TestClassWrapperDto;

public class ReflectionTestUnit {

    private final static String TEMPLATE_STATISTIC = "Class - %s: \n  success: %s\n  fail: %s\n  total: %s\n";

    public static void main(String[] args) {
        new ReflectionTestUnit().start(args);
    }

    private void start(String... testClass) {
        List<TestClassWrapperDto> testClassList = parseTestClass(testClass);
        testClassList.forEach(this::invokeTestClass);
        List<String> statistic = getStatisticList(testClassList, TEMPLATE_STATISTIC);
        printStatisticList(statistic);
    }

    private List<TestClassWrapperDto> parseTestClass(String... testClass) {
        return Arrays.stream(testClass).map(this::getTestMethods).collect(Collectors.toList());
    }

    private TestClassWrapperDto getTestMethods(String testClass) {
        Class<?> clazz = getClassForName(testClass);
        Method[] declaredMethods = clazz.getDeclaredMethods();
        List<Method> beforeMethods = getMethodsByAnnotation(declaredMethods, Before.class);
        List<Method> testMethods = getMethodsByAnnotation(declaredMethods, Test.class);
        List<Method> afterMethods = getMethodsByAnnotation(declaredMethods, After.class);
        return buildTestClassWrapper(clazz, beforeMethods, testMethods, afterMethods);
    }

    private TestClassWrapperDto buildTestClassWrapper(Class<?> clazz,
        List<Method> beforeMethods, List<Method> testMethods, List<Method> afterMethods) {
        return TestClassWrapperDto.builder()
            .clazz(clazz)
            .beforeMethod(beforeMethods)
            .testMethod(testMethods)
            .afterMethod(afterMethods)
            .statisticDto(StatisticDto.builder().build())
            .build();
    }

    private List<Method> getMethodsByAnnotation(Method[] methods, Class<? extends Annotation> annotation) {
        return Arrays.stream(methods)
            .filter(method -> method.isAnnotationPresent(annotation))
            .toList();
    }

    private void invokeTestClass(TestClassWrapperDto testClassWrapperDto) {
        testClassWrapperDto.getTestMethod()
            .forEach(testMethod -> callTestMethod(testMethod, testClassWrapperDto));
    }

    private void callTestMethod(Method testMethod, TestClassWrapperDto testClassWrapperDto) {
        StatisticDto statisticDto = testClassWrapperDto.getStatisticDto();
        Object instantiate = null;
        try {
            instantiate = instantiate(testClassWrapperDto.getClazz());
            callMethods(instantiate, testClassWrapperDto.getBeforeMethod());
            callMethod(instantiate, testMethod.getName());
            int success = statisticDto.getSuccess();
            statisticDto.setSuccess(success + 1);
        } catch (Exception e) {
            int Fail = statisticDto.getFail();
            statisticDto.setFail(Fail + 1);
        } finally {
            callMethods(instantiate, testClassWrapperDto.getAfterMethod());
        }
    }

    private void callMethods(Object instantiate, List<Method> methods) {
        methods.stream()
            .map(Method::getName)
            .forEach(afterMethod -> callMethod(instantiate, afterMethod));
    }

    private List<String> getStatisticList(List<TestClassWrapperDto> testClassList, String template) {
        return testClassList.stream()
            .map(testClass -> getStatistic(testClass, template))
            .collect(Collectors.toList());
    }

    private String getStatistic(TestClassWrapperDto testClass, String template) {
        String canonicalName = testClass.getClazz().getCanonicalName();
        StatisticDto statisticDto = testClass.getStatisticDto();
        int success = statisticDto.getSuccess();
        int fail = statisticDto.getFail();
        int total = success + fail;
        return String.format(template, canonicalName, success, fail, total);
    }

    private void printStatisticList(List<String> statistic) {
        statistic.forEach(System.out::println);
    }
}
