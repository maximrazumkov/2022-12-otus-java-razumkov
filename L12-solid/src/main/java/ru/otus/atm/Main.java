package ru.otus.atm;

import static ru.otus.atm.domain.BanknoteDenomination.FIVE_THOUSANDTH_BANKNOTE;
import static ru.otus.atm.domain.BanknoteDenomination.ONE_HUNDREDTH_BANKNOTE;

import java.util.Map;
import ru.otus.atm.domain.BanknoteDenomination;
import ru.otus.atm.service.atm.Atm;
import ru.otus.atm.service.atm.impl.VirtualAtmBuilder;

public class Main {

    public static void main(String[] args) {
        Atm atm = VirtualAtmBuilder.getBuilder()
            .setBanknote(ONE_HUNDREDTH_BANKNOTE, 15L)
            .setBanknote(FIVE_THOUSANDTH_BANKNOTE, 5L)
            .build();
        Map<BanknoteDenomination, Long> money = atm.getMoney(100);
        System.out.println(atm.getRemainingAmount());
    }
}
