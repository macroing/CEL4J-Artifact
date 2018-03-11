CEL4J Artifact (v. 0.3.0)
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
* To import packages you can use import statements like `import javax.swing.*;`.
* To change package you can use package statements like `package com.company;`.
* You can set a variable to the `ScriptContext` using the `set(String, Object)` method.
* You can get a variable from the `ScriptContext` using the `get(String)` method.
* Variables starting with `$` are treated in a special way. They are substituted with a cast to a variable from the `ScriptContext`.
* A script has access to the `ScriptContext` using the variable `scriptContext`.
* Artifact comes with an interactive scripting tool that can be run in CLI- or GUI mode.

Examples
--------
The following example demonstrates how you can use Artifact:

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

The following example demonstrates some of the features provided by Artifact:

```java
//Here we show how to change the package:
package org.macroing.cel4j.artifact.example;

//Here we show how to import all public members of a class using a static import statement:
import static java.lang.Math.*;

double a = 3.0D;
double b = 5.0D;

//Here we show that the static import worked:
double max = max(a, b);
double min = min(a, b);

//Here we show the eval(String) method that can be called from within the script:
Object result = eval(String.format("return %s + %s;", max, min));

//Here we show how to set a value given a key:
set("result", result);

//Here we show how to get the value using its key once set:
result = get("result");

//If the key "result" was already associated with a value prior to the evaluation of this script, you could use $result to access it instead.
//$result, if it's a Double like shown above, would be converted to the following code:
//Double.class.cast(scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).get("result"))

System.out.println(result);

//Here we show that we can return a result from the script:
return result;
```

Imported Packages
-----------------
The packages currently imported, as shown in the list below, may change in the future. It may turn out that there are too many ambiguous classes and interfaces.

* `java.lang.Math.*` (static)
* `java.awt.*`
* `java.awt.color.*`
* `java.awt.event.*`
* `java.awt.font.*`
* `java.awt.geom.*`
* `java.awt.image.*`
* `java.lang.ref.*`
* `java.lang.reflect.*`
* `java.math.*`
* `java.net.*`
* `java.nio.*`
* `java.nio.channels.*`
* `java.nio.charset.*`
* `java.nio.file.*`
* `java.nio.file.attribute.*`
* `java.text.*`
* `java.util.*`
* `java.util.concurrent.*`
* `java.util.concurrent.atomic.*`
* `java.util.concurrent.locks.*`
* `java.util.jar.*`
* `java.util.logging.*`
* `java.util.prefs.*`
* `java.util.regex.*`
* `java.util.zip.*`
* `javax.script.*`
* `javax.swing.*`
* `javax.swing.border.*`
* `javax.swing.colorchooser.*`
* `javax.swing.event.*`
* `javax.swing.filechooser.*`
* `javax.swing.table.*`
* `javax.swing.text.*`
* `javax.swing.tree.*`
* `javax.swing.undo.*`
* `javax.tools.*`
* `org.macroing.cel4j.artifact.*`

Dependencies
------------
 - [Java 8 + tools.jar](http://www.java.com).