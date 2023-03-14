package ru.otus.atm.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum BanknoteDenomination {
    FIVE_THOUSANDTH_BANKNOTE(5000L),
    ONE_THOUSANDTH_BANKNOTE(1000L),
    FIVE_HUNDREDTH_BANKNOTE(500L),
    ONE_HUNDREDTH_BANKNOTE(100L);

    private final long banknote;
}
