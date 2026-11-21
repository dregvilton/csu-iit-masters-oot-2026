
package ru.csu.iit.master2026.oot.hw01bank;

import java.util.Objects;

/** Снятие со счёта. Отмена возвращает деньги на счёт. */
public final class Withdraw implements Command {

    private final Account account;
    private final Amount amount;
    private boolean executed;

    public Withdraw(Account account, Amount amount) {
        this.account = Objects.requireNonNull(account);
        this.amount = Objects.requireNonNull(amount);
    }

    @Override
    public void execute() {
        if (executed) {
            throw new IllegalStateException("Команда уже выполнена");
        }

        account.withdraw(amount);
        executed = true;
    }

    @Override
    public void undo() {
        if (!executed) {
            throw new IllegalStateException("Команда не выполнена");
        }

        account.deposit(amount);
        executed = false;
    }
}
