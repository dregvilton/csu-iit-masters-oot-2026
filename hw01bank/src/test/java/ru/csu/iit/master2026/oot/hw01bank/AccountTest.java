
package ru.csu.iit.master2026.oot.hw01bank;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    void дебетовый_счёт_не_уходит_в_минус() {
        Account account = Account.debit(new AccountId("ACC-1"));

        account.deposit(Amount.rubles(500));
        account.withdraw(Amount.rubles(200));

        assertThat(account.balance()).isEqualTo(30_000);
        assertThat(account.available()).isEqualTo(Amount.rubles(300));

        assertThatThrownBy(() -> account.withdraw(Amount.rubles(400)))
                .isInstanceOf(IllegalStateException.class);

        assertThat(account.balance()).isEqualTo(30_000);
    }

    @Test
    void кредитный_счёт_может_использовать_лимит() {
        Account account = Account.credit(
                new AccountId("ACC-2"), Amount.rubles(1000));

        assertThat(account.available()).isEqualTo(Amount.rubles(1000));

        account.withdraw(Amount.rubles(700));

        assertThat(account.balance()).isEqualTo(-70_000);
        assertThat(account.available()).isEqualTo(Amount.rubles(300));

        account.deposit(Amount.rubles(200));

        assertThat(account.balance()).isEqualTo(-50_000);
        assertThat(account.available()).isEqualTo(Amount.rubles(500));
    }

    @Test
    void кредитный_лимит_превысить_нельзя() {
        Account account = Account.credit(
                new AccountId("ACC-3"), Amount.rubles(1000));

        account.withdraw(Amount.rubles(1000));

        assertThat(account.available()).isEqualTo(Amount.ZERO);

        assertThatThrownBy(() -> account.withdraw(Amount.rubles(1)))
                .isInstanceOf(IllegalStateException.class);

        assertThat(account.balance()).isEqualTo(-100_000);
    }

    @Test
    void счета_равны_по_номеру_а_не_по_балансу() {
        Account first = Account.debit(new AccountId("ACC-4"));
        Account second = Account.debit(new AccountId("ACC-4"));

        first.deposit(Amount.rubles(100));

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.balance()).isNotEqualTo(second.balance());
    }
}
