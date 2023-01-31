package ru.otus.test;

import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

public class TestClass3 {
    @Before
    public void before1() {
        System.out.println("Before 1");
        throw new RuntimeException("Тест упал");
    }

    @Test
    public void test1() {
        System.out.println("Test 1");
    }

    @Test
    public void test2() {
        System.out.println("Test 2");
    }

    @Test
    public void test3() {
        System.out.println("Test 3");
        throw new RuntimeException("Тест упал");
    }

    @After
    public void after() {
        System.out.println("After 1");
    }

    @After
    public void after2() {
        System.out.println("After 2");
    }
}
