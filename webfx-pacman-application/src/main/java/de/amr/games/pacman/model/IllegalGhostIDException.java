/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.model;

/**
 * @author Armin Reichert
 */
public class IllegalGhostIDException extends IllegalArgumentException {

	public IllegalGhostIDException(int id) {
		super("Illegal ghost ID value (Allowed values: 0-3): " + id);
	}
}