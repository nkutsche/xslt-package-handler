# XSLT Package Handler

This project provides a Maven based framework to wrap XSLT packages in jar files and refind them in the classpath.

## Release notes

### 2.1.0 (SNAPSHOT)

* Adds support for Saxon 12.
* Performance optimization for Calabash Configurer 
* Adds XProcConfigurers for Saxon 11 (and 12) based Calabash versions

### 2.0.0

* Redesign of the concept, skip unnecessary Maven plugin mojo for the using project.
* Adds Configurer classes for XProc/Calabash integration
* Adds possibility to specifify multiple packages in one module.

### 1.0.0

* First public release

## Challenge to be Target

XSLT 3.0 comes with a new feature to declare and re-use packages. Packages should be declared by an identifying name and a version. The new `<xsl:use-package>` references the package only by this coordinates - similar to the Maven artifact coordinates. The location of the sources should not be part of the stylesheets, but on configuration level. For Saxon that means that I have to specify in an Saxon configuration file the coordinates and the location paths to the package files. 

And that is where the problems begin if I try to provide the packages as Maven dependencies:

* I have to declare in the Saxon configuration file *and* in the Maven pom.xml the dependencies. *This is duplicate code!*
* I have to know the full path to my package XSLT file. As this is wrapped by a jar file I have to know the path inside the jar file. This is handled by my dependency project and should not be part of the using project. *This is not declarative!*
* The path resolution of the locations is restricted. The URLs are not resolved by the given catalog file. I can not use the [catalogBuilder-plugin](https://github.com/cmarchand/maven-catalogBuilder-plugin) to run my source code without creating a full package. *This is not handy!*

## How to use

Let assume we have a basic project `com.example:lib:1.0` which shoud provide an XSLT package as Maven dependency to the using project `com.example:main:1.0`. The `com.example:lib:1.0` produce a jar artifact which contains the XSLT package as `lib.jar!/com/example/lib/lib.xsl`.

### Store Package Location

The first task is to store the location of the XSLT package on a place where it can be found. Therefore you have to add to the pom.xml of `com.example:lib:1.0` the following plugin call:

```xml
<plugin>
    <groupId>com.nkutsche</groupId>
    <artifactId>xslt-pkg-maven-plugin</artifactId>
    <version>2.0.0</version>
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

#### Full configuration options

```xml

<configuration>
    <outDir>${project.build.directory}/classes</outDir>
    <packagePath>/com/example/lib/lib.xsl</packagePath>
    <!-- Alternative:
        <packagePaths>
            <packagePath>/com/example/lib/lib-1.xsl</packagePath>
            <packagePath>/com/example/lib/lib-2.xsl</packagePath>
        </packagePaths>
    -->
</configuration>
```

### Dependencies and creating the main project classpath

Add the following dependencies to the pom.xml of `com.example:main:1.0`:

```xml
<!-- The dependency to your basic project -->
<dependency>
    <groupId>com.nkutsche</groupId>
    <artifactId>lib</artifactId>
    <version>1.0</version>
</dependency>
<!-- package manager to let Saxon find your XSLT package -->
<dependency>
    <groupId>com.nkutsche</groupId>
    <artifactId>xslt-pkg-managerXXX</artifactId>
    <version>2.0.0</version>
</dependency>
<!-- other dependencies like Saxon, Calabash, etc. -->
```

**Note:**

* Use for `XXX` the values `99`, `100` or `110` depending on the Saxon version you want to use - `9.x`, `10.x` or `11.x`. 

* this dependency does not provide the Saxon final version. It was just compiled with interface classes from different Saxon major versions. You need to add Saxon (or e.g. Calabash) as dependency by your self.

For creating the execution classpath you can use any way you like. Just add all jars from your project inclusive project jar. With Maven I often use the `appassembler-maven-plugin` or the `exec-maven-plugin`.


### Saxon Call

To let Saxon find your XSLT packages in your classpath, you have to call it with the specific initializer class `com.nkutsche.xslt.pkg.handler.PackageManager`. On the Saxon CLI the switch `-init` can be used.

Example call:

```
mvn exec:exec -Dexec.executable="java" -Dexec.args="-cp %%classpath net.sf.saxon.Transform -init:com.nkutsche.xslt.pkg.handler.PackageManager ..."
```

### XML Calabash Call

Since xslt-pkg-manager 2.0.0 there is also integration classes for Calabash to use it in XProc. To enable the XSLT Packages for Calabash you have to specify the class `com.nkutsche.xslt.pkg.handler.XProcConfigurer` as XProc configurer using the configuration option `xproc-configurer`. In the Calabash configuration file it would look like this:

```xml
<xproc-config xmlns="http://xmlcalabash.com/ns/configuration">
    <xproc-configurer class-name="com.nkutsche.xslt.pkg.handler.XProcConfigurer"/>
</xproc-config>
```

On the Calabash CLI it is possible by setting the specific system property:

```
java -Dcom.xmlcalabash.xproc-configurer=com.nkutsche.xslt.pkg.handler.XProcConfigurer ...
```

### Sample Project

Find [here](sample/sample-project/) a working sample project.

## How it works

### Goal `package-info`

The goal `package-info` will add a property file to your jar artifact. The path of this property file will have always be: `/META-INF/xslt/com.nkutsche.xslt.packages`. In this property file it stores the path of your XSLT package file(s):

```
/com/example/lib/lib.xsl
```

This is everything what to do in the project `com.example:lib:1.0`.

### Saxon initializer | XProc configurer

The Saxon initializer or XProc configurer searches in each jar file of the current classpath for files with the path `/META-INF/xslt/com.nkutsche.xslt.packages`. So it finds all available XSLT packages. By parsing the XSLT files it identifies the package names and versions and notifies Saxon about it.



