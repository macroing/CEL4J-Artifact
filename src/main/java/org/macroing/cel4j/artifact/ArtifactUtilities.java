/**
 * Copyright 2009 - 2016 J&#246;rgen Lundgren
 * 
 * This file is part of org.macroing.cel4j.artifact.
 * 
 * org.macroing.cel4j.artifact is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.macroing.cel4j.artifact is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.macroing.cel4j.artifact. If not, see <http://www.gnu.org/licenses/>.
 */
package org.macroing.cel4j.artifact;

import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;
import java.util.Objects;

import javax.script.ScriptEngineManager;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.macroing.cit.java.util.Throwables;

final class ArtifactUtilities {
	private static final ScriptEngineManager DEFAULT_SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private ArtifactUtilities() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static ActionListener newExitActionListener(final JFrame jFrame) {
		return e -> {
			switch(JOptionPane.showConfirmDialog(jFrame, "Are you sure you want to quit?")) {
				case JOptionPane.OK_OPTION:
					System.exit(0);
					
					break;
				default:
					break;
			}
		};
	}
	
	public static Icon createIcon(final String path, final String description) {
		final URL uRL = ArtifactUtilities.class.getResource(Objects.requireNonNull(path, "path == null"));
		
		if(uRL != null) {
			return new ImageIcon(uRL, Objects.requireNonNull(description, "description == null"));
		}
		
		return null;
	}
	
	public static ScriptEngineManager getDefaultScriptEngineManager() {
		return DEFAULT_SCRIPT_ENGINE_MANAGER;
	}
	
	public static UncaughtExceptionHandler newUncaughtExceptionHandler(final JTextArea jTextArea) {
		return (thread, throwable) -> jTextArea.append(Throwables.getStackTrace(throwable, thread) + "\n");
	}
}