package ru.otus.atm.domain;

public record InterimCalculationResult(long countNeedBanknote, long amountBanknote, long remainingBanknotes, long countMoneyBoxBanknote) { }
