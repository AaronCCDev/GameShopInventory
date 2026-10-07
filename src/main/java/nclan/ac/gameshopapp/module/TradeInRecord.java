package nclan.ac.gameshopapp.module;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class TradeInRecord {

    private final String gameTitle;
    private final OffsetDateTime tradeInDate;
    private final double discountPercent;

    public TradeInRecord(
            String gameTitle,
            OffsetDateTime tradeInDate,
            double discountPercent
    ) {
        this.gameTitle = gameTitle;
        this.tradeInDate = tradeInDate;
        this.discountPercent = discountPercent;
    }

    public String getFormattedDate() {
        return tradeInDate == null
                ? ""
                : tradeInDate.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }

    @Override
    public String toString() {
        return gameTitle
                + "  •  "
                + getFormattedDate()
                + "  •  "
                + String.format("%.0f%% discount", discountPercent);
    }
}