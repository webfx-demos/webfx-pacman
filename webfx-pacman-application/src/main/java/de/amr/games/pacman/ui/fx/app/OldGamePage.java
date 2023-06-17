/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.ui.fx.app;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.GameState;
import de.amr.games.pacman.model.GameVariant;
import de.amr.games.pacman.model.IllegalGameVariantException;
import de.amr.games.pacman.ui.fx.PacManGames2d;
import de.amr.games.pacman.ui.fx.input.Keyboard;
import de.amr.games.pacman.ui.fx.rendering2d.ArcadeTheme;
import de.amr.games.pacman.ui.fx.scene.GameScene;
import de.amr.games.pacman.ui.fx.scene2d.GameScene2D;
import de.amr.games.pacman.ui.fx.util.FlashMessageView;
import de.amr.games.pacman.ui.fx.util.ResourceManager;
import de.amr.games.pacman.ui.fx.util.Ufx;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * @author Armin Reichert
 */
public class OldGamePage {

	private static final Color FRAME_COLOR = ArcadeTheme.PALE;
	private static final int FRAME_THICKNESS = 12; // TODO should be adapted to size of game scene
	private static final int HELP_BUTTON_SIZE = 24;

	protected final PacManGames2dUI ui;
	protected final StackPane root;
	protected final FlashMessageView flashMessageView = new FlashMessageView();
	protected final BorderPane sceneContainer;
	protected final Pane helpButton;

	public OldGamePage(PacManGames2dUI ui) {
		this.ui = ui;

		sceneContainer = new BorderPane();
		sceneContainer.setBackground(ResourceManager.coloredBackground(Color.BLACK));
		sceneContainer.setMaxSize(1, 1); // gets resized with content
		sceneContainer.setScaleX(0.9);
		sceneContainer.setScaleY(0.9);

		helpButton = new VBox(createHelpButtonIcon(ui.game().variant()));
		helpButton.setPadding(new Insets(4));
		helpButton.setMaxSize(HELP_BUTTON_SIZE, HELP_BUTTON_SIZE);
		helpButton.setCursor(Cursor.HAND);
		helpButton.setOnMouseClicked(e -> {
			if (e.getClickCount() == 1 && e.getButton() == MouseButton.PRIMARY) {
				//ui.showHelp();
			}
		});

		root = new StackPane(sceneContainer, flashMessageView);
		root.setBackground(ui.theme().background("wallpaper.background"));
		root.setOnKeyPressed(this::handleKeyPressed);
	}

	public void setGameScene(GameScene gameScene) {
		var scene2D = (GameScene2D) gameScene;
		scene2D.setRoundedCorners(false);
		if (isPlayScene(scene2D)) {
			sceneContainer.setPadding(new Insets(0, 20, 0, 20));
			root.addEventHandler(KeyEvent.KEY_PRESSED, ui.keyboardPlayerSteering);
		} else {
			sceneContainer.setPadding(new Insets(0));
			root.removeEventHandler(KeyEvent.KEY_PRESSED, ui.keyboardPlayerSteering);
		}
		updateHelpButton(gameScene);
		sceneContainer.setCenter(new StackPane(scene2D.root(), helpButton));
		StackPane.setAlignment(helpButton, Pos.TOP_RIGHT);
		root.requestFocus();
	}

	private boolean isPlayScene(GameScene gameScene) {
		var gameVariant = ui.game().variant();
		if (gameVariant == GameVariant.MS_PACMAN) {
			return gameScene == ui.configMsPacMan.playScene();
		} else if (gameVariant == GameVariant.PACMAN) {
			return gameScene == ui.configPacMan.playScene();
		}
		throw new IllegalGameVariantException(gameVariant);
	}

	private boolean isHelpAvailable(GameScene gameScene) {
		var state = gameScene.gameController().state();
		if (state == GameState.BOOT || state == GameState.INTERMISSION || state == GameState.INTERMISSION_TEST) {
			return false;
		}
		return true;
	}

