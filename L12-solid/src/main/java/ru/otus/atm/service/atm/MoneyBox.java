package ru.otus.atm.service.atm;

import ru.otus.atm.domain.BanknoteDenomination;

public interface MoneyBox {
    long getBanknotes(BanknoteDenomination banknoteDenomination);
    void putBanknotes(BanknoteDenomination banknoteDenomination, long amount);
    long getRemainingAmount();
    void addBanknote(BanknoteDenomination banknoteDenomination);
    boolean containsBanknotes(BanknoteDenomination banknoteDenomination);
}
