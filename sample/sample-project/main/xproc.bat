
call mvn -P 120 exec:exec -Dexec.executable="java" -Dexec.args="-Dcom.xmlcalabash.xproc-configurer=com.nkutsche.xslt.pkg.handler.XProcConfigurer -cp %%%%classpath com.xmlcalabash.drivers.Main src/main/resources/main.xpl"

call mvn -P 110 exec:exec -Dexec.executable="java" -Dexec.args="%JAVA_OPTS% -Dcom.xmlcalabash.xproc-configurer=com.nkutsche.xslt.pkg.handler.XProcConfigurer -cp %%%%classpath com.xmlcalabash.drivers.Main src/main/resources/main.xpl"

call mvn -P 100 exec:exec -Dexec.executable="java" -Dexec.args="-Dcom.xmlcalabash.xproc-configurer=com.nkutsche.xslt.pkg.handler.XProcConfigurer -cp %%%%classpath com.xmlcalabash.drivers.Main src/main/resources/main.xpl"

call mvn -P 99 exec:exec -Dexec.executable="java" -Dexec.args="-cp %%%%classpath com.xmlcalabash.drivers.Main --config src/main/resources/cfg/configuration.xml src/main/resources/main.xpl"

:eof