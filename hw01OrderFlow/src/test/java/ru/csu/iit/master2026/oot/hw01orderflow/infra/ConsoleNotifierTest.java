package ru.csu.iit.master2026.oot.hw01orderflow.infra;

import org.junit.jupiter.api.Test;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.CourierDelivery;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.CustomerId;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.DeliveryTariff;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.Email;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.Order;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.OrderId;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.Pickup;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.SameDayDelivery;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.csu.iit.master2026.oot.hw01orderflow.domain.Money.rubles;

class ConsoleNotifierTest {

    @Test
    void экспресс_в_день_заказа_обещает_сегодня() {
        assertThat(placedMessage(new SameDayDelivery())).isEqualTo(
                "[EMAIL anna@example.ru] Анна, заказ ORD-1 принят. " +
                        "Сумма: 1250.00 руб. Экспресс-доставка: привезём сегодня.\n");
    }

    @Test
    void обычный_экспресс_обещает_завтра() {
        assertThat(placedMessage(new ExpressDelivery())).isEqualTo(
                "[EMAIL anna@example.ru] Анна, заказ ORD-1 принят. " +
                        "Сумма: 850.00 руб. Экспресс-доставка: привезём завтра.\n");
    }

    @Test
    void курьер_без_обещания_экспресса() {
        assertThat(placedMessage(new CourierDelivery())).isEqualTo(
                "[EMAIL anna@example.ru] Анна, заказ ORD-1 принят. " +
                        "Сумма: 650.00 руб.\n");
    }

    @Test
    void самовывоз_без_обещания_экспресса() {
        assertThat(placedMessage(new Pickup())).isEqualTo(
                "[EMAIL anna@example.ru] Анна, заказ ORD-1 принят. " +
                        "Сумма: 350.00 руб.\n");
    }

    private String placedMessage(DeliveryTariff delivery) {
        Customer anna = new Customer(new CustomerId("c-1"), "Анна",
                new Email("anna@example.ru"), "+79000000001");

        Order order = Order.builder()
                .customer(anna)
                .item("Кружка", 1, rubles(350))
                .delivery(delivery)
                .build(new OrderId("ORD-1"));

        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(captured);
            new ConsoleNotifier().orderPlaced(order);
        } finally {
            System.setOut(original);
        }

        return output.toString(StandardCharsets.UTF_8);
    }
}
