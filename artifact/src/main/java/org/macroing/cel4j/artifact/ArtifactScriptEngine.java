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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
	private static final Pattern PATTERN_IMPORT;
	private static final Pattern PATTERN_PACKAGE;
	private static final Pattern PATTERN_SUBSTITUTION_VARIABLE;
	private static final String DEFAULT_PACKAGE_NAME;
	private static final String LINE_SEPARATOR;
	private static final String NAME_IMPORT;
	private static final String NAME_PACKAGE;
	private static final String PROPERTY_DUMP;
	private static final String PROPERTY_IMPORT;
	private static final String REGEX_IDENTIFIER;
	private static final String REGEX_IMPORT;
	private static final String REGEX_PACKAGE;
	private static final String REGEX_SUBSTITUTION_VARIABLE;
	private static final String REGEX_WHITE_SPACE;
	private static final String TMP_DIRECTORY;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final AtomicReference<String> packageName;
	private final List<String> importStatements;
	private final List<String> importStatementsOptional;
	private final List<String> importStatementsRequired;
	private final Map<String, CompiledScript> compiledScripts;
	private final ScriptEngineFactory scriptEngineFactory;
	private final boolean isDumpingSourceCode;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public ArtifactScriptEngine(final ScriptEngineFactory scriptEngineFactory) {
		this.scriptEngineFactory = Objects.requireNonNull(scriptEngineFactory, "scriptEngineFactory == null");
		this.packageName = new AtomicReference<>(DEFAULT_PACKAGE_NAME);
		this.importStatements = new ArrayList<>();
		this.importStatementsOptional = doCreateImportStatementsOptional();
		this.importStatementsRequired = doCreateImportStatementsRequired();
		this.compiledScripts = new HashMap<>();
		this.isDumpingSourceCode = Objects.toString(System.getProperty(PROPERTY_DUMP)).equals("true");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	static {
		IDENTIFIER = new AtomicInteger(0);
		
		DEFAULT_PACKAGE_NAME = "org.macroing.cel4j.artifact";
		
		PROPERTY_DUMP = "org.macroing.cel4j.artifact.dump";
		PROPERTY_IMPORT = "org.macroing.cel4j.artifact.import";
		
		LINE_SEPARATOR = System.getProperty("line.separator");
		TMP_DIRECTORY = System.getProperty("java.io.tmpdir");
		
		NAME_IMPORT = "Import";
		NAME_PACKAGE = "Package";
		
		REGEX_IDENTIFIER = "(?!(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|false|finally|final|float|for|if|goto|implements|import|instanceof|interface|int|long|native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throws|throw|transient|true|try|void|volatile|while)([^\\p{javaJavaIdentifierPart}]|$))\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
		REGEX_IMPORT = String.format("(?<%s>import(\\s+static)?\\s+%s(\\s*\\.\\s*%s)*?(\\s*\\.\\s*\\*)?\\s*;)", NAME_IMPORT, REGEX_IDENTIFIER, REGEX_IDENTIFIER);
		REGEX_PACKAGE = String.format("package\\s+(?<%s>%s(\\s*\\.\\s*%s)*?)\\s*;", NAME_PACKAGE, REGEX_IDENTIFIER, REGEX_IDENTIFIER);
		REGEX_SUBSTITUTION_VARIABLE = String.format("\\$(%s)", REGEX_IDENTIFIER);
		REGEX_WHITE_SPACE = "\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)";
		
		PATTERN_IMPORT = Pattern.compile(REGEX_IMPORT);
		PATTERN_PACKAGE = Pattern.compile(REGEX_PACKAGE);
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
		final String className = "ArtifactScriptImpl" + IDENTIFIER.incrementAndGet();
		final String packageName = this.packageName.get();
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
		
		for(final String importStatement : this.importStatementsRequired) {
			document.linef(importStatement);
		}
		
		for(final String importStatement : this.importStatementsOptional) {
			document.linef(importStatement);
		}
		
		for(final String importStatement : this.importStatements) {
			document.linef(importStatement);
		}
		
		document.linef("");
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
		document.linef("			%s", doFormatScript(script));
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
		document.linef("}");
		
		return document.toString();
	}
	
	private String doSearchAndReplace(String script) throws ScriptException {
		script = doSearchAndReplaceImports(script);
		script = doSearchAndReplacePackages(script);
		script = doSearchAndReplaceSubstitutionVariables(script);
		
		return script;
	}
	
	private String doSearchAndReplaceImports(final String script) {
		final StringBuffer stringBuffer = new StringBuffer(script.length());
		
		final Matcher matcher = PATTERN_IMPORT.matcher(script);
		
		while(matcher.find()) {
			final String importStatement = matcher.group(NAME_IMPORT);
			final String replacement = "";
			
			this.importStatements.add(importStatement);
			
			matcher.appendReplacement(stringBuffer, replacement);
		}
		
		matcher.appendTail(stringBuffer);
		
		return stringBuffer.toString();
	}
	
	private String doSearchAndReplacePackages(final String script) {
		final StringBuffer stringBuffer = new StringBuffer(script.length());
		
		final Matcher matcher = PATTERN_PACKAGE.matcher(script);
		
		while(matcher.find()) {
			final String packageName = matcher.group(NAME_PACKAGE);
			final String replacement = "";
			
			this.packageName.set(packageName);
			
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
		
		if(this.isDumpingSourceCode) {
			System.out.println(sourceCode);
		}
		
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
	
	private static List<String> doCreateImportStatementsOptional() {
		final String importFilename = System.getProperty(PROPERTY_IMPORT);
		
		if(importFilename != null) {
			final File importFile = new File(importFilename);
			
			if(importFile.isFile()) {
				try {
					final List<String> importStatementsOptional = new ArrayList<>(Files.readAllLines(importFile.toPath()));
					
					for(int i = importStatementsOptional.size() - 1; i >= 0; i--) {
						final String importStatementOptional = importStatementsOptional.get(i);
						
						if(!PATTERN_IMPORT.matcher(importStatementOptional).matches()) {
							importStatementsOptional.remove(i);
						}
					}
					
					return importStatementsOptional;
				} catch(final IOException e) {
//					Fall back on default behavior for now.
				}
			}
		}
		
		final List<String> importStatementsOptional = new ArrayList<>();
		
		importStatementsOptional.add("import static java.lang.Math.*;");
		importStatementsOptional.add("import java.awt.*;");
		importStatementsOptional.add("import java.awt.color.*;");
		importStatementsOptional.add("import java.awt.event.*;");
		importStatementsOptional.add("import java.awt.font.*;");
		importStatementsOptional.add("import java.awt.geom.*;");
		importStatementsOptional.add("import java.awt.image.*;");
		importStatementsOptional.add("import java.lang.ref.*;");
		importStatementsOptional.add("import java.lang.reflect.*;");
		importStatementsOptional.add("import java.math.*;");
		importStatementsOptional.add("import java.net.*;");
		importStatementsOptional.add("import java.nio.*;");
		importStatementsOptional.add("import java.nio.channels.*;");
		importStatementsOptional.add("import java.nio.charset.*;");
		importStatementsOptional.add("import java.nio.file.*;");
		importStatementsOptional.add("import java.nio.file.attribute.*;");
		importStatementsOptional.add("import java.text.*;");
		importStatementsOptional.add("import java.util.*;");
		importStatementsOptional.add("import java.util.concurrent.*;");
		importStatementsOptional.add("import java.util.concurrent.atomic.*;");
		importStatementsOptional.add("import java.util.concurrent.locks.*;");
		importStatementsOptional.add("import java.util.jar.*;");
		importStatementsOptional.add("import java.util.logging.*;");
		importStatementsOptional.add("import java.util.prefs.*;");
		importStatementsOptional.add("import java.util.regex.*;");
		importStatementsOptional.add("import java.util.zip.*;");
		importStatementsOptional.add("import javax.swing.*;");
		importStatementsOptional.add("import javax.swing.border.*;");
		importStatementsOptional.add("import javax.swing.colorchooser.*;");
		importStatementsOptional.add("import javax.swing.event.*;");
		importStatementsOptional.add("import javax.swing.filechooser.*;");
		importStatementsOptional.add("import javax.swing.table.*;");
		importStatementsOptional.add("import javax.swing.text.*;");
		importStatementsOptional.add("import javax.swing.tree.*;");
		importStatementsOptional.add("import javax.swing.undo.*;");
		importStatementsOptional.add("import javax.tools.*;");
		
		return importStatementsOptional;
	}
	
	private static List<String> doCreateImportStatementsRequired() {
		final List<String> importStatementsRequired = new ArrayList<>();
		
		importStatementsRequired.add("import javax.script.*;");
		importStatementsRequired.add("import org.macroing.cel4j.artifact.*;");
		
		return importStatementsRequired;
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
	
	private static String doFormatScript(final String script) {
		final String[] lines = script.trim().split("\n");
		
		final StringBuilder stringBuilder = new StringBuilder();
		
		for(int i = 0; i < lines.length; i++) {
			final String line = lines[i];
			
			stringBuilder.append(i > 0 ? "			" : "");
			stringBuilder.append(line);
			stringBuilder.append(i + 1 < lines.length ? LINE_SEPARATOR : "");
		}
		
		return stringBuilder.toString();
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