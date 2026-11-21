package ru.csu.iit.master2026.oot.hw01orderflow.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.*;
import ru.csu.iit.master2026.oot.hw01orderflow.infra.InMemoryOrderRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.csu.iit.master2026.oot.hw01orderflow.domain.Money.rubles;

class SalesReportTest {

    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();

    @BeforeEach
    void twoOrders() {
        Customer anna = new Customer(new CustomerId("c-1"), "Анна",
                new Email("anna@example.ru"), "+79000000001");
        PlaceOrder placeOrder = new PlaceOrder(repository, new RecordingNotifier());
        Order paid = placeOrder.place(Order.builder()
                .customer(anna)
                .item("Ноутбук", 1, rubles(64990))
                .delivery(new ExpressDelivery()));
        paid.pay();
        placeOrder.place(Order.builder()
                .customer(anna)
                .item("Кружка", 1, rubles(350))
                .delivery(new CourierDelivery()));
    }

    @Test
    void один_скелет_текстовый_формат() {
        assertThat(new TextSalesReport(repository).render()).isEqualTo("""
                ORD-1  PAID      65490.00 руб.
                ORD-2  NEW       650.00 руб.
                Заказов: 2, экспресс: 1, выручка: 65490.00 руб., крупнейший: ORD-1""");
    }

    @Test
    void один_скелет_csv() {
        assertThat(new CsvSalesReport(repository).render()).isEqualTo("""
                id;status;total_kopecks
                ORD-1;PAID;6549000
                ORD-2;NEW;65000
                TOTAL;2;6549000
                """);
    }

    @Test
    void оба_экспресс_тарифа_учитываются_в_отчёте() {
        Customer anna = new Customer(new CustomerId("c-1"), "Анна",
                new Email("anna@example.ru"), "+79000000001");
        PlaceOrder placeOrder = new PlaceOrder(repository, new RecordingNotifier());

        placeOrder.place(Order.builder()
                .customer(anna)
                .item("Кружка", 1, rubles(350))
                .delivery(new SameDayDelivery()));

        placeOrder.place(Order.builder()
                .customer(anna)
                .item("Кружка", 1, rubles(350))
                .delivery(new Pickup()));

        assertThat(new TextSalesReport(repository).render())
                .endsWith("Заказов: 4, экспресс: 2, выручка: 65490.00 руб., крупнейший: ORD-1");
    }

    @Test
    void json_отчёт_совпадает_с_условием() {
        Customer anna = new Customer(new CustomerId("c-1"), "Анна",
                new Email("anna@example.ru"), "+79000000001");

        PlaceOrder placeOrder = new PlaceOrder(repository, new RecordingNotifier());

        Order shipped = placeOrder.place(Order.builder()
                .customer(anna)
                .item("Кружка", 2, rubles(350))
                .delivery(new Pickup()));

        shipped.pay();
        shipped.ship();

        String expected = """
            {
              "orders": [
                {"id": "ORD-1", "status": "PAID", "totalKopecks": 6549000},
                {"id": "ORD-2", "status": "NEW", "totalKopecks": 65000},
                {"id": "ORD-3", "status": "SHIPPED", "totalKopecks": 70000}
              ],
              "count": 3,
              "express": 1,
              "revenueKopecks": 6619000,
              "largest": "ORD-1"
            }
            """;

        JsonSalesReport report = new JsonSalesReport(repository);

        assertThat(report.render()).isEqualTo(expected);
        assertThat(report.render()).isEqualTo(expected);
    }

    @Test
    void json_отчёт_для_пустого_хранилища() {
        OrderRepository empty = new InMemoryOrderRepository();

        assertThat(new JsonSalesReport(empty).render()).isEqualTo("""
            {
              "orders": [
              ],
              "count": 0,
              "express": 0,
              "revenueKopecks": 0,
              "largest": null
            }
            """);
    }

    @Test
    void оплаченный_на_паузе_входит_в_выручку_а_неоплаченный_нет() {
        Customer anna = new Customer(new CustomerId("c-1"), "Анна",
                new Email("anna@example.ru"), "+79000000001");

        PlaceOrder placeOrder = new PlaceOrder(repository, new RecordingNotifier());

        Order paid = placeOrder.place(Order.builder()
                .customer(anna)
                .item("Кружка", 1, rubles(350))
                .delivery(new Pickup()));

        paid.pay();
        paid.hold();

        Order unpaid = placeOrder.place(Order.builder()
                .customer(anna)
                .item("Кружка", 1, rubles(350))
                .delivery(new Pickup()));

        unpaid.hold();

        assertThat(paid.status()).isEqualTo(OrderStatus.ON_HOLD);
        assertThat(unpaid.status()).isEqualTo(OrderStatus.ON_HOLD);

        assertThat(new TextSalesReport(repository).render())
                .endsWith("Заказов: 4, экспресс: 1, выручка: 65840.00 руб., крупнейший: ORD-1");

        assertThat(new JsonSalesReport(repository).render())
                .contains("\"status\": \"ON_HOLD\"")
                .contains("\"revenueKopecks\": 6584000");
    }
}
