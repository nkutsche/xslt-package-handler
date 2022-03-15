# XSLT Package Handler

This project provides a Maven based framework to wrap XSLT packages in jar files and refind them in the classpath.

## Release notes

### 1.0.0

* First public release

## Challenge to be Target

XSLT 3.0 comes with a new feature to declare and re-use packages. Packages should be declared by an identifying name and a version. The new `<xsl:use-package>` references the package only by this coordinates - similar to Maven artifact coordinates. The location of the sources should not be part of the stylesheets, but on configuration level. For Saxon that means that I have to specify in an Saxon configuration file the coordinates and the location paths to the package files. 

And that is where the problems begin if I try to provide the packages as Maven dependencies:

* I have to declare in the Saxon configuration file *and* in the Maven pom.xml the dependencies. *This is duplicate code!*
* I have to know the full path to my package XSLT file. As this is wrapped by a jar file I have to know the path inside the jar file. This is handled by my dependency and should not be part of my main project. *This is not declarative!*
* The path resolution of the locations is restricted. The URLs are not resolved by the given catalog file. I can not use the [catalogBuilder-plugin](https://github.com/cmarchand/maven-catalogBuilder-plugin) to run my source code without creating a full package. *This is not handy!*

## How to use

Let assume we have a project `com.example:lib:1.0` which shoud provide an XSLT package as Maven dependency to the project `com.example:main:1.0`. The `com.example:lib:1.0` produce jar artifact which contains the XSLT package as `lib.jar!/com/example/lib/lib.xsl`.

### Store Package Location

The first task is to store the location of the XSLT package on a place where it can be found. Therefore you have to add to the pom.xml of `com.example:lib:1.0` the following plugin call:

```xml
<plugin>
    <groupId>com.nkutsche</groupId>
    <artifactId>xslt-pkg-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>package-info</goal>
            </goals>
            <configuration>
                <packagePath>/com/example/lib/lib.xsl</packagePath>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Create Dependency List

As the jar archives in the Java classpath can not be browsable you need at first to create a dependency list for your main project. Therefor you add to the pom.xml of `com.example:main:1.0` the following plugin call:

```xml
<plugin>
    <groupId>com.nkutsche</groupId>
    <artifactId>xslt-pkg-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>dependency-list</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Other dependencies and creating the main project classpath

Add the following dependency to the pom.xml of `com.example:main:1.0`:

<dependency>
    <groupId>com.nkutsche</groupId>
    <artifactId>xslt-pkg-managerXXX</artifactId>
    <version>1.0.0</version>
</dependency>

Use for `XXX` the values `99`, `100` or `110` depending on the Saxon version you want to use - `9.x`, `10.x` or `11.x`. *Note:* this dependency does not provide the Saxon final version. It was just compiled with interface classes from different Saxon major versions.

For creating the execution classpath you can use any way you like. Just add all jars from your project inclusive project jar. With Maven I often use the `appassembler-maven-plugin` or the `exec-maven-plugin`.


### Saxon Call

To let Saxon find your dependency XSLT packages, you have to call it with the specific initializer class `com.nkutsche.xslt.pkg.handler.PackageManager`. On the Saxon CLI the switch `-init` can be used.

Example call:

```
mvn exec:exec -Dexec.executable="java" -Dexec.args="-cp %%classpath net.sf.saxon.Transform -init:com.nkutsche.xslt.pkg.handler.PackageManager ..."
```

## How it works

### Goal `package-info`

The goal `package-info` will add a property file to your jar artifact. The path of this property file will have always the same pattern: `/META-INF/xslt/{groupId}/{artifactId}/packages` - so in our case `/META-INF/xslt/com.example/lib/packages`. In this property file it stores the path of your XSLT package file:

```properties
com.example.lib=/com/example/lib/lib.xsl
```

This is everything what to do in the project `com.example:lib:1.0`.

### Goal `dependency-list`

As you can not browse inside of the classpath jars in Java, it is necessary to have a list of all dependencies. The plugin goal `dependency-list` creates a full list of all project dependencies in your jar artifact - always in the path `META-INF/xslt/dependencies`.

### Saxon initializer

The Saxon initializer searches for a such a dependency list and checks if one dependency jar file was marked as XSLT package (with a property file `/META-INF/xslt/{groupId}/{artifactId}/packages`). If so, it notifies Saxon about it ensures that they can be found. 

