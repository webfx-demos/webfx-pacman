/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.model;

/**
 * @author Armin Reichert
 */
public class IllegalLevelNumberException extends IllegalArgumentException {

	public IllegalLevelNumberException(int number) {
		super("Illegal level number (Allowed values: 1-): " + number);
	}
}