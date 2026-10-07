package nclan.ac.gameshopapp.module;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class Sale {

    private final int orderId;

    private final String customerName;

    private final Map<AbstractGame, Integer> items;

    private final double total;

    private final LocalDateTime date;

    public Sale(
            int orderId,
            String customerName,
            Map<AbstractGame, Integer> items,
            double total
    ) {

        this.orderId = orderId;

        this.customerName =
                customerName;

        this.items =
                new LinkedHashMap<>(
                        items
                );

        this.total = total;

        this.date =
                LocalDateTime.now();
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Map<AbstractGame, Integer> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {

        return "Order #"
                + orderId
                + " - "
                + customerName
                + " - £"
                + String.format(
                "%.2f",
                total
        );
    }
}