package ru.otus.atm.service;

import java.util.List;
import ru.otus.atm.domain.BanknoteDenomination;

public interface PuttingMoney {
    void putMoney(List<? extends BanknoteDenomination> money);
}
