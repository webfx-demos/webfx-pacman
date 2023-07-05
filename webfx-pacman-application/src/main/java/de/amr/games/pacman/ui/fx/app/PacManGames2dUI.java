/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.ui.fx.app;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.GameState;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.event.GameEventListener;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.event.SoundEvent;
import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.model.GameVariant;
import de.amr.games.pacman.model.IllegalGameVariantException;
import de.amr.games.pacman.ui.fx.input.KeyboardSteering;
import de.amr.games.pacman.ui.fx.rendering2d.mspacman.GhostAnimationsMsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.mspacman.PacAnimationsMsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.mspacman.SpritesheetMsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.pacman.GhostAnimationsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.pacman.PacAnimationsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.pacman.SpritesheetPacManGame;
import de.amr.games.pacman.ui.fx.scene.GameScene;
import de.amr.games.pacman.ui.fx.scene.GameSceneConfiguration;
import de.amr.games.pacman.ui.fx.scene2d.*;
import de.amr.games.pacman.ui.fx.util.*;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import dev.webfx.kit.util.scene.DeviceSceneUtil;
import de.amr.games.pacman.ui.fx.*;

import static de.amr.games.pacman.controller.GameState.INTRO;
import static de.amr.games.pacman.lib.Globals.checkNotNull;

/**
 * 2D-only user interface for Pac-Man and Ms. Pac-Man games. No dashboard, no picture-in-picture view.
 * 
 * @author Armin Reichert
 */
public class PacManGames2dUI implements GameEventListener {

	protected GameSceneConfiguration configMsPacMan;
	protected GameSceneConfiguration configPacMan;
	protected GameClock clock;
	protected Theme theme;
	protected Stage stage;
	protected Scene scene;
	protected StartPage startPage;
	protected GamePage gamePage;
	protected HelpMenuFactory helpMenuFactory;
	protected KeyboardSteering keyboardPlayerSteering;
	protected SoundHandler soundHandler;
	protected GameScene currentGameScene;
	protected boolean showingStartPage;

	public void init(Stage stage, Settings settings, Theme theme) {
		checkNotNull(stage);
		checkNotNull(settings);
		checkNotNull(theme);

		this.stage = stage;
		this.theme = theme;

		GameController.create(settings.variant);

		clock = new GameClock(this::onTick, this::onRender);
		clock.pausedPy.addListener((py, ov, nv) -> updateStage());
		clock.targetFrameratePy.set(GameModel.FPS);

		soundHandler = new SoundHandler(theme);

		var screenSize = Screen.getPrimary().getBounds();
		double height = screenSize.getHeight() * 0.8;
		double width = height * 4.0 / 3.0;
		scene = DeviceSceneUtil.newScene(new Region(), width, height, Color.BLACK);
		scene.widthProperty().addListener((py, ov, nv) -> {
			if (!showingStartPage) {
				resizeGamePage(scene.getWidth(), scene.getHeight());
			}});
		scene.heightProperty().addListener((py, ov, nv) -> {
			if (!showingStartPage) {
				resizeGamePage(scene.getWidth(), scene.getHeight());
			}});

		configureGameScenes();
		createStartPage();
		createGamePage();
		configureHelpMenus();
		configurePacSteering();
		configureBindings(settings);
		GameController.addListener(this);

		stage.setScene(scene);
		stage.setFullScreen(settings.fullScreen);
		stage.centerOnScreen();
		stage.show();

		showStartPage();
	}

	protected void onTick() {
		if (!showingStartPage) {
			GameController.it().update();
			gamePage.update();
		}
	}

	protected void onRender()
	{
		if (!showingStartPage) {
			gamePage.render();
		}
	}

	protected void configureGameScenes() {
		configMsPacMan = new GameSceneConfiguration(
			new BootScene(this),
			new MsPacManIntroScene(this),
			new MsPacManCreditScene(this),
			new PlayScene2D(this),
			null,
			new MsPacManCutscene1(this),
			new MsPacManCutscene2(this),
			new MsPacManCutscene3(this)
		);
		configPacMan = new GameSceneConfiguration(
			new BootScene(this),
			new PacManIntroScene(this),
			new PacManCreditScene(this),
			new PlayScene2D(this),
			null,
			new PacManCutscene1(this),
			new PacManCutscene2(this),
			new PacManCutscene3(this)
		);
	}

	protected void createStartPage() {
		startPage = new StartPage(this);
	}

	protected void showStartPage() {
		clock.stop();
		startPage.setGameVariant(game().variant());
		scene.setRoot(startPage.root());
		startPage.root().requestFocus();
		updateStage();
		showingStartPage = true;
	}

