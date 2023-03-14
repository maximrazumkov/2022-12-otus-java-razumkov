package ru.otus;

import java.util.ArrayList;
import ru.otus.model.Message;
import ru.otus.processor.ProcessorChangeField11AndField12;

public class HomeWork {

    /*
     Реализовать to do:
       1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
       2. Сделать процессор, который поменяет местами значения field11 и field12
       3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
             Секунда должна определяьться во время выполнения.
             Тест - важная часть задания
             Обязательно посмотрите пример к паттерну Мементо!
       4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
          Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
          Для него уже есть тест, убедитесь, что тест проходит
     */

    public static void main(String[] args) {
        /*
           по аналогии с Demo.class
           из элеменов "to do" создать new ComplexProcessor и обработать сообщение
         */
        ArrayList<Object> objects = new ArrayList<>();
        var processors = new ProcessorChangeField11AndField12();

        var message = new Message.Builder(1L)
            .field1("field1")
            .field2("field2")
            .field3("field3")
            .field6("field6")
            .field10("field10")
            .field11("field11")
            .field12("field12")
            .build();

        String field11 = message.getField11();
        field11 = "";
        System.out.println(field11);

        var result = processors.process(message);
        System.out.println("result:" + result);
    }
}
