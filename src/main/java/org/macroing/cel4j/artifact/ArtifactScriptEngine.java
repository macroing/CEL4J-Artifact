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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

final class ArtifactScriptEngine extends AbstractScriptEngine implements Compilable {
	private static final AtomicInteger IDENTIFIER;
	private static final Pattern PATTERN_IMPORT_PRAGMA;
	private static final Pattern PATTERN_PACKAGE_PRAGMA;
	private static final Pattern PATTERN_SUBSTITUTION_VARIABLE;
	private static final String LINE_SEPARATOR;
	private static final String NAME_IMPORT_PRAGMA;
	private static final String NAME_PACKAGE_PRAGMA;
	private static final String REGEX_IDENTIFIER;
	private static final String REGEX_IMPORT_PRAGMA;
	private static final String REGEX_PACKAGE_PRAGMA;
	private static final String REGEX_SUBSTITUTION_VARIABLE;
	private static final String REGEX_WHITE_SPACE;
	private static final String TMP_DIRECTORY;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final List<String> importStatements = new ArrayList<>();
	private final Map<String, CompiledScript> compiledScripts = new HashMap<>();
	private final ScriptEngineFactory scriptEngineFactory;
	private String packageName = "org.macroing.cel4j.artifact";
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public ArtifactScriptEngine(final ScriptEngineFactory scriptEngineFactory) {
		this.scriptEngineFactory = Objects.requireNonNull(scriptEngineFactory, "scriptEngineFactory == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	static {
		IDENTIFIER = new AtomicInteger(0);
		
		LINE_SEPARATOR = System.getProperty("line.separator");
		TMP_DIRECTORY = System.getProperty("java.io.tmpdir");
		
		NAME_IMPORT_PRAGMA = "ImportPragma";
		NAME_PACKAGE_PRAGMA = "PackagePragma";
		
		REGEX_IDENTIFIER = "(?!(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|false|finally|final|float|for|if|goto|implements|import|instanceof|interface|int|long|native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throws|throw|transient|true|try|void|volatile|while)([^\\p{javaJavaIdentifierPart}]|$))\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
		REGEX_IMPORT_PRAGMA = String.format("#\\s*(?<%s>import(\\s+static)?\\s+%s(\\s*\\.\\s*%s)*?(\\s*\\.\\s*\\*)?\\s*;)", NAME_IMPORT_PRAGMA, REGEX_IDENTIFIER, REGEX_IDENTIFIER);
		REGEX_PACKAGE_PRAGMA = String.format("#\\s*package\\s+(?<%s>%s(\\s*\\.\\s*%s)*?)\\s*;", NAME_PACKAGE_PRAGMA, REGEX_IDENTIFIER, REGEX_IDENTIFIER);
		REGEX_SUBSTITUTION_VARIABLE = String.format("\\$(%s)", REGEX_IDENTIFIER);
		REGEX_WHITE_SPACE = "\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)";
		
		PATTERN_IMPORT_PRAGMA = Pattern.compile(REGEX_IMPORT_PRAGMA);
		PATTERN_PACKAGE_PRAGMA = Pattern.compile(REGEX_PACKAGE_PRAGMA);
		PATTERN_SUBSTITUTION_VARIABLE = Pattern.compile(REGEX_SUBSTITUTION_VARIABLE);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}
	
	@Override
	public CompiledScript compile(final Reader reader) throws ScriptException {
		return doCompile(doReadFrom(Objects.requireNonNull(reader, "reader == null")));
	}
	
	@Override
	public CompiledScript compile(final String script) throws ScriptException {
		return doCompile(Objects.requireNonNull(script, "script == null"));
	}
	
	@Override
	public Object eval(final Reader reader, final ScriptContext scriptContext) throws ScriptException {
		return doEval(Objects.requireNonNull(reader, "reader == null"), Objects.requireNonNull(scriptContext, "scriptContext == null"));
	}
	
	@Override
	public Object eval(final String script, final ScriptContext scriptContext) throws ScriptException {
		return doEval(Objects.requireNonNull(script, "script == null"), Objects.requireNonNull(scriptContext, "scriptContext == null"));
	}
	
	@Override
	public ScriptEngineFactory getFactory() {
		return this.scriptEngineFactory;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private CompiledScript doCompile(final String script) throws ScriptException {
		final String script0 = doSearchAndReplace(script);
		final String className = "CompiledScriptImpl" + IDENTIFIER.incrementAndGet();
		final String packageName = this.packageName;
		final String directory = packageName.replace(".", "/");
		
		final File binaryDirectory = doGetBinaryDirectory();
		final File sourceDirectory = doGetSourceDirectory();
		final File sourceFile = doGetSourceFile(directory, className);
		
		doAddToClassPath(binaryDirectory);
		doGenerateSourceCode(packageName, className, script0, sourceFile);
		
		try {
			final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
			
			final List<File> files = doGetFiles(URLClassLoader.class.cast(ClassLoader.getSystemClassLoader()).getURLs());
			
			try(final StandardJavaFileManager standardJavaFileManager = javaCompiler.getStandardFileManager(null, null, null)) {
				standardJavaFileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(binaryDirectory));
				standardJavaFileManager.setLocation(StandardLocation.CLASS_PATH, files);
				standardJavaFileManager.setLocation(StandardLocation.SOURCE_PATH, Arrays.asList(sourceDirectory));
				
				final CompilationTask compilationTask = javaCompiler.getTask(null, standardJavaFileManager, null, null, null, standardJavaFileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile)));
				
				final boolean isCompiled = compilationTask.call().booleanValue();
				
				if(!isCompiled) {
					throw new ScriptException("Unable to compile script \"" + script + "\".");
				}
			}
		} catch(final IOException | RuntimeException e) {
			throw new ScriptException(e);
		}
		