	private ImageView createHelpButtonIcon(GameVariant variant) {
		var icon = new ImageView(
				ui.theme().image(variant == GameVariant.MS_PACMAN ? "mspacman.helpButton.icon" : "pacman.helpButton.icon"));
		icon.setFitWidth(HELP_BUTTON_SIZE);
		icon.setFitHeight(HELP_BUTTON_SIZE);
//		icon.setSmooth(true);
		return icon;
	}

	private void updateHelpButton(GameScene gameScene) {
		if (isHelpAvailable(gameScene)) {
			helpButton.getChildren().setAll(createHelpButtonIcon(ui.game().variant()));
			helpButton.setVisible(true);
		} else {
			helpButton.setVisible(false); // or gray out etc.
		}
	}

	public Pane root() {
		return root;
	}

	public Node helpButton() {
		return helpButton;
	}

	public FlashMessageView flashMessageView() {
		return flashMessageView;
	}

	public void update() {
		if (ui.currentGameScene() != null) {
			ui.currentGameScene().update();
		}
	}

	public void render() {
		if (ui.currentGameScene() != null) {
			ui.currentGameScene().render();
		}
		flashMessageView.update();
	}

	public void setBackground(Background background) {
		root.setBackground(background);
	}

	public void addLayer(Node layer) {
		root.getChildren().add(layer);
	}

	protected void handleKeyPressed(KeyEvent keyEvent) {
		Keyboard.accept(keyEvent);
		handleKeyboardInput();
		if (ui.currentGameScene() != null) {
			ui.currentGameScene().handleKeyboardInput();
		}
		Keyboard.clearState();
	}

	protected void handleKeyboardInput() {
		if (Keyboard.pressed(PacManGames2d.KEY_SHOW_HELP)) {
			//ui.showHelp();
		} else if (Keyboard.pressed(PacManGames2d.KEY_AUTOPILOT)) {
			ui.toggleAutopilot();
		} else if (Keyboard.pressed(PacManGames2d.KEY_BOOT)) {
			if (GameController.it().state() != GameState.BOOT) {
				ui.reboot();
			}
		} else if (Keyboard.pressed(PacManGames2d.KEY_DEBUG_INFO)) {
			Ufx.toggle(PacManGames2d.PY_SHOW_DEBUG_INFO);
		} else if (Keyboard.pressed(PacManGames2d.KEY_IMMUNITIY)) {
			ui.toggleImmunity();
		} else if (Keyboard.pressed(PacManGames2d.KEY_PAUSE)) {
			ui.togglePaused();
		} else if (Keyboard.pressed(PacManGames2d.KEY_PAUSE_STEP) || Keyboard.pressed(PacManGames2d.KEY_SINGLE_STEP)) {
			ui.oneSimulationStep();
		} else if (Keyboard.pressed(PacManGames2d.KEY_TEN_STEPS)) {
			ui.tenSimulationSteps();
		} else if (Keyboard.pressed(PacManGames2d.KEY_SIMULATION_FASTER)) {
			ui.changeSimulationSpeed(5);
		} else if (Keyboard.pressed(PacManGames2d.KEY_SIMULATION_SLOWER)) {
			ui.changeSimulationSpeed(-5);
		} else if (Keyboard.pressed(PacManGames2d.KEY_SIMULATION_NORMAL)) {
			ui.resetSimulationSpeed();
		} else if (Keyboard.pressed(PacManGames2d.KEY_QUIT)) {
			if (GameController.it().state() != GameState.BOOT && GameController.it().state() != GameState.INTRO) {
				ui.restartIntro();
			}
		} else if (Keyboard.pressed(PacManGames2d.KEY_TEST_LEVELS)) {
			ui.startLevelTestMode();
		} else if (Keyboard.pressed(PacManGames2d.KEY_FULLSCREEN)) {
			ui.stage.setFullScreen(true);
		}
	}

}