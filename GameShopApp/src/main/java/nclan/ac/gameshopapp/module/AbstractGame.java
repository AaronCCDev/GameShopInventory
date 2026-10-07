package nclan.ac.gameshopapp.module;

import nclan.ac.gameshopapp.enums.GameType;
import nclan.ac.gameshopapp.enums.Platform;

public abstract class AbstractGame {

    private final int id;

    private String title;
    private double price;
    private int stock;
    private Platform platform;
    private int releaseYear;

    public AbstractGame(
            int id,
            String title,
            double price,
            int stock,
            Platform platform,
            int releaseYear
    ) {

        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
        this.platform = platform;
        this.releaseYear = releaseYear;
    }

    public int getId() {
        return
                id;
    }

    public String getTitle() {
        return
                title;
    }

    public void setTitle(String title) {
        this.title
                = title;
    }

    public double getPrice() {
        return
                price;
    }

    public void setPrice(double price) {
        this.price
                = price;
    }

    public int getStock() {
        return
                stock;
    }

    public void setStock(int stock) {
        this.stock
                = stock;
    }

    public Platform getPlatform() {
        return
                platform;
    }

    public void setPlatform(Platform platform) {
        this.platform
                = platform;
    }

    public int getReleaseYear() {
        return
                releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear
                = releaseYear;
    }

    public boolean isInStock() {
        return
                stock > 0;
    }

    public boolean isRetro() {
        return releaseYear
                < 2005;
    }

    public abstract GameType getGameType();

    @Override
    public String toString() {

        String stockText;

        if (stock <= 0) {
            stockText = "OUT OF STOCK";
        } else {
            stockText = "Stock: " + stock;
        }

        return title
                + " - £"
                + String.format("%.2f", price)
                + " - "
                + stockText
                + " - "
                + platform;
    }
}