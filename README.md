CEL4J Artifact (v. 0.2.0)
=========================
CEL4J Artifact is a library that provides a ScriptEngine implementation called Artifact, that evaluates a super-set of Java source code.

It's part of the Code Engineering Library for Java (CEL4J) project hosted by Macroing.org.

Supported Features
------------------
* Full Java-compatibility. You can use any Java source code that can be executed from within a Java method.
* Some of the more common packages from the standard Java library are imported by default.
* You don't have to catch any `Exception`s thrown by a method. A catch-clause exists by default.
* You can return anything you want from the script, but are not required to.
* You can evaluate Java source code as part of the script itself, using the `eval(String)` method.
* Some pragmas or pre-processor directives are supported. They all start with `#`.
* One pragma is for importing. Use it like this `#import javax.swing.*;`.
* One pragma is for redefining the package for the class. Use it like this `#package com.company;`.
* You can set a variable to the `ScriptContext` using the `set(String, Object)` method.
* You can get a variable from the `ScriptContext` using the `get(String)` method.
* Variables starting with `$` are treated in a special way. They are substituted with a cast to a variable from the `ScriptContext`.
* A script has access to the `ScriptContext` using the variable `scriptContext`.
* Artifact comes with an interactive scripting tool that can be run in CLI- or GUI mode.

Examples
--------
```java
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class HelloWorld {
    public static void main(String[] args) {
        try {
//          Create a new ScriptEngineManager:
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            
//          Get the ScriptEngine for Artifact and evaluate a script:
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension("java");
            scriptEngine.eval("System.out.println(\"Hello, World!\");");
        } catch(ScriptException e) {
            e.printStackTrace();
        }
    }
}
```

Dependencies
------------
 - [Java 8 + tools.jar](http://www.java.com).
