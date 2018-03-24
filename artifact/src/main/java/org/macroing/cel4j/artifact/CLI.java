/*
 * The MIT License (MIT)
 * 
 * Copyright 2018 J&#246;rgen Lundgren
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

import java.util.Objects;
import java.util.Scanner;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

final class CLI {
	private static final ScriptEngineManager DEFAULT_SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private CLI() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void start(final String extension) {
		try(final Scanner scanner = new Scanner(System.in)) {
			final ScriptEngineManager scriptEngineManager = DEFAULT_SCRIPT_ENGINE_MANAGER;
			
			final ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension(Objects.requireNonNull(extension, "extension == null"));
			
			while(true) {
				System.out.print("Artifact: ");
				
				final String script = scanner.nextLine();
				
				try {
					final Object object = scriptEngine.eval(script);
					
					System.out.println("Artifact: " + object + System.getProperty("line.separator"));
				} catch(final ScriptException e) {
					System.out.println("Artifact: " + e.toString() + System.getProperty("line.separator"));
				}
			}
		}
	}
}