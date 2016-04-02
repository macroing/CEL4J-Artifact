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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

final class JTextAreaOutputStreamDecorator extends OutputStream {
	private final JTextArea jTextArea;
	private final OutputStream outputStream;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public JTextAreaOutputStreamDecorator(final JTextArea jTextArea, final OutputStream outputStream) {
		this.jTextArea = Objects.requireNonNull(jTextArea, "jTextArea == null");
		this.outputStream = Objects.requireNonNull(outputStream, "outputStream == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void write(final int b) throws IOException {
		this.outputStream.write(b);
		
		final byte[] bytes = new byte[] {(byte)(b)};
		
		final String string = new String(bytes, "ISO-8859-1");
		
		final Runnable runnable = () -> this.jTextArea.append(string);
		
		SwingUtilities.invokeLater(runnable);
	}
}