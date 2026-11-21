
package ru.csu.iit.master2026.oot.hw01bank;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommandTest {

    @Test
    void пополнение_можно_выполнить_и_отменить() {
        Account account = Account.debit(new AccountId("ACC-1"));
        Deposit command = new Deposit(account, Amount.rubles(500));

        command.execute();

        assertThat(account.balance()).isEqualTo(50_000);

        command.undo();

        assertThat(account.balance()).isZero();
    }

    @Test
    void пополнение_нельзя_отменить_если_денег_недостаточно() {
        Account account = Account.debit(new AccountId("ACC-2"));
        Deposit command = new Deposit(account, Amount.rubles(500));

        command.execute();
        account.withdraw(Amount.rubles(300));

        assertThatThrownBy(command::undo)
                .isInstanceOf(IllegalStateException.class);

        assertThat(account.balance()).isEqualTo(20_000);
    }

    @Test
    void снятие_можно_выполнить_и_отменить() {
        Account account = Account.debit(new AccountId("ACC-3"));
        account.deposit(Amount.rubles(600));

        Withdraw command = new Withdraw(account, Amount.rubles(200));

        command.execute();

        assertThat(account.balance()).isEqualTo(40_000);

        command.undo();

        assertThat(account.balance()).isEqualTo(60_000);
    }

    @Test
    void неудачное_снятие_не_меняет_баланс() {
        Account account = Account.debit(new AccountId("ACC-4"));
        Withdraw command = new Withdraw(account, Amount.rubles(100));

        assertThatThrownBy(command::execute)
                .isInstanceOf(IllegalStateException.class);

        assertThat(account.balance()).isZero();

        assertThatThrownBy(command::undo)
                .isInstanceOf(IllegalStateException.class);
    }


    @Test
    void перевод_можно_выполнить_и_отменить() {
        Account anna = Account.debit(new AccountId("ACC-5"));
        Account boris = Account.credit(
                new AccountId("ACC-6"), Amount.rubles(1000));

        anna.deposit(Amount.rubles(500));

        Transfer transfer = new Transfer(anna, boris, Amount.rubles(200));

        transfer.execute();

        assertThat(anna.balance()).isEqualTo(30_000);
        assertThat(boris.balance()).isEqualTo(20_000);

        transfer.undo();

        assertThat(anna.balance()).isEqualTo(50_000);
        assertThat(boris.balance()).isZero();
    }

    @Test
    void перевод_без_денег_не_меняет_оба_счёта() {
        Account anna = Account.debit(new AccountId("ACC-7"));
        Account boris = Account.debit(new AccountId("ACC-8"));

        anna.deposit(Amount.rubles(100));

        Transfer transfer = new Transfer(anna, boris, Amount.rubles(200));

        assertThatThrownBy(transfer::execute)
                .isInstanceOf(IllegalStateException.class);

        assertThat(anna.balance()).isEqualTo(10_000);
        assertThat(boris.balance()).isZero();
    }

    @Test
    void неудачная_отмена_перевода_не_меняет_балансы() {
        Account anna = Account.debit(new AccountId("ACC-9"));
        Account boris = Account.debit(new AccountId("ACC-10"));

        anna.deposit(Amount.rubles(500));

        Transfer transfer = new Transfer(anna, boris, Amount.rubles(200));
        transfer.execute();

        boris.withdraw(Amount.rubles(150));

        assertThatThrownBy(transfer::undo)
                .isInstanceOf(IllegalStateException.class);

        assertThat(anna.balance()).isEqualTo(30_000);
        assertThat(boris.balance()).isEqualTo(5_000);

        boris.deposit(Amount.rubles(150));
        transfer.undo();

        assertThat(anna.balance()).isEqualTo(50_000);
        assertThat(boris.balance()).isZero();
    }

    @Test
    void нельзя_переводить_на_тот_же_счёт() {
        Account first = Account.debit(new AccountId("ACC-11"));
        Account second = Account.credit(
                new AccountId("ACC-11"), Amount.rubles(1000));

        assertThatThrownBy(() ->
                new Transfer(first, second, Amount.rubles(100)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
