/**
 * Provides a {@code ScriptEngine} implementation called Artifact that evaluates a super-set of Java source code.
 * <p>
 * It compiles the code into {@code CompiledScript}s and loads them, using the context {@code ClassLoader}. It caches the {@code CompiledScript}s using a normalized version of the source code provided for that
 * {@code CompiledScript}. By doing so, it won't re-compile if you just add whitespace in other places than {@code String} literals.
 * <p>
 * To demonstrate its use, here is an example:
 * <pre>
 * {@code
 * ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
 * ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension("java");
 * scriptEngine.eval("System.out.println(\"Hello, World!\"); return true;");
 * }
 * </pre>
 * As you can see in the example above, you can return a result. But you don't have to do it. If nothing is returned by you, {@code null} will be returned by default.
 * <p>
 * If a {@code Throwable} is caught while evaluating, it will be reported to the default {@code Thread.UncaughtExceptionHandler}.
 * <p>
 * Certain variables will be substituted with other code. If a variable starts with a dollar sign ({@code $}), followed by one or more word characters ({@code \\w}), that variable will be substituted with code similar to the
 * following example:
 * <p>
 * The variable {@code $variableName} will be substituted with {@code VariableType.class.cast(scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).get("variableName"))}.
 * <p>
 * This makes it easier to write code, but it's probably not a problem-free approach.
 */
package org.macroing.cel4j.artifact;