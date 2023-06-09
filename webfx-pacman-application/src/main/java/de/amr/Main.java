package de.amr;

import de.amr.games.pacman.ui.fx.app.PacManGames2d;
import de.amr.games.pacman.ui.fx.app.PacManGames2dUI;
import de.amr.games.pacman.ui.fx.app.Settings;
import de.amr.games.pacman.ui.fx.rendering2d.ArcadeTheme;
import javafx.application.Application;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.util.Locale;

public class Main extends Application {


    private PacManGames2dUI ui;
    protected final Settings settings = new Settings();

    @Override
    public void init() {
        if (getParameters() != null) {
            settings.merge(getParameters().getNamed());
        }
        Logger.info("Game initialized: {}", settings);
    }

    @Override
    public void start(Stage stage) {
        ui = new PacManGames2dUI();
        ui.init(stage, settings, new ArcadeTheme(PacManGames2d.MGR));
        Logger.info("Game started. {} Hz language={}", ui.clock().targetFrameratePy.get(), Locale.getDefault());
    }

    @Override
    public void stop() {
        ui.clock().stop();
        Logger.info("Game stopped.");
    }
}