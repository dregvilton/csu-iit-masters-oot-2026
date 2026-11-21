
package ru.csu.iit.master2026.oot.hw01bank;

/**
 * Сумма операции в рублях, хранится в копейках.
 *
 * <p>Значимый объект: неизменяемый, равенство по значению, отрицательной суммы не бывает.
 */
public record Amount(long kopecks) {

    public static final Amount ZERO = new Amount(0);

    /** @throws IllegalArgumentException если {@code kopecks < 0} */
    public Amount {
        if (kopecks < 0) {
            throw new IllegalArgumentException("Сумма не может быть отрицательной: " + kopecks);
        }
    }

    public static Amount rubles(long rubles) {
        return new Amount(Math.multiplyExact(rubles, 100));
    }

    public Amount plus(Amount other) {
        return new Amount(Math.addExact(kopecks, other.kopecks));
    }

    /** @throws IllegalArgumentException если результат меньше нуля */
    public Amount minus(Amount other) {
        return new Amount(Math.subtractExact(kopecks, other.kopecks));
    }

    public boolean isAtLeast(Amount other) {
        return kopecks >= other.kopecks;
    }

    /** Например, {@code 1234.05 руб.} */
    @Override
    public String toString() {
        return "%d.%02d руб.".formatted(kopecks / 100, kopecks % 100);
    }
}
