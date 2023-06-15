// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.pacman.application.openjfx {

    // Direct dependencies modules
    requires javafx.media;
    requires webfx.kit.openjfx;
    requires webfx.pacman.application;
    requires webfx.platform.boot.java;
    requires webfx.platform.console.java;
    requires webfx.platform.json.java;
    requires webfx.platform.resource.java;
    requires webfx.platform.scheduler.java;
    requires webfx.platform.shutdown.java;
    requires webfx.platform.useragent.java.client;
    requires webfx.platform.windowhistory.java;
    requires webfx.platform.windowlocation.java;

    // Meta Resource package
    opens dev.webfx.platform.meta.exe;

}