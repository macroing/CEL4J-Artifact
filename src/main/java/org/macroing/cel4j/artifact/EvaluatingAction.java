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

import java.awt.event.ActionEvent;
import java.util.Objects;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import org.macroing.cit.java.util.Throwables;

final class EvaluatingAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final JTextPane jTextPane;
	private final ScriptEngine scriptEngine;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public EvaluatingAction(final JTextPane jTextPane, final String extension) {
		this.jTextPane = Objects.requireNonNull(jTextPane, "jTextPane == null");
		this.scriptEngine = ArtifactUtilities.getDefaultScriptEngineManager().getEngineByExtension(extension);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void actionPerformed(final ActionEvent actionEvent) {
		try {
			this.scriptEngine.eval(this.jTextPane.getDocument().getText(0, this.jTextPane.getDocument().getLength()));
		} catch(final BadLocationException | ScriptException e) {
			Throwables.handleThrowable(e);
		}
	}
}