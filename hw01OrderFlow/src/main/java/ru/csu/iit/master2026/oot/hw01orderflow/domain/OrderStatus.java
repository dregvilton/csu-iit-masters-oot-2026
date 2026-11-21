
package ru.csu.iit.master2026.oot.hw01orderflow.domain;

/**
 * Статус заказа. Паттерн «Состояние»: каждый статус сам знает, куда из него можно перейти.
 *
 * <p>Переходы: NEW → PAID → SHIPPED; NEW и PAID → CANCELLED.
 * NEW и PAID → ON_HOLD; ON_HOLD → предыдущий статус или CANCELLED.
 * Остальное — исключение.
 */
public enum OrderStatus {

    NEW {
        @Override
        OrderStatus pay() {
            return PAID;
        }

        @Override
        OrderStatus cancel() {
            return CANCELLED;
        }

        @Override
        OrderStatus hold() {
            return ON_HOLD;
        }
    },

    PAID {
        @Override
        OrderStatus ship() {
            return SHIPPED;
        }

        @Override
        OrderStatus cancel() {
            return CANCELLED;
        }

        @Override
        OrderStatus hold() {
            return ON_HOLD;
        }

        @Override
        public boolean paid() {
            return true;
        }
    },

    SHIPPED {
        @Override
        public boolean paid() {
            return true;
        }
    },

    ON_HOLD {
        @Override
        OrderStatus resume(OrderStatus previous) {
            return previous;
        }

        @Override
        OrderStatus cancel() {
            return CANCELLED;
        }
    },

    CANCELLED;

    /** Переходы по умолчанию запрещены: статус разрешает только свои. */
    OrderStatus pay() {
        throw forbidden("оплатить");
    }

    OrderStatus ship() {
        throw forbidden("отправить");
    }

    OrderStatus cancel() {
        throw forbidden("отменить");
    }

    OrderStatus hold() {
        throw forbidden("приостановить");
    }

    OrderStatus resume(OrderStatus previous) {
        throw forbidden("возобновить");
    }

    /** Деньги за заказ получены. */
    public boolean paid() {
        return false;
    }

    private IllegalStateException forbidden(String action) {
        return new IllegalStateException("Нельзя " + action + " заказ в статусе " + this);
    }
}
