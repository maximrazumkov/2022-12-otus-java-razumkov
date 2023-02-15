package ru.otus.atm.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import ru.otus.atm.domain.BanknoteDenomination;
import ru.otus.atm.domain.BanknoteInfo;
import ru.otus.atm.exception.IncorrectAmountException;
import ru.otus.atm.exception.UnableGiveMoneyException;
import ru.otus.atm.exception.NotEnoughMoneyException;
import ru.otus.atm.service.GettingMoney;
import ru.otus.atm.service.GettingRemaining;
import ru.otus.atm.service.PuttingMoney;

@RequiredArgsConstructor
public class VirtualAtm implements GettingRemaining, GettingMoney, PuttingMoney {

    final static String NOT_ENOUGH_MONEY = "There are not enough money";
    final static String UNABLE_GIVE_AMOUNT = "Unable to give requested amount";
    final static String INCORRECT_AMOUNT = "Incorrect amount";

    final Map<BanknoteDenomination, Long> moneyBox;

    @Override
    public Map<BanknoteDenomination, Long> getMoney(long requestedAmount) {
        validate(requestedAmount);
        return getMoneyByAmount(requestedAmount);
    }

    private void validate(long requestedAmount) {
        if (requestedAmount < 0) {
            throw new IncorrectAmountException(INCORRECT_AMOUNT);
        }
        if (getRemainingAmount() < requestedAmount) {
            throw new NotEnoughMoneyException(NOT_ENOUGH_MONEY);
        }
        if (!checkSumma(requestedAmount)) {
            throw new UnableGiveMoneyException(UNABLE_GIVE_AMOUNT);
        }
    }

    private boolean checkSumma(long requestedAmount) {
        return getRemainingAmountByRequestedAmount(requestedAmount) == 0;
    }

    private long getRemainingAmountByRequestedAmount(long requestedAmount) {
        return Arrays.stream(BanknoteDenomination.values())
            .filter(moneyBox::containsKey)
            .reduce(requestedAmount, this::reduceRemainingAmount, Long::sum);
    }

    private long reduceRemainingAmount(long partialResult, BanknoteDenomination banknoteDenomination) {
        return partialResult - getBanknoteInfo(partialResult, banknoteDenomination).amountBanknote();
    }

    private BanknoteInfo getBanknoteInfo(long remainingAmount, BanknoteDenomination banknoteDenomination) {
        long countNeedBanknote = remainingAmount / banknoteDenomination.getBanknote();
        long countMoneyBoxBanknote = Optional.ofNullable(moneyBox.get(banknoteDenomination)).orElse(0L);
        long remainingBanknotes = countMoneyBoxBanknote - countNeedBanknote;
        long count = remainingBanknotes < 0 ? countMoneyBoxBanknote : countNeedBanknote;
        long amount = count * banknoteDenomination.getBanknote();
        return new BanknoteInfo(countNeedBanknote, amount, remainingBanknotes, countMoneyBoxBanknote);
    }

    private Map<BanknoteDenomination, Long> getMoneyByAmount(long requestedAmount) {
        long remainingAmount = requestedAmount;
        var result = new HashMap<BanknoteDenomination, Long>();
        for (BanknoteDenomination banknoteDenomination : BanknoteDenomination.values()) {
            BanknoteInfo banknoteInfo = getBanknoteInfo(remainingAmount, banknoteDenomination);
            remainingAmount -= banknoteInfo.amountBanknote();
            if ((banknoteInfo.remainingBanknotes()) < 0) {
                moneyBox.put(banknoteDenomination, 0L);
                result.put(banknoteDenomination, banknoteInfo.countMoneyBoxBanknote());
            } else {
                moneyBox.put(banknoteDenomination, banknoteInfo.remainingBanknotes());
                result.put(banknoteDenomination, banknoteInfo.countNeedBanknote());
            }
        }
        return result;
    }

    @Override
    public long getRemainingAmount() {
        return moneyBox.entrySet().stream()
            .map(entry -> entry.getValue() * entry.getKey().getBanknote())
            .reduce(Long::sum)
            .orElse(0L);
    }

    @Override
    public void putMoney(List<? extends BanknoteDenomination> money) {
        money.forEach(banknoteDenomination -> moneyBox.merge(banknoteDenomination, 1L, Long::sum));
    }
}
