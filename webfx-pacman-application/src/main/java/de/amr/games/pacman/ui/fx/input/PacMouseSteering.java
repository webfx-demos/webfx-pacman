/*
MIT License

Copyright (c) 2022 Armin Reichert

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package de.amr.games.pacman.ui.fx.input;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.model.actors.Pac;
import de.amr.games.pacman.ui.fx.app.MinGamePage;
import javafx.scene.Node;
import org.tinylog.Logger;

import java.util.function.Supplier;

/**
 * @author Armin Reichert, Bruno Salmon
 */
public class PacMouseSteering {

    private Supplier<Pac> pacSupplier;
    private boolean alternateIntermediateDirection;

    public PacMouseSteering(MinGamePage page, Node node, Supplier<Pac> pacSupplier) {
        this.pacSupplier = pacSupplier;
        node.setOnMouseDragged(event -> {
            Logger.info("Dragged: {}", event);
            Pac pac = pacSupplier.get();
            if (pac != null) {
                double x = event.getX() / page.getScaling();
                double y = event.getY() / page.getScaling();
                var dir = computeDirection(pac, x, y);
                if (dir != null) {
                    pac.setWishDir(dir);
                    Logger.info("Pac should move {}", dir);
                }
            }
        });
    }

    private Direction computeDirection(Pac pac, double gestureX, double gestureY) {
        var pos = pac.center();
        if (Math.abs(gestureX - pos.x()) < 8 && Math.abs(gestureY - pos.y()) < 8) {
            return null; // inside Pac sprite
        }
        // Angle between Pac-Man and the mouse (between -180° and +180°)
        double angle = Math.atan2(pos.y() - gestureY, gestureX - pos.x()) / Math.PI * 180;
        // We will consider all cardinal directions (RIGHT, UP, LEFT & DOWN) and all intermediate directions (RIGHT_UP,
        // LEFT_UP, LEFT_DOWN & RIGHT_DOWN) => 8 directions => 360 / 8 = 45° each. RIGHT direction starts at
        // angleStart = -45/2 = -22.5° up to angleStart + 45°, and so on (+45° each time). To make things easier, we
        // shift the angle by adding 22.5° (so RIGHT will finally be between 0 & 45°, RIGHT_UP between 45° & 90°, etc...)
        double shiftedAngle = (angle + 45d / 2 + 360d) % 360d; // Also we ensure shiftedAngle is between 0° and 360°
        // Because intermediate directions don't exist in Direction enum, we simulate them by alternating between the 2
        // closest cardinal directions. Ex: to simulate RIGHT_UP, we return first RIGHT, next time UP, then RIGHT, UP...
        alternateIntermediateDirection = !alternateIntermediateDirection;
        if (shiftedAngle <= 45)   return Direction.RIGHT;
        if (shiftedAngle <= 90)   return alternateIntermediateDirection ? Direction.RIGHT : Direction.UP; // RIGHT_UP intermediate direction
        if (shiftedAngle <= 135 ) return Direction.UP;
        if (shiftedAngle <= 180)  return alternateIntermediateDirection ? Direction.LEFT : Direction.UP; // LEFT_UP intermediate direction
        if (shiftedAngle <= 225)  return Direction.LEFT;
        if (shiftedAngle <= 270)  return alternateIntermediateDirection ? Direction.LEFT : Direction.DOWN; // LEFT_DOWN intermediate direction
        if (shiftedAngle <= 315)  return Direction.DOWN;
        return alternateIntermediateDirection ? Direction.RIGHT : Direction.DOWN; // RIGHT_DOWN intermediate direction
    }
}