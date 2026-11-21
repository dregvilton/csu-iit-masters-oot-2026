package ru.csu.iit.master2026.oot.hw01orderflow.app;

import ru.csu.iit.master2026.oot.hw01orderflow.domain.Order;

/** Отчёт в формате JSON: суммы в копейках. */
public final class JsonSalesReport extends SalesReport {

    private boolean firstOrder;

    public JsonSalesReport(OrderRepository orders) {
        super(orders);
    }

    @Override
    protected void header(StringBuilder out) {
        firstOrder = true;
        out.append("{\n  \"orders\": [\n");
    }

    @Override
    protected void row(StringBuilder out, Order order) {
        if (!firstOrder) {
            out.append(",\n");
        }

        out.append("    {\"id\": \"").append(order.id())
                .append("\", \"status\": \"").append(order.status())
                .append("\", \"totalKopecks\": ")
                .append(order.total().kopecks()).append('}');

        firstOrder = false;
    }

    @Override
    protected void footer(StringBuilder out, Totals totals) {
        if (!firstOrder) {
            out.append('\n');
        }

        out.append("  ],\n")
                .append("  \"count\": ").append(totals.count()).append(",\n")
                .append("  \"express\": ").append(totals.express()).append(",\n")
                .append("  \"revenueKopecks\": ").append(totals.revenue().kopecks()).append(",\n")
                .append("  \"largest\": ")
                .append(totals.largest()
                        .map(id -> "\"" + id + "\"")
                        .orElse("null"))
                .append("\n}\n");
    }
}