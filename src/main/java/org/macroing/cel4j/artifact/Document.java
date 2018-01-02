/**
 * Copyright 2009 - 2018 J&#246;rgen Lundgren
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

import java.util.Objects;

final class Document {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final StringBuilder stringBuilder = new StringBuilder();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Document() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Document linef(final String string, final Object... objects) {
		this.stringBuilder.append(String.format(Objects.requireNonNull(string, "textAfterIndentationFormat == null"), Objects.requireNonNull(objects, "objects == null")) + LINE_SEPARATOR);
		
		return this;
	}
	
	@Override
	public String toString() {
		return this.stringBuilder.toString();
	}
}