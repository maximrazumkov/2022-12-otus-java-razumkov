package ru.otus.atm.service.atm;

import java.util.List;
import java.util.Map;
import ru.otus.atm.domain.BanknoteDenomination;

public interface Atm {
    Map<BanknoteDenomination, Long> getMoney(long requestedAmount);
    long getRemainingAmount();
    void putMoney(List<? extends BanknoteDenomination> money);
}
