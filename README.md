# webfx-pacman

## Issues/workarounds (GWT only?)
- TinyLog library, how to include?
- String.format()
- String.isBlank()
- List.of() -> dev.webfx.platform.util.collection.Collections.listOf()
- Map.of() -> ??? (new HashMap<>())
- File access -> Local store API
- Console logging
- ResourceBundle support (removed all usages, no multilingual support is needed in this app)
- Class::isInstance, Class::cast in stream filter
- Class.getResource()
- Resource directories under src/main/resources/... have to be declared (exported) in web.xml
- Bindings: DoubleProperty.divide(), DoubleProperty.multiply() not supported?
- TextFlow not working
- Border around Pane not working (?)
- Canvas scaling behaves differently in GWT than in OpenJFX (not sure if bug)
- Avoid timezone error messages in GWT

## Additional/new functionality needed
- Touch event support
- Load/save highscore (local storage API)
- Initial game variant selection based on URL (HTTP domain) 