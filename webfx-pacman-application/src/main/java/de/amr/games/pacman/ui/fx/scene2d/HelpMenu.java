/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.ui.fx.scene2d;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.GameState;
import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import static de.amr.games.pacman.lib.Globals.oneOf;

/**
 * @author Armin Reichert
 */
public class HelpMenu extends BorderPane {

	private final FadeTransition fadingTransition;

	public HelpMenu() {
		fadingTransition = new FadeTransition(Duration.seconds(0.5), this);
		fadingTransition.setFromValue(1);
		fadingTransition.setToValue(0);
	}

	/**
	 * Makes the help root visible for given duration and then plays the close animation.
	 * 
	 * @param menus        available menu collection
	 * @param fadingDelay duration before menu starts fading out
	 */
	public void show(HelpMenuFactory menus, Duration fadingDelay) {
		var game = GameController.it().game();
		var gameState = GameController.it().state();
		Pane menu = null;
		if (gameState == GameState.INTRO) {
			menu = menus.menuIntro();
		} else if (gameState == GameState.CREDIT) {
			menu = menus.menuCredit();
		} else if (oneOf(gameState, GameState.READY, GameState.HUNTING, GameState.PACMAN_DYING, GameState.GHOST_DYING)) {
			if (game.level().isPresent()) {
				menu = game.level().get().isDemoLevel() ? menus.menuDemoLevel() : menus.menuPlaying();
			}
		}
		setCenter(menu);
		setOpacity(1);
		if (fadingTransition.getStatus() == Status.RUNNING) {
			fadingTransition.playFromStart();
		}
		fadingTransition.setDelay(fadingDelay);
		fadingTransition.play();
	}
}