# webfx-pacman

## Issues/workarounds
- [x] TinyLog support. Workaround: Simple mock class.
- [ ] `String.format()`.
- [ ] `String.isBlank()`.
- [x] `List.of()`: Use `dev.webfx.platform.util.collection.Collections.listOf()`.
- [x] `Map.of()` -> Use `new HashMap<>()`.
- File access -> Use local storage API.
- [x] Use `dev.webfx.platform.console.Console` for logging into browser console.
- [x] ResourceBundle support (removed all usages, no multilingual support is needed in this app)
- [x] `Class::isInstance`, `Class::cast` support (was used in stream filter)
- [x] `Class.getResource()`: Use `dev.webfx.platform.resource.Resource.toUrl()`
- [x] Resource directories under `src/main/resources/...` have to be declared (exported) in web.xml
- [x] `DoubleProperty.divide()`, `DoubleProperty.multiply()` and many other binding functions
- `TextFlow` not working: Use layouts.
- Border around Pane only partially working (?)
- Canvas scaling behaves differently in GWT than in OpenJFX (upscaling in GWT looks really bad)
- [x] Avoid timezone error messages in GWT (why not generally fixed?)
- What is the line `DeviceSceneUtil.onFontsAndImagesLoaded(() -> {} , GameAssets.Manager.getLoadedImages());` good for?

## Additional functionality (as in existing WebFX version)
- [x] Touch/mouse control of Pac-Man
- [x] Load/save highscore (local storage API)
- [x] Initial game variant selection based on URL (HTTP domain)
- [x] Discrete "scaling" (resizing) of canvas
- [x] Help menus
- [x] Signature/copyright