package de.amr.games.pacman.ui.fx.app;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.GameState;
import de.amr.games.pacman.model.GameLevel;
import de.amr.games.pacman.model.GameVariant;
import de.amr.games.pacman.ui.fx.PacManGames2d;
import de.amr.games.pacman.ui.fx.input.Keyboard;
import de.amr.games.pacman.ui.fx.input.PacMouseSteering;
import de.amr.games.pacman.ui.fx.rendering2d.ArcadeTheme;
import de.amr.games.pacman.ui.fx.scene.GameScene;
import de.amr.games.pacman.ui.fx.scene.GameSceneConfiguration;
import de.amr.games.pacman.ui.fx.scene2d.GameScene2D;
import de.amr.games.pacman.ui.fx.scene2d.HelpButton;
import de.amr.games.pacman.ui.fx.scene2d.HelpMenu;
import de.amr.games.pacman.ui.fx.scene2d.HelpMenuFactory;
import de.amr.games.pacman.ui.fx.util.FlashMessageView;
import de.amr.games.pacman.ui.fx.util.Logger;
import de.amr.games.pacman.ui.fx.util.ResourceManager;
import de.amr.games.pacman.ui.fx.util.Ufx;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import static de.amr.games.pacman.lib.Globals.oneOf;

/**
 * @author Armin Reichert
 */
public class GamePage {

    private static final Color  BORDER_COLOR         = ArcadeTheme.PALE;
    private static final double BORDER_WIDTH         = 10;
    private static final double BORDER_CORNER_RADIUS = 20;

    private static final Duration MENU_FADING_DELAY = Duration.seconds(1.5);

    private final PacManGames2dUI ui;
    private final FlashMessageView flashMessageView = new FlashMessageView();
    private final StackPane root = new StackPane();
    private final BorderPane layoutPane = new BorderPane();
    private final BorderPane rootPane = new BorderPane();
    private final Canvas canvas = new Canvas();
    private final Pane popupLayer = new Pane();
    private final HelpMenuFactory helpMenuFactory = new HelpMenuFactory();
    private final HelpMenu helpMenu = new HelpMenu();
    private final HelpButton helpButton = new HelpButton();
    private final Signature signature = new Signature();

    private GameScene2D gameScene2D;
    private double scaling = 1.0;

    public GamePage(PacManGames2dUI ui) {
        this.ui = ui;

        root.getChildren().addAll(layoutPane, popupLayer, flashMessageView);

        //TODO in desktop version, corners are black, in GWT they are transparent (bug?) what is wanted here
        rootPane.setBackground(ResourceManager.coloredBackground(Color.BLACK));
        rootPane.setBorder(ResourceManager.roundedBorder(BORDER_COLOR, BORDER_CORNER_RADIUS, BORDER_WIDTH));
        rootPane.setCenter(canvas);
        rootPane.heightProperty().addListener((py, ov, nv) -> resize(scaling));

        layoutPane.setBackground(ui.theme().background("wallpaper.background"));
        layoutPane.setCenter(rootPane);

        popupLayer.getChildren().addAll(helpButton, signature.root(), helpMenu);

        helpButton.setOnMouseClicked(e -> {
            Logger.info("Mouse clicked: {}", e);
            e.consume();
            Logger.info("Mouse event consumed");
            showHelpMenu();
        });
        root.setOnKeyPressed(this::handleKeyPressed);
        popupLayer.setOnMouseClicked(this::handleMouseClick);
        new PacMouseSteering(this, popupLayer, () -> ui.game().level().map(GameLevel::pac).orElse(null));

        // For debugging draw borders
        PacManGames2d.PY_SHOW_DEBUG_INFO.addListener((py, ov, debug) -> {
            if (debug) {
                root.setBorder(ResourceManager.border(Color.RED, 3));
                layoutPane.setBorder(ResourceManager.border(Color.YELLOW, 3));
                popupLayer.setBorder(ResourceManager.border(Color.GREENYELLOW, 3));
            } else {
                root.setBorder(null);
                layoutPane.setBorder(null);
                popupLayer.setBorder(null);
            }
        });
    }

    private void handleMouseClick(MouseEvent mouseEvent) {
        Logger.info("Mouse clicked: {}", mouseEvent);
        if (mouseEvent.getButton() != MouseButton.PRIMARY) {
            Logger.info("Ignored: Not primary mouse button");
        }
        var config = sceneConfiguration();
        if (gameScene2D == config.introScene()
                || gameScene2D == config.creditScene() && ui.game().credit() == 0
                || gameScene2D == config.playScene() && ui.game().level().get().isDemoLevel()) {
            ui.addCredit(); // simulate key press "5" (add credit)
        } else if (gameScene2D == config.creditScene() /* credit > 0 */) {
            ui.startGame(); // simulate key press "1" (start game)
        }
    }

    private void showHelpMenu() {
        helpMenuFactory.setFont(ui.theme().font("font.monospaced", Math.max(6, 14 * scaling)));
        helpMenu.show(currentHelpMenu(), MENU_FADING_DELAY);
        helpMenu.setTranslateX(10 * scaling);
        helpMenu.setTranslateY(30 * scaling);
    }

