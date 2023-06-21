# WebFX UI for Pac-Man and Ms. Pac-Man Arcade games

## About this project

Repository [Pac-Man games](https://github.com/armin-reichert/pacman-basic) contains a UI-agnostic
Java implementation of the classic Arcade games Pac-Man and Ms. Pac-Man. A JavaFX user interface for these games, with
2D and 3D play scenes, can be found [here](https://github.com/armin-reichert/pacman-javafx).

This repository contains a WebFX compatible, stripped-down version of the JavaFX user intefaces without 3D scenes. 
It can be run on all platforms supported by WebFX, especially inside a browser via the GWT implementation.

## How to use

The game can be started by opening the following URL(s) in a browser:
- [Ms. Pac-Man game](https://mspacman.webfx.dev)
- [Pac-Man game](https://pacman.webfx.dev)

### Scene selection

From the start page and intro scence of either game, you can switch to the other game by pressing <kbd>V</kbd>. Pressing <kbd>Enter</kbd> or <kbd>Space</kbd> or clicking the "Play!"
button displays the intro scene of the selected game variant.

To be able to play, you first have to "insert a coin" which is done either by pressing the key <kbd>5</kbd> or by clicking anywhere on the intro scene. After having added credit, the game can be started by pressing key <kbd>1</kbd> or by clicking anywhere with the mouse (or touching). (The used keys used are derived from the MAME Arcade emulator). To quit the play scene and return to the intro scene, press key <kbd>Q</kbd>.

Pac-Man steering:
- Pac-Man is steered using the cursor keys.

Simulation control:
- <kbd>P</kbd>: Toggle pause mode
- <kbd>Shift+P</kbd> or <kbd>Space</kbd>: Single step
- <kbd>T</kbd>: Ten steps
- <kbd>Alt+F</kbd>: Run faster (in 5 Hz steps)
- <kbd>Alt+S</kbd>: Run slower (in 5 Hz steps)
- <kbd>Alt+0</kbd>: Run at 60 Hz

Test modes: (available from intro screens)
- <kbd>Alt+C</kbd>: Play the cutscenes of the game

Cheats:
- <kbd>Alt+Shift+A</kbd>: Toggle autopilot mode
- <kbd>Alt+Shift+I</kbd>: Toggle immunity mode

Play scene cheats:
- <kbd>Alt+E</kbd>: Eat all pills except the energizers
- <kbd>Alt+L</kbd>: Add 3 player lives
- <kbd>Alt+N</kbd>: Enter next game level
- <kbd>Alt+X</kbd>: Kill all ghosts outside the ghost house

### With mouse or touch screen

- Intro scene:  Click/tap anywhere to "add a coin" and change to the "credit scene".
- Credit scene: Click/tap anywhere to start the game and change to the play scene.
- Play scene:   To steer Pac-Man, use a mouse drag or a "wipe" gesture. Dragging / wiping anywhere inside the maze to any direction causes Pac-Man to move to that direction (as soon as he can).

