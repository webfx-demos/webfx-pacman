// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.pacman.application {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires webfx.kit.util.scene;
    requires webfx.platform.console;
    requires webfx.platform.resource;
    requires webfx.platform.storage;
    requires webfx.platform.util;
    requires webfx.platform.windowlocation;

    // Exported packages
    exports de.amr.games.pacman.controller;
    exports de.amr.games.pacman.event;
    exports de.amr.games.pacman.lib;
    exports de.amr.games.pacman.model;
    exports de.amr.games.pacman.model.actors;
    exports de.amr.games.pacman.model.world;
    exports de.amr.games.pacman.ui.fx;
    exports de.amr.games.pacman.ui.fx.app;
    exports de.amr.games.pacman.ui.fx.input;
    exports de.amr.games.pacman.ui.fx.rendering2d;
    exports de.amr.games.pacman.ui.fx.rendering2d.mspacman;
    exports de.amr.games.pacman.ui.fx.rendering2d.pacman;
    exports de.amr.games.pacman.ui.fx.scene;
    exports de.amr.games.pacman.ui.fx.scene2d;
    exports de.amr.games.pacman.ui.fx.util;

    // Resources packages
    opens de.amr.games.pacman.ui.fx.fonts;
    opens de.amr.games.pacman.ui.fx.graphics;
    opens de.amr.games.pacman.ui.fx.graphics.icons;
    opens de.amr.games.pacman.ui.fx.graphics.mspacman;
    opens de.amr.games.pacman.ui.fx.graphics.pacman;
    opens de.amr.games.pacman.ui.fx.sound.common;
    opens de.amr.games.pacman.ui.fx.sound.mspacman;
    opens de.amr.games.pacman.ui.fx.sound.pacman;
    opens de.amr.games.pacman.ui.fx.sound.voice;

    // Provided services
    provides javafx.application.Application with de.amr.games.pacman.ui.fx.app.PacManGames2dApp;

}