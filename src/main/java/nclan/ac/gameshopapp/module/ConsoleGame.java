package nclan.ac.gameshopapp.module;

import nclan.ac.gameshopapp.enums.GameType;
import nclan.ac.gameshopapp.enums.Platform;

public class ConsoleGame extends AbstractGame {

    public ConsoleGame(
            int id,
            String title,
            double price,
            int stock,
            Platform platform,
            int releaseYear
    ) {

        super(
                id,
                title,
                price,
                stock,
                platform,
                releaseYear
        );
    }

    @Override
    public GameType getGameType() {
        return GameType.CONSOLE_GAME;
    }
}