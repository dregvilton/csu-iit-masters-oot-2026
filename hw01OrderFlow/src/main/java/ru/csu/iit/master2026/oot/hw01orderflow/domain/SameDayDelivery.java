package ru.csu.iit.master2026.oot.hw01orderflow.domain;

/** Экспресс: 900 руб., в день заказа. */
public record SameDayDelivery() implements DeliveryTariff {

    private static final Money PRICE = Money.rubles(900);

    @Override
    public Money cost(Money goods) {
        return PRICE;
    }

    @Override
    public int days() {
        return 0;
    }

    @Override
    public boolean express() {
        return true;
    }

    @Override
    public String promise() {
        return "Экспресс-доставка: привезём сегодня.";
    }
}