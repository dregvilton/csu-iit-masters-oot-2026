
package ru.csu.iit.master2026.oot.hw01bank;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AmountTest {

    @Test
    void рубли_переводятся_в_копейки() {
        assertThat(Amount.rubles(500).kopecks()).isEqualTo(50_000);
    }

    @Test
    void суммы_складываются_и_вычитаются() {
        Amount amount = Amount.rubles(500);

        assertThat(amount.plus(Amount.rubles(200))).isEqualTo(Amount.rubles(700));
        assertThat(amount.minus(Amount.rubles(200))).isEqualTo(Amount.rubles(300));
        assertThat(amount.isAtLeast(Amount.rubles(500))).isTrue();
        assertThat(amount.isAtLeast(Amount.rubles(600))).isFalse();
    }

    @Test
    void отрицательную_сумму_создать_нельзя() {
        assertThatThrownBy(() -> new Amount(-1))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Amount.rubles(100).minus(Amount.rubles(200)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void суммы_равны_по_значению_и_печатаются_в_рублях() {
        Amount first = new Amount(123_405);
        Amount second = new Amount(123_405);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.toString()).isEqualTo("1234.05 руб.");
        assertThat(Amount.ZERO).isEqualTo(new Amount(0));
    }
}
