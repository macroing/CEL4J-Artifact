/*
 * The MIT License (MIT)
 * 
 * Copyright 2018 - 2019 J&#246;rgen Lundgren
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.macroing.cel4j.artifact;

/**
 * This class is the entry-point for the CLI- and GUI-program.
 * <p>
 * By default it will start with the CLI-program, using the Artifact scripting language for evaluating the source code. This means it's like setting the {@code -e} flag to either {@code java} or {@code .java}.
 * <p>
 * The following is a walk-through of all flags currently supported.
 * <ul>
 * <li>
 * {@code -e} - Use this flag to set the filename extension of the scripting language you want to use. If not set, Artifact will be used. That is, the {@code -e} flag is set to {@code java}. This flag requires one parameter argument. So, to use it, here is
 * an example {@code -e java}.
 * </li>
 * <li>
 * {@code -g} - Use this flag to start the GUI-program. By default the CLI-program will be used. This flag takes no parameter arguments. So, to use it, simply write {@code -g}.
 * </li>
 * </ul>
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Main {
	private Main() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) {
		boolean isUsingGUI = false;
		
		String extension = "java";
		
		if(args != null) {
			for(int i = 0; i < args.length; i++) {
				switch(args[i]) {
					case "-e":
						if(i + 1 < args.length) {
							extension = args[++i];
						}
						
						break;
					case "-g":
						isUsingGUI = true;
						
						break;
					default:
						break;
				}
			}
		}
		
		if(isUsingGUI) {
			GUI.start(extension);
		} else {
			CLI.start(extension);
		}
	}
}