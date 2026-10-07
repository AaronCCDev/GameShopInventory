package nclan.ac.gameshopapp.module;

public class OrderItem {

    private final String gameTitle;
    private final int quantity;
    private final double price;
    private final double lineTotal;

    public OrderItem(
            String gameTitle,
            int quantity,
            double price,
            double lineTotal
    ) {
        this.gameTitle = gameTitle;
        this.quantity = quantity;
        this.price = price;
        this.lineTotal = lineTotal;
    }

    @Override
    public String toString() {
        return gameTitle
                + "  ×" + quantity
                + "  •  £" + String.format("%.2f", price)
                + " each"
                + "  •  £" + String.format("%.2f", lineTotal);
    }
}