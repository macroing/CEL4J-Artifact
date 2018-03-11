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

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * {@code ArtifactScript} is an implementation of {@code CompiledScript} and is extended by each script class that is evaluated by this Artifact {@code ScriptEngine} implementation.
 * <p>
 * The purpose with this class, is to supply all script writers with a simple API they can use when writing their scripts.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class ArtifactScript extends CompiledScript {
	private final ScriptEngine scriptEngine;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code ArtifactScript} instance.
	 * <p>
	 * If {@code scriptEngine} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param scriptEngine a {@code ScriptEngine}
	 * @throws NullPointerException thrown if, and only if, {@code scriptEngine} is {@code null}
	 */
	protected ArtifactScript(final ScriptEngine scriptEngine) {
		this.scriptEngine = Objects.requireNonNull(scriptEngine, "scriptEngine == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Executes {@code script} as script.
	 * <p>
	 * Returns the return value of the script.
	 * <p>
	 * If {@code script} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an error occurs in the script, a {@code ScriptException} will be thrown.
	 * 
	 * @param script the script to execute
	 * @return the return value of the script
	 * @throws NullPointerException thrown if, and only if, {@code script} is {@code null}
	 * @throws ScriptException thrown if, and only if, an error occurs in the script
	 */
	public final Object eval(final String script) throws ScriptException {
		try {
			return this.scriptEngine.eval(script);
		} catch(final Exception e) {
			throw new ScriptException(e);
		}
	}
	
	/**
	 * Returns the value assigned to {@code key}, or {@code null} if no value is assigned to {@code key}.
	 * <p>
	 * If {@code key} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code key} is an empty {@code String}, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param key a {@code String}
	 * @return the value assigned to {@code key}, or {@code null} if no value is assigned to {@code key}
	 * @throws IllegalArgumentException thrown if, and only if, {@code key} is an empty {@code String}
	 * @throws NullPointerException thrown if, and only if, {@code key} is {@code null}
	 */
	public final Object get(final String key) {
		return this.scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).get(key);
	}
	
	/**
	 * Returns the {@code ScriptEngine} whose compile method created this {@code ArtifactScript}.
	 * 
	 * @return the {@code ScriptEngine} whose compile method created this {@code ArtifactScript}
	 */
	@Override
	public final ScriptEngine getEngine() {
		return this.scriptEngine;
	}
	
	/**
	 * Sets the value {@code value} for the key {@code key}.
	 * <p>
	 * If {@code key} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code key} is an empty {@code String}, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param key the key
	 * @param value the value
	 * @throws IllegalArgumentException thrown if, and only if, {@code key} is an empty {@code String}
	 * @throws NullPointerException thrown if, and only if, {@code key} is {@code null}
	 */
	public final void set(final String key, final Object value) {
		this.scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).put(key, value);
	}
}