# webfx-pacman

## Issues/workarounds (GWT only?)
- No TinyLog support.
- No `String.format()`
- No `String.isBlank()`
- [x] No `List.of()`; Use `dev.webfx.platform.util.collection.Collections.listOf()`
- [x] No `Map.of()` -> Use `new HashMap<>()`
- No file access -> Use local storage API
- [x] Use `dev.webfx.platform.console.Console` for logging into browser console
- No/restricted resourceBundle support (removed all usages, no multilingual support is needed in this app)
- No `Class::isInstance`, `Class::cast` support (was used in stream filter)
- No `Class.getResource()`: Use `dev.webfx.platform.resource.Resource.toUrl()`
- [x] Resource directories under `src/main/resources/...` have to be declared (exported) in web.xml
- No `DoubleProperty.divide()`, `DoubleProperty.multiply()` and many other binding functions
- `TextFlow` not working: Use layouts
- Border around Pane only partially working (?)
- Canvas scaling behaves differently in GWT than in OpenJFX (upscaling in GWT looks really bad)
- [x] Avoid timezone error messages in GWT (why not generally fixed?)
- What is the line `DeviceSceneUtil.onFontsAndImagesLoaded(() -> {} , GameAssets.Manager.getLoadedImages());` good for?

## Additional/new functionality needed
- [x] Touch/mouse control of Pac-Man
- [ ] Load/save highscore (local storage API)
- [x] Initial game variant selection based on URL (HTTP domain)
- [x] Discrete "scaling" (resizing) of canvas