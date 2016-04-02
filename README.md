CEL4J Artifact (v. 0.0.1)
=========================
CEL4J Artifact is a library that provides a ScriptEngine extension called Artifact, that evaluates a super-set of Java source code.

It's part of the Code Engineering Library for Java (CEL4J) project hosted by Macroing.org.

Supported Features
------------------
* Full Java-compatibility. You can use any Java source code that can be executed from within a Java method.
* With Artifact comes an interactive scripting tool that can be run in CLI- or GUI mode.
* A bunch of packages from the standard Java library are imported by default.
* You don't have to catch an `Exception` thrown by a method. A catch-clause exists by default.
* You can return anything you want from the script, but are not required to.
* You can evaluate Java source code as part of the script itself, using the `eval(String)` method.
* A simpler syntax for `List` creation, such as `$[1, "Hello, World!", new Rectangle()]`.
* A simpler syntax for `Map` creation, such as `$["a" => 1, 2.0D => 'c']`.
* Support for pragmas or pre-processor directives. They all start with `#`.
* One pragma is for importing. Use it like this `#import javax.swing.*;`.
* One pragma is for redefining the package for the class. Use it like this `#package com.company;`.
* You can set a variable to the `ScriptContext` using the `set(String, Object)` method.
* You can get a variable from the `ScriptContext` using the `get(String)` method.
* Variables starting with `$` are treated in a special way. They are substituted with a variable from the `ScriptContext`.
* If a script starts with `uri:` followed by a URI, the resource found by that URI will be loaded and evaluated.

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
 - [CIT Java](https://github.com/macroing/CIT-Java)

Note
----
This library has not been properly released yet. This means, even though it says it's version 1.0.0 in the build.xml file and all Java source code files, it should not be treated as such. When this library gets released, it will be tagged and available on the "releases" page.
