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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

/**
 * {@code ArtifactScriptEngineFactory} is a {@code ScriptEngineFactory} that manages a {@code ScriptEngine} called Artifact that evaluates a super-set of Java source code.
 * <p>
 * The {@code ScriptEngine} provided compiles the source code into {@code CompiledScript}s and loads them, using the context {@code ClassLoader}. It caches the {@code CompiledScript}s using a normalized version of the source code provided for that
 * {@code CompiledScript}. By doing so, no re-compilation will be performed when you add whitespace in other places than {@code String} literals.
 * <p>
 * To demonstrate its use, here is an example:
 * <pre>
 * {@code
 * ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
 * 
 * ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension("java");
 * scriptEngine.eval("System.out.println(\"Hello, World!\"); return true;");
 * }
 * </pre>
 * As you can see in the example above, you can return a result. Although you can, it's not necessary. If nothing is returned by you, {@code null} will be returned by default.
 * <p>
 * If a variable starts with a dollar sign ({@code $}), followed by a variable name (a Java identifier), that variable will be substituted for a variable in the {@code ScriptContext} and cast to its type. Lets say you have a variable {@code $string}
 * that refers to a variable called {@code "string"} in the {@code ScriptContext}, and that variable is of type {@code String}. A call such as {@code $string.length()} would return the length of the {@code String} variable. Note, however, that this
 * assumes the variable already exists in the {@code ScriptContext} prior to the evaluation of the current script. Adding a variable to the {@code ScriptContext} and then using this variable substitution mechanism to get that variable in the same
 * script won't work. The reason for this, is that the variable substitution is performed prior to the evaluation of the script itself.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class ArtifactScriptEngineFactory implements ScriptEngineFactory {
	/**
	 * A {@code String} denoting the name of the {@code ScriptEngine} Artifact.
	 */
	public static final String ENGINE_NAME = "Artifact";
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final ScriptEngine scriptEngine = new ArtifactScriptEngine(this);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code ArtifactScriptEngineFactory} instance.
	 */
	public ArtifactScriptEngineFactory() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code List} with extensions.
	 * <p>
	 * The currently supported extensions are {@code ".java"} and {@code "java"}.
	 * 
	 * @return a {@code List} with extensions
	 */
	@Override
	public List<String> getExtensions() {
		return Collections.unmodifiableList(new ArrayList<>(Arrays.asList(".java", "java")));
	}
	
	/**
	 * Returns a {@code List} with mime-types.
	 * <p>
	 * There are currently no supported mime-types.
	 * 
	 * @return a {@code List} with mime-types
	 */
	@Override
	public List<String> getMimeTypes() {
		return Collections.unmodifiableList(new ArrayList<>());
	}
	
	/**
	 * Returns a {@code List} with names.
	 * <p>
	 * The currently supported names are {@code "artifact"} and {@code "java"}.
	 * 
	 * @return a {@code List} with names
	 */
	@Override
	public List<String> getNames() {
		return Collections.unmodifiableList(new ArrayList<>(Arrays.asList("artifact", "java")));
	}
	
	/**
	 * Returns an {@code Object} value based on a {@code String} key.
	 * <p>
	 * The key may be {@code null}.
	 * 
	 * @param key the key to return a value from
	 * @return an {@code Object} value based on a {@code String} key
	 */
	@Override
	public Object getParameter(final String key) {
		if(key != null) {
			switch(key) {
				case ScriptEngine.ENGINE:
					return getEngineName();
				case ScriptEngine.ENGINE_VERSION:
					return getEngineVersion();
				case ScriptEngine.LANGUAGE:
					return getLanguageName();
				case ScriptEngine.LANGUAGE_VERSION:
					return getLanguageVersion();
				default:
					return null;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the {@code ScriptEngine}.
	 * <p>
	 * It may be cached.
	 * 
	 * @return the {@code ScriptEngine}
	 */
	@Override
	public ScriptEngine getScriptEngine() {
		return this.scriptEngine;
	}
	
	/**
	 * Returns a {@code String} with the name of the {@code ScriptEngine}.
	 * <p>
	 * The name is {@code "Artifact"}.
	 * 
	 * @return a {@code String} with the name of the {@code ScriptEngine}
	 */
	@Override
	public String getEngineName() {
		return ENGINE_NAME;
	}
	
	/**
	 * Returns a {@code String} with the version of the {@code ScriptEngine}.
	 * <p>
	 * The current version is {@code "1.0.0"}.
	 * 
	 * @return a {@code String} with the version of the {@code ScriptEngine}
	 */
	@Override
	public String getEngineVersion() {
		return "1.0.0";
	}
	
	/**
	 * Returns a {@code String} with the name of the language.
	 * <p>
	 * The name is {@code "Java"}.
	 * 
	 * @return a {@code String} with the name of the language
	 */
	@Override
	public String getLanguageName() {
		return "Java";
	}
	
	/**
	 * Returns a {@code String} with the version of the language.
	 * <p>
	 * The current version is {@code "8"}.
	 * 
	 * @return a {@code String} with the version of the language
	 */
	@Override
	public String getLanguageVersion() {
		return "8";
	}
	
	/**
	 * Returns an empty {@code String} at this time.
	 * 
	 * @param object a {@code String} with an object
	 * @param method a {@code String} with a method
	 * @param args a {@code String} array with parameter arguments
	 * @return an empty {@code String} at this time
	 */
	@Override
	public String getMethodCallSyntax(final String object, final String method, final String... args) {
		return "";
	}
	
	/**
	 * Returns an empty {@code String} at this time.
	 * 
	 * @param toDisplay a {@code String} to display
	 * @return an empty {@code String} at this time
	 */
	@Override
	public String getOutputStatement(final String toDisplay) {
		return "";
	}
	
	/**
	 * Returns an empty {@code String} at this time.
	 * 
	 * @param statements a {@code String} array with statements
	 * @return an empty {@code String} at this time
	 */
	@Override
	public String getProgram(final String... statements) {
		return "";
	}
}