	protected void createGamePage() {
		gamePage = new GamePage(this);
	}

	private void resizeGamePage(double width, double height) {
		double s = 0.9 * height / PacManGames2d.CANVAS_HEIGHT_UNSCALED;
		if (s * PacManGames2d.CANVAS_WIDTH_UNSCALED > 0.8 * width) {
			s = 0.8 * width / PacManGames2d.CANVAS_WIDTH_UNSCALED;
		}
		s = Math.floor(s * 10) / 10; // round scaling factor to first decimal digit
		gamePage.resize(s);
	}

	protected void showGamePage() {
		reboot();
		scene.setRoot(gamePage.root());
		resizeGamePage(scene.getWidth(), scene.getHeight());
		updateStage();
		showingStartPage = false;
		gamePage.root().requestFocus();
		clock.start();
	}

	protected void configureHelpMenus() {
		helpMenuFactory = new HelpMenuFactory();
		helpMenuFactory.setFont(theme.font("font.monospaced", 24));
	}

	protected void configurePacSteering() {
		keyboardPlayerSteering = new KeyboardSteering();
		GameController.it().setManualPacSteering(keyboardPlayerSteering);
	}

	protected void updateStage() {
		switch (game().variant()) {
		case MS_PACMAN: {
			stage.setTitle("Ms. Pac-Man (WebFX)");
			stage.getIcons().setAll(theme.image("mspacman.icon"));
			break;
		}
		case PACMAN: {
			stage.setTitle("Pac-Man (WebFX)");
			stage.getIcons().setAll(theme.image("pacman.icon"));
			break;
		}
		default:
			throw new IllegalGameVariantException(game().variant());
		}
	}

	/**
	 * @param settings application settings
	 */
	protected void configureBindings(Settings settings) {
		// snooze...
	}

	protected GameScene sceneMatchingCurrentGameState() {
		var config = game().variant() == GameVariant.MS_PACMAN ? configMsPacMan : configPacMan;
		switch (GameController.it().state()) {
		case BOOT:
			return config.bootScene();
		case CREDIT:
			return config.creditScene();
		case INTRO:
			return config.introScene();
		case INTERMISSION:
			return config.cutScene(game().level().get().intermissionNumber);
		case INTERMISSION_TEST:
			return config.cutScene(game().intermissionTestNumber);
		default:
			return config.playScene();
		}
	}

	protected void updateOrReloadGameScene(boolean reload) {
		var nextGameScene = sceneMatchingCurrentGameState();
		if (nextGameScene == null) {
			throw new IllegalStateException("No game scene found for game state " + GameController.it().state());
		}
		if (reload || nextGameScene != currentGameScene) {
			setGameScene(nextGameScene);
		}
		updateStage();
	}

	protected void setGameScene(GameScene newGameScene) {
		var prevGameScene = currentGameScene;
		if (prevGameScene != null) {
			prevGameScene.end();
		}
		currentGameScene = newGameScene;
		currentGameScene.setParentScene(stage.getScene());
		currentGameScene.init();
		gamePage.setGameScene(currentGameScene);
		Logger.trace("Game scene changed from {} to {}", prevGameScene, currentGameScene);
	}

	@Override
	public void onGameEvent(GameEvent e) {
		Logger.trace("Event received: {}", e);
		// call event specific handler
		GameEventListener.super.onGameEvent(e);
		if (currentGameScene != null) {
			currentGameScene.onGameEvent(e);
		}
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		updateOrReloadGameScene(false);
	}

	@Override
	public void onUnspecifiedChange(GameEvent e) {
		updateOrReloadGameScene(true);
	}

	@Override
	public void onLevelCreated(GameEvent e) {
		// Found no better point in time to create and assign the sprite animations to the guys
		e.game.level().ifPresent(level -> {
			switch (level.game().variant()) {
			case MS_PACMAN: {
				var ss = (SpritesheetMsPacManGame) spritesheet();
				level.pac().setAnimations(new PacAnimationsMsPacManGame(level.pac(), ss));
				level.ghosts().forEach(ghost -> ghost.setAnimations(new GhostAnimationsMsPacManGame(ghost, ss)));
				Logger.trace("Created Ms. Pac-Man game creature animations for level #{}", level.number());
				break;
			}
			case PACMAN: {
				var ss = (SpritesheetPacManGame) spritesheet();
				level.pac().setAnimations(new PacAnimationsPacManGame(level.pac(), ss));
				level.ghosts().forEach(ghost -> ghost.setAnimations(new GhostAnimationsPacManGame(ghost, ss)));
				Logger.trace("Created Pac-Man game creature animations for level #{}", level.number());
				break;
			}
			default:
				throw new IllegalGameVariantException(level.game().variant());
			}
		});
		updateOrReloadGameScene(true);
	}

