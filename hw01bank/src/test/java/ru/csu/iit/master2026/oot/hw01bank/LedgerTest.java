
package ru.csu.iit.master2026.oot.hw01bank;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LedgerTest {

    @Test
    void журнал_выполняет_и_отменяет_команды_в_обратном_порядке() {
        Account account = Account.debit(new AccountId("ACC-1"));
        Ledger ledger = new Ledger();

        ledger.run(new Deposit(account, Amount.rubles(500)));
        ledger.run(new Deposit(account, Amount.rubles(200)));

        assertThat(account.balance()).isEqualTo(70_000);
        assertThat(ledger.size()).isEqualTo(2);

        ledger.undoLast();

        assertThat(account.balance()).isEqualTo(50_000);
        assertThat(ledger.size()).isEqualTo(1);

        ledger.undoLast();

        assertThat(account.balance()).isZero();
        assertThat(ledger.size()).isZero();
    }

    @Test
    void неудачная_команда_не_попадает_в_журнал() {
        Account account = Account.debit(new AccountId("ACC-2"));
        Ledger ledger = new Ledger();

        assertThatThrownBy(() ->
                ledger.run(new Withdraw(account, Amount.rubles(100))))
                .isInstanceOf(IllegalStateException.class);

        assertThat(account.balance()).isZero();
        assertThat(ledger.size()).isZero();
    }

    @Test
    void неудачная_отмена_сохраняет_команду_в_журнале() {
        Account account = Account.debit(new AccountId("ACC-3"));
        Ledger ledger = new Ledger();

        ledger.run(new Deposit(account, Amount.rubles(500)));

        account.withdraw(Amount.rubles(400));

        assertThatThrownBy(ledger::undoLast)
                .isInstanceOf(IllegalStateException.class);

        assertThat(account.balance()).isEqualTo(10_000);
        assertThat(ledger.size()).isEqualTo(1);

        account.deposit(Amount.rubles(400));
        ledger.undoLast();

        assertThat(account.balance()).isZero();
        assertThat(ledger.size()).isZero();
    }

    @Test
    void пустой_журнал_отменить_нельзя() {
        Ledger ledger = new Ledger();

        assertThatThrownBy(ledger::undoLast)
                .isInstanceOf(IllegalStateException.class);

        assertThat(ledger.size()).isZero();
    }
}