		try {
			final Class<?> clazz = Class.forName(packageName + "." + className);
			
			final Object object = clazz.getConstructor(new Class<?>[] {ScriptEngine.class}).newInstance(new Object[] {this});
			
			return CompiledScript.class.cast(object);
		} catch(final Exception e) {
			throw new ScriptException(e);
		}
	}
	
	private Object doEval(final Reader reader, final ScriptContext scriptContext) throws ScriptException {
		return doEval(doReadFrom(reader), scriptContext);
	}
	
	private Object doEval(final String script, final ScriptContext scriptContext) throws ScriptException {
		try {
			final String key = script.replaceAll(REGEX_WHITE_SPACE, "");
			
			CompiledScript compiledScript = this.compiledScripts.get(key);
			
			if(compiledScript == null) {
				compiledScript = doCompile(script);
				
				this.compiledScripts.put(key, compiledScript);
			}
			
			if(compiledScript == null) {
				throw new ScriptException("Unable to evaluate script \"" + script + "\".");
			}
			
			return compiledScript.eval(scriptContext);
		} catch(final NullPointerException e) {
			throw new ScriptException(e);
		}
	}
	
	private String doGenerateSourceCode(final String packageName, final String className, final String script) {
		final
		Document document = new Document();
		document.linef("package %s;", packageName);
		document.linef("");
		document.linef("import static java.lang.Math.*;");
		document.linef("");
		document.linef("import java.applet.*;");
		document.linef("import java.awt.*;");
		document.linef("import java.awt.color.*;");
		document.linef("import java.awt.datatransfer.*;");
		document.linef("import java.awt.dnd.*;");
		document.linef("import java.awt.event.*;");
		document.linef("import java.awt.font.*;");
		document.linef("import java.awt.geom.*;");
		document.linef("import java.awt.im.*;");
		document.linef("import java.awt.image.*;");
		document.linef("import java.awt.image.renderable.*;");
		document.linef("import java.awt.print.*;");
		document.linef("import java.lang.annotation.*;");
		document.linef("import java.lang.instrument.*;");
		document.linef("import java.lang.invoke.*;");
		document.linef("import java.lang.management.*;");
		document.linef("import java.lang.ref.*;");
		document.linef("import java.lang.reflect.*;");
		document.linef("import java.math.*;");
		document.linef("import java.net.*;");
		document.linef("import java.nio.*;");
		document.linef("import java.nio.channels.*;");
		document.linef("import java.nio.charset.*;");
		document.linef("import java.nio.file.*;");
		document.linef("import java.nio.file.attribute.*;");
		document.linef("import java.text.*;");
		document.linef("import java.util.*;");
		document.linef("import java.util.concurrent.*;");
		document.linef("import java.util.concurrent.atomic.*;");
		document.linef("import java.util.concurrent.locks.*;");
		document.linef("import java.util.jar.*;");
		document.linef("import java.util.logging.*;");
		document.linef("import java.util.prefs.*;");
		document.linef("import java.util.regex.*;");
		document.linef("import java.util.zip.*;");
		document.linef("");
		document.linef("import javax.script.*;");
		document.linef("import javax.swing.*;");
		document.linef("import javax.swing.border.*;");
		document.linef("import javax.swing.colorchooser.*;");
		document.linef("import javax.swing.event.*;");
		document.linef("import javax.swing.filechooser.*;");
		document.linef("import javax.swing.table.*;");
		document.linef("import javax.swing.text.*;");
		document.linef("import javax.swing.text.html.*;");
		document.linef("import javax.swing.text.html.parser.*;");
		document.linef("import javax.swing.text.rtf.*;");
		document.linef("import javax.swing.tree.*;");
		document.linef("import javax.swing.undo.*;");
		document.linef("import javax.tools.*;");
		document.linef("");
		document.linef("import org.macroing.cel4j.artifact.*;");
		document.linef("");
		
		if(this.importStatements.size() > 0) {
			for(final String importStatement : this.importStatements) {
				document.linef(importStatement);
				document.linef("");
			}
		}
		
		document.linef("public final class %s extends ArtifactScript {", className);
		document.linef("	public %s(final ScriptEngine scriptEngine) {", className);
		document.linef("		super(scriptEngine);");
		document.linef("	}");
		document.linef("	");
		document.linef("	@Override");
		document.linef("	public Object eval(final ScriptContext scriptContext) throws ScriptException {");
		document.linef("		Exception exception = null;");
		document.linef("		");
		document.linef("		try {");
		document.linef("			%s", script);
		document.linef("		} catch(final Exception e) {");
		document.linef("			exception = e;");
		document.linef("		}");
		document.linef("		");
		document.linef("		if(exception != null) {");
		document.linef("			throw new ScriptException(exception);");
		document.linef("		} else {");
		document.linef("			return null;");
		document.linef("		}");
		document.linef("	}");
		document.linef("	");
		document.linef("}");
		
		return document.toString();
	}
	
	private String doSearchAndReplace(String script) throws ScriptException {
		script = doSearchAndReplaceImportPragmas(script);
		script = doSearchAndReplacePackagePragmas(script);
		script = doSearchAndReplaceSubstitutionVariables(script);
		
		return script;
	}
	
	private String doSearchAndReplaceImportPragmas(final String script) {
		final StringBuffer stringBuffer = new StringBuffer(script.length());
		
		final Matcher matcher = PATTERN_IMPORT_PRAGMA.matcher(script);
		
		while(matcher.find()) {
			final String importStatement = matcher.group(NAME_IMPORT_PRAGMA);
			final String replacement = "";
			
			this.importStatements.add(importStatement);
			
			matcher.appendReplacement(stringBuffer, replacement);
		}
		
		matcher.appendTail(stringBuffer);
		
		return stringBuffer.toString();
	}
	
	private String doSearchAndReplacePackagePragmas(final String script) {
		final StringBuffer stringBuffer = new StringBuffer(script.length());
		
		final Matcher matcher = PATTERN_PACKAGE_PRAGMA.matcher(script);
		
		while(matcher.find()) {
			final String packageName = matcher.group(NAME_PACKAGE_PRAGMA);
			final String replacement = "";
			
			this.packageName = packageName;
			
			matcher.appendReplacement(stringBuffer, replacement);
		}
		
		matcher.appendTail(stringBuffer);
		
		return stringBuffer.toString();
	}
	
	private String doSearchAndReplaceSubstitutionVariables(final String script) throws ScriptException {
		final StringBuffer stringBuffer = new StringBuffer(script.length());
		
		final Matcher matcher = PATTERN_SUBSTITUTION_VARIABLE.matcher(script);
		
		while(matcher.find()) {
			final String variableName = matcher.group(1);
			final String dynamicCastEvaluation = String.format("return scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).get(\"%s\");", variableName);
			
			String replacement = "";
			
			final Object object = eval(dynamicCastEvaluation);
			
			if(object != null) {
				final Class<?> clazz = object.getClass();
				
				replacement = doCast(clazz, variableName);
			}
			
			matcher.appendReplacement(stringBuffer, replacement);
		}
		
		matcher.appendTail(stringBuffer);
		
		return stringBuffer.toString();
	}
	
	private void doGenerateSourceCode(final String packageName, final String className, final String script, final File sourceFile) throws ScriptException {
		final String sourceCode = doGenerateSourceCode(packageName, className, script);
		
		try(final FileWriter fileWriter = new FileWriter(sourceFile)) {
			fileWriter.write(sourceCode);
		} catch(final IOException e) {
			throw new ScriptException(e);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static File doGetBinaryDirectory() {
		final
		File file = new File(TMP_DIRECTORY, "artifact/bin");
		file.mkdirs();
		
		return file;
	}
	
	private static File doGetSourceDirectory() {
		final
		File file = new File(TMP_DIRECTORY, "artifact/src");
		file.mkdirs();
		
		return file;
	}
	
	private static File doGetSourceFile(final String directory, final String className) {
		final
		File file = new File(doGetSourceDirectory(), directory + "/" + className + ".java");
		file.getParentFile().mkdirs();
		
		return file;
	}
	
	private static int doGetDimensionsOf(final Class<?> clazz, final int dimensions) {
		if(clazz.isArray()) {
			return doGetDimensionsOf(clazz.getComponentType(), dimensions + 1);
		}
		
		return dimensions;
	}
	
	private static List<File> doGetFiles(final URL[] uRLs) {
		final List<File> files = new ArrayList<>();
		
		for(final URL uRL : uRLs) {
			try {
				files.add(new File(uRL.toURI()));
			} catch(final URISyntaxException e) {
				files.add(new File(uRL.getPath()));
			}
		}
		
		return files;
	}
	
	private static String doCast(final Class<?> clazz, final String variableName) {
		String className = clazz.getName();
		
		if(clazz.isArray()) {
			final int dimensions = doGetDimensionsOf(clazz, 0);
			
			className = className.replace(";", "");
			className = className.replaceAll("\\[{" + dimensions + "," + dimensions + "}L", "");
			className = className + String.join("", Collections.nCopies(dimensions, "[]"));
		}
		
		return String.format("%s.class.cast(scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).get(\"%s\"))", className, variableName);
	}
	
	private static String doReadFrom(final Reader reader) throws ScriptException {
		try(final BufferedReader bufferedReader = new BufferedReader(reader)) {
			final StringBuilder stringBuilder = new StringBuilder();
			
			for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
				stringBuilder.append(line);
				stringBuilder.append(LINE_SEPARATOR);
			}
			
			return stringBuilder.toString();
		} catch(final IOException e) {
			throw new ScriptException(e);
		}
	}
	
	private static void doAddToClassPath(final File file) {
		try {
			final Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class<?>[]{URL.class});
			
			final boolean isAccessible = method.isAccessible();
			
			method.setAccessible(true);
			method.invoke(URLClassLoader.class.cast(ClassLoader.getSystemClassLoader()), new Object[]{file.toURI().toURL()});
			method.setAccessible(isAccessible);
		} catch(final IllegalAccessException | InvocationTargetException | MalformedURLException | NoSuchMethodException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}