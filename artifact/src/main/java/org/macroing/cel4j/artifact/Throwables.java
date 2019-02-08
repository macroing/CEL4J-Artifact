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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Objects;

final class Throwables {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Throwables() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static String getStackTrace(final Throwable throwable) {
		return getStackTrace(throwable, Thread.currentThread());
	}
	
	public static String getStackTrace(final Throwable throwable, final Thread thread) {
		final StringBuilder stringBuilder = new StringBuilder(thread + LINE_SEPARATOR);
		
		Throwable currentThrowable = throwable;
		
		do {
			final Class<?> clazz = currentThrowable.getClass();
			
			final String className = clazz.getName();
			final String message = currentThrowable.getMessage() != null ? ": " + currentThrowable.getMessage() : "";
			
			stringBuilder.append(className);
			stringBuilder.append(" ");
			stringBuilder.append(message);
			stringBuilder.append(LINE_SEPARATOR);
			
			for(final StackTraceElement stackTraceElement : currentThrowable.getStackTrace()) {
				final boolean hasFileName = stackTraceElement.getFileName() != null;
				final boolean hasLineNumber = stackTraceElement.getLineNumber() >= 0;
				
				stringBuilder.append("\t");
				stringBuilder.append("at");
				stringBuilder.append(stackTraceElement.getClassName());
				stringBuilder.append(".");
				stringBuilder.append(stackTraceElement.getMethodName());
				stringBuilder.append("(");
				stringBuilder.append(hasFileName ? stackTraceElement.getFileName() : "");
				stringBuilder.append(hasFileName && hasLineNumber ? ":" : "");
				stringBuilder.append(hasLineNumber ? Integer.toString(stackTraceElement.getLineNumber()) : "");
				stringBuilder.append(!hasFileName && !hasLineNumber ? "Unknown Source" : "");
				stringBuilder.append(")");
				stringBuilder.append(LINE_SEPARATOR);
			}
		} while((currentThrowable = currentThrowable.getCause()) != null);
		
		return stringBuilder.toString();
	}
	
	public static void handleThrowable(final Throwable throwable) {
		Objects.requireNonNull(throwable, "throwable == null");
		
		final UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		
		if(uncaughtExceptionHandler != null) {
			uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), throwable);
		} else {
			throwable.printStackTrace();
		}
		
		/*
		 * LinkageError:
		 * This may happen. But only if we got things like JNI and other linking problems. We should fix them, so why continue?
		 * 
		 * ThreadDeath:
		 * This shouldn't happen. ThreadDeath is only thrown by Thread.stop(), which is deprecated, and thus never used by us. We don't use deprecated things. But someone else might, in which case it's important to handle it anyway.
		 * 
		 * VirtualMachineError:
		 * This may happen. If we are out of memory (OutOfMemoryError is just one kind of VirtualMachineError), we probably have a problem in our code. Then why continue?
		 */
		if(throwable instanceof LinkageError) {
			System.exit(1);
		} else if(throwable instanceof ThreadDeath) {
			throw ThreadDeath.class.cast(throwable);
		} else if(throwable instanceof VirtualMachineError) {
			System.exit(1);
		}
	}
}