	@Override
	public void onSoundEvent(SoundEvent e) {
		soundHandler.onSoundEvent(e);
	}

	public void showFlashMessage(String message, Object... args) {
		showFlashMessageSeconds(1, message, args);
	}

	public void showFlashMessageSeconds(double seconds, String message, Object... args) {
//		gamePage.flashMessageView().showMessage(String.format(message, args), seconds);
		gamePage.flashMessageView().showMessage(message, seconds);
	}

	public Theme theme() {
		return theme;
	}

	public SoundHandler soundHandler() {
		return soundHandler;
	}

	public GameClock clock() {
		return clock;
	}

	public GameScene currentGameScene() {
		return currentGameScene;
	}

	public GameModel game() {
		return GameController.it().game();
	}

	public Spritesheet spritesheet() {
		switch (game().variant()) {
		case MS_PACMAN:
			return theme().get("mspacman.spritesheet");
		case PACMAN:
			return theme().get("pacman.spritesheet");
		default:
			throw new IllegalGameVariantException(game().variant());
		}
	}

	// Actions

	public void startGame() {
		if (game().hasCredit()) {
			soundHandler.stopVoice();
			GameController.it().startPlaying();
		}
	}

	public void startCutscenesTest() {
		GameController.it().startCutscenesTest(1);
		showFlashMessage("Cut scenes");
	}

	public void restartIntro() {
		if (currentGameScene != null) {
			currentGameScene.end();
			soundHandler.stopAllSounds();
			GameController.setSoundEventsEnabled(true);
			if (game().isPlaying()) {
				game().changeCredit(-1);
			}
			GameController.it().restart(INTRO);
		}
	}

	public void reboot() {
		if (currentGameScene != null) {
			currentGameScene.end();
		}
		soundHandler().playVoice("voice.explain");
		GameController.it().restart(GameState.BOOT);
	}

	public void addCredit() {
		GameController.setSoundEventsEnabled(true);
		GameController.it().addCredit();
	}

	public void togglePaused() {
		Ufx.toggle(clock.pausedPy);
		if (clock.pausedPy.get()) {
			theme.audioClips().forEach(AudioClip::stop);
		}
		showFlashMessage(clock.isPaused() ? "Game PAUSED" : "Game RESUMED");
	}

	public void oneSimulationStep() {
		if (clock.pausedPy.get()) {
			clock.executeSingleStep(true);
		}
	}

	public void tenSimulationSteps() {
		if (clock.pausedPy.get()) {
			clock.executeSteps(10, true);
		}
	}

	public void changeSimulationSpeed(int delta) {
		int newFramerate = clock.targetFrameratePy.get() + delta;
		if (newFramerate > 0) {
			clock.targetFrameratePy.set(newFramerate);
			showFlashMessageSeconds(0.75, newFramerate + "Hz");
		}
	}

	public void resetSimulationSpeed() {
		clock.targetFrameratePy.set(GameModel.FPS);
		showFlashMessageSeconds(0.75, clock.targetFrameratePy.get() + "Hz");
	}

	public void switchGameVariant() {
		var variant = game().variant().next();
		GameController.it().selectGameVariant(variant);
		showStartPage();
	}

	public void toggleAutopilot() {
		GameController.it().toggleAutoControlled();
		var auto = GameController.it().isAutoControlled();
		String message = auto ? "Autopilot ON" : "Autopilot OFF";
		showFlashMessage(message);
		soundHandler.playVoice(auto ? "voice.autopilot.on" : "voice.autopilot.off");
	}

	public void toggleImmunity() {
		game().setImmune(!game().isImmune());
		var immune = game().isImmune();
		String message = immune ? "Immunity ON" : "Immunity OFF";
		showFlashMessage(message);
		soundHandler.playVoice(immune ? "voice.immunity.on" : "voice.immunity.off");
	}

	public void cheatAddLives() {
		int newLivesCount = game().lives() + 3;
		game().setLives(newLivesCount);
		showFlashMessage("You have " + newLivesCount + " lives now");
	}

	public void cheatEatAllPellets() {
		GameController.it().cheatEatAllPellets();
	}

	public void cheatEnterNextLevel() {
		GameController.it().cheatEnterNextLevel();
	}

	public void cheatKillAllEatableGhosts() {
		GameController.it().cheatKillAllEatableGhosts();
	}
}