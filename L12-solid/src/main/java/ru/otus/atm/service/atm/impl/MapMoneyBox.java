package ru.otus.atm.service.atm.impl;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import ru.otus.atm.domain.BanknoteDenomination;
import ru.otus.atm.service.atm.MoneyBox;

@RequiredArgsConstructor
class MapMoneyBox implements MoneyBox {

    private final Map<BanknoteDenomination, Long> moneyBox;

    @Override
    public long getBanknotes(BanknoteDenomination banknoteDenomination) {
        return moneyBox.get(banknoteDenomination);
    }

    @Override
    public void putBanknotes(BanknoteDenomination banknoteDenomination, long amount) {
        moneyBox.put(banknoteDenomination, amount);
    }

    @Override
    public long getRemainingAmount() {
        return moneyBox.entrySet().stream()
            .map(entry -> entry.getValue() * entry.getKey().getBanknote())
            .reduce(Long::sum)
            .orElse(0L);
    }

    @Override
    public void addBanknote(BanknoteDenomination banknoteDenomination) {
        moneyBox.merge(banknoteDenomination, 1L, Long::sum);
    }

    @Override
    public boolean containsBanknotes(BanknoteDenomination banknoteDenomination) {
        return moneyBox.containsKey(banknoteDenomination);
    }
}
