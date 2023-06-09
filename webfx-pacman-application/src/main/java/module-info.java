// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.pacman.application {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires webfx.platform.util;

    // Exported packages
    exports de.amr.games.pacman.controller;
    exports de.amr.games.pacman.event;
    exports de.amr.games.pacman.lib;
    exports de.amr.games.pacman.model;
    exports de.amr.games.pacman.model.actors;
    exports de.amr.games.pacman.model.world;
    exports de.amr.games.pacman.ui.fx.app;
    exports de.amr.games.pacman.ui.fx.input;
    exports de.amr.games.pacman.ui.fx.rendering2d;
    exports de.amr.games.pacman.ui.fx.rendering2d.mspacman;
    exports de.amr.games.pacman.ui.fx.rendering2d.pacman;
    exports de.amr.games.pacman.ui.fx.scene;
    exports de.amr.games.pacman.ui.fx.scene2d;
    exports de.amr.games.pacman.ui.fx.util;
    exports org.tinylog;

    // Provided services
    provides javafx.application.Application with de.amr.games.pacman.ui.fx.app.PacManGames2dApp;

}