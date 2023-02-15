package ru.otus.atm.service;

import java.util.List;
import java.util.Map;
import ru.otus.atm.domain.BanknoteDenomination;

public interface GettingMoney {
    Map<BanknoteDenomination, Long> getMoney(long requestedAmount);
}
