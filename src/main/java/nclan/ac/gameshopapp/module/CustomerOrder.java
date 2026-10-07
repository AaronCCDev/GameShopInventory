package nclan.ac.gameshopapp.module;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerOrder {

    private final long orderId;
    private final OffsetDateTime orderDate;
    private final double total;

    public CustomerOrder(
            long orderId,
            OffsetDateTime orderDate,
            double total
    ) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.total = total;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getFormattedDate() {
        return orderDate == null
                ? ""
                : orderDate.format(
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                )
        );
    }

    @Override
    public String toString() {
        return "Order #"
                + orderId
                + "  •  "
                + getFormattedDate()
                + "  •  £"
                + String.format("%.2f", total);
    }
}