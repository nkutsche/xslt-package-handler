call mvn -P 120 exec:exec -Dexec.executable="java" -Dexec.args="-cp %%%%classpath net.sf.saxon.Transform -init:com.nkutsche.xslt.pkg.handler.PackageManager -it -xsl:src/main/resources/main.xsl"

call mvn -P 100 exec:exec -Dexec.executable="java" -Dexec.args="%JAVA_OPTS% -cp %%%%classpath net.sf.saxon.Transform -init:com.nkutsche.xslt.pkg.handler.PackageManager -it -xsl:src/main/resources/main.xsl"

call mvn -P 99 exec:exec -Dexec.executable="java" -Dexec.args="-cp %%%%classpath net.sf.saxon.Transform -init:com.nkutsche.xslt.pkg.handler.PackageManager -it -xsl:src/main/resources/main.xsl"

call mvn -P 110 exec:exec -Dexec.executable="java" -Dexec.args="-cp %%%%classpath net.sf.saxon.Transform -init:com.nkutsche.xslt.pkg.handler.PackageManager -it -xsl:src/main/resources/main.xsl"