    private Pane currentHelpMenu() {
        var gameState = GameController.it().state();
        if (gameState == GameState.INTRO) {
            return helpMenuFactory.menuIntro();
        }
        if (gameState == GameState.CREDIT) {
            return helpMenuFactory.menuCredit();
        }
        if (ui.game().level().isPresent()
                && oneOf(gameState, GameState.READY, GameState.HUNTING, GameState.PACMAN_DYING, GameState.GHOST_DYING)) {
                return ui.game().level().get().isDemoLevel() ? helpMenuFactory.menuDemoLevel() : helpMenuFactory.menuPlaying();
        }
        return null;
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
        resize(scaling);
        if (gameScene == sceneConfiguration().playScene()) {
            root.addEventHandler(KeyEvent.KEY_PRESSED, ui.keyboardPlayerSteering);
        } else {
            root.removeEventHandler(KeyEvent.KEY_PRESSED, ui.keyboardPlayerSteering);
        }
        updateHelpButton();
        if (gameScene == sceneConfiguration().introScene()) {
            signature.showAfterSeconds(3);
        } else {
            signature.hide();
        }
        root.requestFocus();
    }

    private GameSceneConfiguration sceneConfiguration() {
        return ui.game().variant() == GameVariant.MS_PACMAN ? ui.configMsPacMan : ui.configPacMan;
    }

    public void resize(double scaling) {
        if (scaling < 0.8) {
            Logger.info("Cannot scale down further");
            return;
        }

        this.scaling = scaling;
        double w = Math.round( (GameScene2D.WIDTH_UNSCALED  + 30) * scaling );
        double h = Math.round( (GameScene2D.HEIGHT_UNSCALED + 15) * scaling );

        rootPane.setMinSize (w, h);
        rootPane.setPrefSize(w, h);
        rootPane.setMaxSize (w, h);

        popupLayer.setMinSize (w, h);
        popupLayer.setPrefSize(w, h);
        popupLayer.setMaxSize (w, h);

        double borderWidth  = Math.max(5, Math.ceil(h / 60));
        double cornerRadius = Math.ceil(15 * scaling);
        rootPane.setBorder(ResourceManager.roundedBorder(ArcadeTheme.PALE, cornerRadius, borderWidth));

        if (gameScene2D != null) {
            gameScene2D.setScaling(scaling);
        }
        updateHelpButton();
        updateSignature();

        Logger.info("Resized game page: scaling: {} height: {} border: {}", scaling, h, borderWidth);
    }

    private void updateHelpButton() {
        String key = ui.game().variant() == GameVariant.MS_PACMAN ? "mspacman.helpButton.icon" : "pacman.helpButton.icon";
        helpButton.setImage(ui.theme().image(key), Math.ceil(10 * scaling));
        helpButton.setTranslateX(popupLayer.getWidth() - 20 * scaling);
        helpButton.setTranslateY(8 * scaling);
        helpButton.setVisible(sceneConfiguration().bootScene() != gameScene2D);
    }

    private void updateSignature() {
        signature.setMadeByFont(Font.font("Helvetica", Math.floor(10 * scaling)));
        signature.setNameFont(ui.theme().font("font.handwriting", Math.floor(12 * scaling)));
        if (ui.game().variant() == GameVariant.MS_PACMAN) {
            signature.root().setTranslateX(50 * scaling);
            signature.root().setTranslateY(40 * scaling);
        } else {
            signature.root().setTranslateX(50 * scaling);
            signature.root().setTranslateY(28 * scaling);
        }
    }

    public double getScaling() {
        return scaling;
    }

    public FlashMessageView flashMessageView() {
        return flashMessageView;
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
        var gameState = GameController.it().state();
        if (Keyboard.pressed(PacManGames2d.KEY_SHOW_HELP)) {
            showHelpMenu();
        } else if (Keyboard.pressed(PacManGames2d.KEY_AUTOPILOT)) {
            ui.toggleAutopilot();
        } else if (Keyboard.pressed(PacManGames2d.KEY_BOOT)) {
            if (gameState != GameState.BOOT) {
                ui.reboot();
            }
        } else if (Keyboard.pressed(PacManGames2d.KEY_DEBUG_INFO)) {
            Ufx.toggle(PacManGames2d.PY_SHOW_DEBUG_INFO);
        } else if (Keyboard.pressed(PacManGames2d.KEY_IMMUNITY)) {
            ui.toggleImmunity();
        } else if (Keyboard.pressed(PacManGames2d.KEY_PAUSE)) {
            ui.togglePaused();
        } else if (Keyboard.pressed(PacManGames2d.KEY_PAUSE_STEP) || Keyboard.pressed(PacManGames2d.KEY_SINGLE_STEP)) {
            ui.oneSimulationStep();
        } else if (Keyboard.pressed(PacManGames2d.KEY_TEN_STEPS)) {
//            ui.tenSimulationSteps();
        } else if (Keyboard.pressed(PacManGames2d.KEY_SIMULATION_FASTER)) {
            ui.changeSimulationSpeed(5);
        } else if (Keyboard.pressed(PacManGames2d.KEY_SIMULATION_SLOWER)) {
            ui.changeSimulationSpeed(-5);
        } else if (Keyboard.pressed(PacManGames2d.KEY_SIMULATION_NORMAL)) {
            ui.resetSimulationSpeed();
        } else if (Keyboard.pressed(PacManGames2d.KEY_QUIT)) {
            if (gameState != GameState.BOOT && gameState != GameState.INTRO) {
                ui.restartIntro();
            }
        } else if (Keyboard.pressed(PacManGames2d.KEY_TEST_LEVELS)) {
//            ui.startLevelTestMode();
        } else if (Keyboard.pressed(PacManGames2d.KEY_FULLSCREEN)) {
            ui.stage.setFullScreen(true);
        }
    }
}