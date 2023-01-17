package homework;

import static java.util.Map.entry;
import static java.util.Objects.nonNull;

import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    //todo: 3. надо реализовать методы этого класса
    private TreeMap<Customer, String> customerMap = new TreeMap<>(this::compareCustomer);

    //важно подобрать подходящую Map-у, посмотрите на редко используемые методы, они тут полезны

    public Map.Entry<Customer, String> getSmallest() {
        //Возможно, чтобы реализовать этот метод, потребуется посмотреть как Map.Entry сделан в jdk
        return copyMapEntry(customerMap.lastEntry()); // это "заглушка, чтобы скомилировать"
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return copyMapEntry(customerMap.lowerEntry(customer)); // это "заглушка, чтобы скомилировать"
    }

    public void add(Customer customer, String data) {
        customerMap.put(customer, data);
    }

    private Map.Entry<Customer, String> copyMapEntry(Map.Entry<Customer, String> entry) {
        return nonNull(entry) ? entry(copyCustomer(entry.getKey()), entry.getValue()) : null;
    }

    private Customer copyCustomer(Customer customer) {
       return new Customer(customer);
    }

    private int compareCustomer(Customer customerA, Customer customerB) {
        return Long.compare(customerB.getScores(), customerA.getScores());
    }
}
