CEL4J Artifact (v. 0.4.0)
=========================
CEL4J Artifact is a library that provides a ScriptEngine implementation called Artifact, that evaluates a super-set of Java source code.

It's part of the Code Engineering Library for Java (CEL4J) project hosted by Macroing.org.

Supported Features
------------------
* Full Java-compatibility. You can use any Java source code that can be executed from within a Java method.
* Some of the more common packages from the standard Java library are imported by default.
* You may override most of the imported packages by specifying the system property ``-Dorg.macroing.cel4j.artifact.import="file.txt"``, which points to a file with one import statement per line.
* You don't have to catch any `Exception`s thrown by a method. The default catch-clause will take care of that for you.
* You can return anything you want from the script, but are not required to.
* You can evaluate Java source code as part of the script itself, using the `eval(String)` method.
* To import packages you can use import statements like `import javax.swing.*;`.
* To change package you can use package statements like `package com.company;`.
* You can set a variable to the `ScriptContext` using the `set(String, Object)` method.
* You can get a variable from the `ScriptContext` using the `get(String)` method.
* Variables starting with `$` are treated in a special way. They are substituted with a cast to their type, as they are stored in the `ScriptContext`.
* A script has access to the `ScriptContext` using the variable `scriptContext`.
* It's possible to dump the generated source code to system out by using the system property `-Dorg.macroing.cel4j.artifact.dump=true`.
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
//Changes the package:
package org.macroing.cel4j.artifact.example;

//Imports all public members of a class using a static import statement:
import static java.lang.Math.*;

double a = 3.0D;
double b = 5.0D;

//Here we use two of the public members that were statically imported:
double max = max(a, b);
double min = min(a, b);

//The eval(String) method can be called from within the script:
Object result = eval(String.format("return %s + %s;", max, min));

//You can set a value to the ScriptContext, given a key:
set("result", result);

//You can get the value from the ScriptContext, given its key:
result = get("result");

//If the key "result" was already associated with a value prior to the evaluation of this script, you could use $result to access it instead.
//$result would be converted to the following code, if it's of type Double like shown above:
//Double.class.cast(scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).get("result"))

System.out.println(result);

//You may optionally return a result from the script:
return result;
```

Imported Packages
-----------------
Artifact has two types of imported packages. These are the required packages that always will be present, and the optional packages that may be overridden.

### Required Packages
These packages are required.

* `javax.script.*`
* `org.macroing.cel4j.artifact.*`

### Optional Packages
These packages are optional and subject to change. It may turn out that there are too many ambiguous classes and interfaces.

It's possible to override these packages. To override them, use a system property called ``org.macroing.cel4j.artifact.import``. The value supplied should be a filename pointing to a file with one import statement per line.

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

Getting Started
---------------
### Apache Ant
To clone this repository, build the project and run it in GUI-mode, you can type the following in Git Bash.
```bash
git clone https://github.com/macroing/CEL4J-Artifact.git
cd CEL4J-Artifact
ant
cd distribution/org.macroing.cel4j.artifact
java -jar org.macroing.cel4j.artifact.jar -g
```

### Gradle
Coming soon...

### Maven
Coming soon...

Interactive Scripting Tool
--------------------------
The interactive scripting tool can be configured to run as either a CLI- or a GUI-program. By default it runs as a CLI-program.

The program uses a set of flags to configure itself. So you can change them to fit your needs. The currently supported flags are the following.

 - The `-e [extension]` flag sets the filename extension for the scripting language to use.
 - The `-g` flag starts the GUI-program.

When you omit the `-e [extension]` flag, Artifact will automatically be used as scripting language. This is just like using `-e java` or `-e .java`.

When you omit the `-g` flag, the CLI-program will automatically be used.

If you use the GUI-program, you will find out that some syntax highlighting is supported.

Dependencies
------------
 - [Java 8 + tools.jar](http://www.java.com).