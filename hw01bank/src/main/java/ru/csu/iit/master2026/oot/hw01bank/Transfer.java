
package ru.csu.iit.master2026.oot.hw01bank;

import java.util.Objects;

/**
 * Перевод между счетами. Атомарен: и выполнение, и отмена
 * либо меняют оба счёта, либо ни одного.
 */
public final class Transfer implements Command {

    private final Account from;
    private final Account to;
    private final Amount amount;
    private boolean executed;

    /** @throws IllegalArgumentException если {@code from} и {@code to} — один и тот же счёт */
    public Transfer(Account from, Account to, Amount amount) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.amount = Objects.requireNonNull(amount);

        if (from.equals(to)) {
            throw new IllegalArgumentException("Нельзя переводить на тот же счёт");
        }
    }

    @Override
    public void execute() {
        if (executed) {
            throw new IllegalStateException("Команда уже выполнена");
        }

        from.withdraw(amount);

        try {
            to.deposit(amount);
        } catch (RuntimeException e) {
            from.deposit(amount);
            throw e;
        }

        executed = true;
    }

    @Override
    public void undo() {
        if (!executed) {
            throw new IllegalStateException("Команда не выполнена");
        }

        to.withdraw(amount);

        try {
            from.deposit(amount);
        } catch (RuntimeException e) {
            to.deposit(amount);
            throw e;
        }

        executed = false;
    }
}
