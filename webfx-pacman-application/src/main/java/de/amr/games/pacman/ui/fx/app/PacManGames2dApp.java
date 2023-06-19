/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.ui.fx.app;

import de.amr.games.pacman.model.GameVariant;
import de.amr.games.pacman.ui.fx.PacManGames2d;
import de.amr.games.pacman.ui.fx.rendering2d.ArcadeTheme;
import de.amr.games.pacman.ui.fx.util.ResourceManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.tinylog.Logger;
import dev.webfx.platform.windowlocation.WindowLocation;
import dev.webfx.kit.util.scene.DeviceSceneUtil;

import java.util.Locale;
import java.util.Optional;

/**
 * @author Armin Reichert
 */
public class PacManGames2dApp extends Application {
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
		settings.variant = getGameVariantFromHostname().orElse(GameVariant.MS_PACMAN);
		ui.init(stage, settings, new ArcadeTheme(PacManGames2d.MGR));
		//TODO What is this good for?
		DeviceSceneUtil.onFontsAndImagesLoaded(() -> {
			Logger.info("{} images loaded", PacManGames2d.MGR.numLoadedImages());
		}, PacManGames2d.MGR.getLoadedImages());
		Logger.info("Game started. {} Hz language={}", ui.clock().targetFrameratePy.get(), Locale.getDefault());
	}

	@Override
	public void stop() {
		ui.clock().stop();
		Logger.info("Game stopped.");
	}

	private static Optional<GameVariant> getGameVariantFromHostname() {
		var hostname = WindowLocation.getHostname();
		if (hostname != null) {
			if (hostname.startsWith("pacman"))
				return Optional.of(GameVariant.PACMAN);
			if (hostname.startsWith("mspacman"))
				return Optional.of(GameVariant.MS_PACMAN);
		}
		return Optional.empty();
	}
}