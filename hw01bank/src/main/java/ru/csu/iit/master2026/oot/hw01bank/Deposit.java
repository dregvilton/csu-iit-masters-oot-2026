
package ru.csu.iit.master2026.oot.hw01bank;

import java.util.Objects;

/** Пополнение счёта. Отмена снимает те же деньги — если они ещё на счёте. */
public final class Deposit implements Command {

    private final Account account;
    private final Amount amount;
    private boolean executed;

    public Deposit(Account account, Amount amount) {
        this.account = Objects.requireNonNull(account);
        this.amount = Objects.requireNonNull(amount);
    }

    @Override
    public void execute() {
        if (executed) {
            throw new IllegalStateException("Команда уже выполнена");
        }

        account.deposit(amount);
        executed = true;
    }

    @Override
    public void undo() {
        if (!executed) {
            throw new IllegalStateException("Команда не выполнена");
        }

        account.withdraw(amount);
        executed = false;
    }
}
