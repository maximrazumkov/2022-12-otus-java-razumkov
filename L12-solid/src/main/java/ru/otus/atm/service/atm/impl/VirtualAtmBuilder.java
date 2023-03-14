package ru.otus.atm.service.atm.impl;

import static ru.otus.atm.domain.BanknoteDenomination.FIVE_HUNDREDTH_BANKNOTE;
import static ru.otus.atm.domain.BanknoteDenomination.FIVE_THOUSANDTH_BANKNOTE;
import static ru.otus.atm.domain.BanknoteDenomination.ONE_HUNDREDTH_BANKNOTE;
import static ru.otus.atm.domain.BanknoteDenomination.ONE_THOUSANDTH_BANKNOTE;

import java.util.HashMap;
import java.util.Map;
import ru.otus.atm.domain.BanknoteDenomination;
import ru.otus.atm.service.atm.Atm;

public class VirtualAtmBuilder {

    private Map<BanknoteDenomination, Long> moneyBoxMap;

    private VirtualAtmBuilder(Map<BanknoteDenomination, Long> moneyBoxMap) {
        this.moneyBoxMap = moneyBoxMap;
    }

    public static VirtualAtmBuilder getBuilder() {
        Map<BanknoteDenomination, Long> moneyBoxMap = new HashMap<>();
        moneyBoxMap.put(FIVE_THOUSANDTH_BANKNOTE, 0L);
        moneyBoxMap.put(FIVE_HUNDREDTH_BANKNOTE, 0L);
        moneyBoxMap.put(ONE_THOUSANDTH_BANKNOTE, 0L);
        moneyBoxMap.put(ONE_HUNDREDTH_BANKNOTE, 0L);
        return new VirtualAtmBuilder(moneyBoxMap);
    }

    public VirtualAtmBuilder setBanknote(BanknoteDenomination banknoteDenomination, long amount) {
        moneyBoxMap.merge(banknoteDenomination, amount, Long::sum);
        return this;
    }

    public Atm build() {
        MapMoneyBox mapMoneyBox = new MapMoneyBox(moneyBoxMap);
        VirtualAtm virtualAtm = new VirtualAtm(mapMoneyBox);
        return virtualAtm;
    }
}
