package ru.otus.test;

import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

public class TestClass2 {

    @Before
    public void before1() {
        System.out.println("Before 1");
    }

    @Before
    public void before2() {
        System.out.println("Before 2");
    }

    @Test
    public void test1() {
        System.out.println("Test 1");
    }

    @Test
    public void test2() {
        System.out.println("test 2");
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
