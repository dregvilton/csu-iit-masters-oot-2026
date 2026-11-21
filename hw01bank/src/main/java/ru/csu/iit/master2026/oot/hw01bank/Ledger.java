
package ru.csu.iit.master2026.oot.hw01bank;

import java.util.ArrayDeque;
import java.util.Deque;

/** Журнал операций: выполняет команды и отменяет последнюю выполненную. */
public final class Ledger {

    private final Deque<Command> history = new ArrayDeque<>();

    /** Выполняет команду и запоминает её. Если команда бросила исключение — она не запоминается. */
    public void run(Command command) {
        command.execute();
        history.push(command);
    }

    /**
     * Отменяет последнюю выполненную команду и забывает её.
     * Если отмена бросила исключение — команда остаётся в журнале.
     *
     * @throws IllegalStateException если отменять нечего
     */
    public void undoLast() {
        if (history.isEmpty()) {
            throw new IllegalStateException("Нет команд для отмены");
        }

        history.peek().undo();
        history.pop();
    }

    /** Сколько команд можно отменить. */
    public int size() {
        return history.size();
    }
}
