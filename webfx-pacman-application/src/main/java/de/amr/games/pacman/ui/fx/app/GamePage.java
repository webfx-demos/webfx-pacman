package de.amr.games.pacman.ui.fx.app;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.GameState;
import de.amr.games.pacman.model.GameLevel;
import de.amr.games.pacman.model.GameVariant;
import de.amr.games.pacman.ui.fx.PacManGames2d;
import de.amr.games.pacman.ui.fx.input.PacMouseSteering;
import de.amr.games.pacman.ui.fx.input.Keyboard;
import de.amr.games.pacman.ui.fx.rendering2d.ArcadeTheme;
import de.amr.games.pacman.ui.fx.scene.GameScene;
import de.amr.games.pacman.ui.fx.scene2d.GameScene2D;
import de.amr.games.pacman.ui.fx.util.FlashMessageView;
import de.amr.games.pacman.ui.fx.util.ResourceManager;
import de.amr.games.pacman.ui.fx.util.Ufx;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.tinylog.Logger;

public class GamePage {

    protected final PacManGames2dUI ui;
    protected final FlashMessageView flashMessageView = new FlashMessageView();
    private BorderPane root = new BorderPane();
    private BorderPane rootPane = new BorderPane();
    private Canvas canvas = new Canvas();

    private GameScene2D gameScene2D;
    private double scaling = 1.0;

    public GamePage(PacManGames2dUI ui) {
        this.ui = ui;

        rootPane.setBackground(ResourceManager.coloredBackground(Color.BLACK));
        rootPane.setCenter(canvas);
        setRootPaneBorder(ArcadeTheme.PALE, 10, 20);

        root.setBackground(ui.theme().background("wallpaper.background"));
        root.setCenter(rootPane);

        root.setOnKeyPressed(this::handleKeyPressed);
        canvas.setOnMouseClicked(this::handleMouseClickOnCanvas);
        new PacMouseSteering(this, canvas, () -> ui.game().level().map(GameLevel::pac).orElse(null));
    }

    private void setRootPaneBorder(Color color, double width, double cornerRadius) {
        rootPane.setBorder(new Border(new BorderStroke(color, BorderStrokeStyle.SOLID,
                new CornerRadii(cornerRadius), new BorderWidths(width))));
    }

    private void handleMouseClickOnCanvas(MouseEvent mouseEvent) {
        var config = ui.game().variant() == GameVariant.MS_PACMAN ? ui.configMsPacMan : ui.configPacMan;
        if (gameScene2D == config.introScene()
                || gameScene2D == config.creditScene() && ui.game().credit() == 0
                || gameScene2D == config.playScene() && ui.game().level().get().isDemoLevel()) {
            // simulate key press "5" (add credit)
            ui.addCredit();
        } else if (gameScene2D == config.creditScene() /* credit > 0 */) {
            // simulate key press "1" (start game)
            ui.startGame();
        }
    }

    public void update() {
        if (gameScene2D != null) {
            gameScene2D.update();
        }
    }

    public void render() {
        if (gameScene2D != null) {
            gameScene2D.render();
        }
        flashMessageView.update();
    }

    Parent root() {
        return root;
    }

    public void setGameScene(GameScene gameScene) {
        gameScene2D = (GameScene2D) gameScene;
        gameScene2D.setCanvas(canvas);
        scale(scaling);
        //TODO not sure if needed
        root.removeEventHandler(KeyEvent.KEY_PRESSED, ui.keyboardPlayerSteering);
        root.addEventHandler(KeyEvent.KEY_PRESSED, ui.keyboardPlayerSteering);
        root.requestFocus();
    }

    public void scale(double scaling) {
        this.scaling = scaling;
        if (gameScene2D != null) {
            gameScene2D.setScaling(scaling);
        }

        double w = Math.round( (GameScene2D.WIDTH_UNSCALED  + 30) * scaling );
        double h = Math.round( (GameScene2D.HEIGHT_UNSCALED + 15) * scaling );
        rootPane.setMinSize(w, h);
        rootPane.setMaxSize(w, h);

        double borderWidth = Math.max(5, Math.ceil(h / 60));
        double cornerRadius = Math.ceil(15 * scaling);
        Logger.info("Resize game page: scaling: {} height: {} border: {}", scaling, h, borderWidth);
        setRootPaneBorder(ArcadeTheme.PALE, borderWidth, cornerRadius);
    }

    public double getScaling() {
        return scaling;
    }

    public FlashMessageView flashMessageView() {
        return flashMessageView;
    }

    public Canvas getCanvas() {
        return canvas;
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
            ui.showHelp();
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