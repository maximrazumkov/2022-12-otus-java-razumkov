package ru.otus.atm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.otus.atm.domain.BanknoteDenomination.FIVE_HUNDREDTH_BANKNOTE;
import static ru.otus.atm.domain.BanknoteDenomination.FIVE_THOUSANDTH_BANKNOTE;
import static ru.otus.atm.domain.BanknoteDenomination.ONE_HUNDREDTH_BANKNOTE;
import static ru.otus.atm.domain.BanknoteDenomination.ONE_THOUSANDTH_BANKNOTE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.atm.domain.BanknoteDenomination;
import ru.otus.atm.exception.IncorrectAmountException;
import ru.otus.atm.exception.UnableGiveMoneyException;
import ru.otus.atm.exception.NotEnoughMoneyException;
import ru.otus.atm.service.impl.VirtualAtm;

@DisplayName("Класс VirtualAtm")
public class VirtualAtmTest {

    private VirtualAtm atm;
    private List<BanknoteDenomination> banknoteList;

    private static Stream<Arguments> generateData() {
        var expectedBox = new HashMap<BanknoteDenomination, Long>();
        expectedBox.put(FIVE_THOUSANDTH_BANKNOTE, 0L);
        expectedBox.put(FIVE_HUNDREDTH_BANKNOTE, 1L);
        expectedBox.put(ONE_THOUSANDTH_BANKNOTE, 16L);
        expectedBox.put(ONE_HUNDREDTH_BANKNOTE, 2L);
        List<Arguments> arguments = new ArrayList<>();
        arguments.add(Arguments.of(16700L, 27800L, expectedBox));

        expectedBox = new HashMap<>();
        expectedBox.put(FIVE_THOUSANDTH_BANKNOTE, 0L);
        expectedBox.put(FIVE_HUNDREDTH_BANKNOTE, 0L);
        expectedBox.put(ONE_THOUSANDTH_BANKNOTE, 0L);
        expectedBox.put(ONE_HUNDREDTH_BANKNOTE, 2L);
        arguments.add(Arguments.of(200L, 44300L, expectedBox));

        expectedBox = new HashMap<>();
        expectedBox.put(FIVE_THOUSANDTH_BANKNOTE, 0L);
        expectedBox.put(FIVE_HUNDREDTH_BANKNOTE, 15L);
        expectedBox.put(ONE_THOUSANDTH_BANKNOTE, 35L);
        expectedBox.put(ONE_HUNDREDTH_BANKNOTE, 20L);
        arguments.add(Arguments.of(44500L, 0L, expectedBox));

        return arguments.stream();
    }

    @BeforeEach
    void setUp() {
        var moneyBox = new HashMap<BanknoteDenomination, Long>();
        moneyBox.put(FIVE_THOUSANDTH_BANKNOTE, 0L);
        moneyBox.put(FIVE_HUNDREDTH_BANKNOTE, 15L);
        moneyBox.put(ONE_THOUSANDTH_BANKNOTE, 35L);
        moneyBox.put(ONE_HUNDREDTH_BANKNOTE, 20L);
        atm = new VirtualAtm(moneyBox);

       banknoteList = List.of(
            FIVE_HUNDREDTH_BANKNOTE,
            FIVE_THOUSANDTH_BANKNOTE,
            ONE_HUNDREDTH_BANKNOTE,
            ONE_THOUSANDTH_BANKNOTE,
            FIVE_THOUSANDTH_BANKNOTE,
            FIVE_THOUSANDTH_BANKNOTE,
            ONE_HUNDREDTH_BANKNOTE
        );
    }

    @Test
    @DisplayName("Должен вернуть остаток денежных средств")
    void shouldReturnRemainingAmount() {
        long actualRemaining = atm.getRemainingAmount();
        long expectedRemaining = 44_500;
        assertThat(actualRemaining).isEqualTo(expectedRemaining);
    }

    @Test
    @DisplayName("Должен положить денежные средства на счет")
    void shouldPutMoney() {
        atm.putMoney(banknoteList);
        long actualRemaining = atm.getRemainingAmount();
        long expectedRemaining = 61_200L;
        assertThat(actualRemaining).isEqualTo(expectedRemaining);
    }

    @ParameterizedTest
    @MethodSource("generateData")
    @DisplayName("Должен вернуть денежные средства со счета")
    void shouldGiveMoney(long requestedAmount, long expectedRemaining, Map<BanknoteDenomination, Long> expectedBox) {
        Map<BanknoteDenomination, Long> actualBox = atm.getMoney(requestedAmount);
        long actualRemaining = atm.getRemainingAmount();
        assertThat(actualRemaining).isEqualTo(expectedRemaining);
        assertThat(actualBox).isEqualTo(expectedBox);
    }

    @Test
    @DisplayName("Должен вернуть денежные средства со счета когда не все ячейки в банкомате")
    void shouldGiveMoneyWhenNotAllBoxBanknote() {
        var boxMoney = new HashMap<BanknoteDenomination, Long>();
        boxMoney.put(ONE_HUNDREDTH_BANKNOTE, 15L);
        boxMoney.put(FIVE_THOUSANDTH_BANKNOTE, 5L);
        VirtualAtm atm = new VirtualAtm(boxMoney);

        var expectedMoney = new HashMap<BanknoteDenomination, Long>();
        expectedMoney.put(FIVE_THOUSANDTH_BANKNOTE, 0L);
        expectedMoney.put(FIVE_HUNDREDTH_BANKNOTE, 0L);
        expectedMoney.put(ONE_THOUSANDTH_BANKNOTE, 0L);
        expectedMoney.put(ONE_HUNDREDTH_BANKNOTE, 1L);

        Map<BanknoteDenomination, Long> actualMoney = atm.getMoney(100);
        long actualRemaining = atm.getRemainingAmount();
        assertThat(actualRemaining).isEqualTo(26_400);
        assertThat(actualMoney).isEqualTo(expectedMoney);
    }

    @Test
    @DisplayName("Должен кидать нужное исключение, когда нельзя выдать запрошенную сумму из-за отсутствия нужных купюр")
    void shouldThrowExpectedExceptionWhenNotEnoughMoney() {
        assertThrows(UnableGiveMoneyException.class, () -> atm.getMoney(1050L));
    }

    @Test
    @DisplayName("Должен кидать нужное исключение, когда недостаточно средств на счету")
    void shouldThrowExpectedExceptionWhenCantGiveRequestedMoney() {
        assertThrows(NotEnoughMoneyException.class, () -> atm.getMoney(100_000L));
    }

    @Test
    @DisplayName("Должен кидать нужное исключение, когда передана некорректная сумма")
    void shouldThrowExpectedExceptionWhenIncorrectAmount() {
        assertThrows(IncorrectAmountException.class, () -> atm.getMoney(-1L));
    }
}
