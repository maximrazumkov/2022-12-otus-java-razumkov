package ru.otus.atm;

import static ru.otus.atm.domain.BanknoteDenomination.FIVE_THOUSANDTH_BANKNOTE;
import static ru.otus.atm.domain.BanknoteDenomination.ONE_HUNDREDTH_BANKNOTE;

import java.util.HashMap;
import java.util.Map;
import ru.otus.atm.domain.BanknoteDenomination;
import ru.otus.atm.service.impl.VirtualAtm;

public class Main {

    public static void main(String[] args) {
        HashMap<BanknoteDenomination, Long> map = new HashMap<>();
        map.put(ONE_HUNDREDTH_BANKNOTE, 15L);
        map.put(FIVE_THOUSANDTH_BANKNOTE, 5L);
        VirtualAtm atm = new VirtualAtm(map);
        Map<BanknoteDenomination, Long> money = atm.getMoney(100);
        System.out.println(atm.getRemainingAmount());
    }
}
