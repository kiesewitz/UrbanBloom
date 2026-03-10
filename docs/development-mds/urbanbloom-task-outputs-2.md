# Tasks

## mobile: run
Executing task: flutter run 

Connected devices:
Chrome (web) • chrome • web-javascript • Google Chrome 145.0.7632.160
Edge (web)   • edge   • web-javascript • Microsoft Edge 145.0.3800.82
[1]: Chrome (chrome)
[2]: Edge (edge)
Please choose one (or "q" to quit): 1
Launching lib\main.dart on Chrome in debug mode...
lib/main.dart:31:22: Error: This requires the experimental 'dot-shorthands' language feature to be enabled.
Try passing the '--enable-experiment=dot-shorthands' command line option.
        colorScheme: .fromSeed(seedColor: Colors.deepPurple),
                     ^
lib/main.dart:105:30: Error: This requires the experimental 'dot-shorthands' language feature to be enabled.
Try passing the '--enable-experiment=dot-shorthands' command line option.
          mainAxisAlignment: .center,
                             ^
Waiting for connection from debug service on Chrome...             30,0s
Failed to compile application.

 *  The terminal process "C:\WINDOWS\System32\WindowsPowerShell\v1.0\powershell.exe -Command flutter run" terminated with exit code: 1. 
 *  Terminal will be reused by tasks, press any key to close it. 

## web: run
Executing task: flutter run -d chrome 

Launching lib\main.dart on Chrome in debug mode...
Waiting for connection from debug service on Chrome...             18,2s

Flutter run key commands.
r Hot reload. 
R Hot restart.
h List all available interactive commands.
d Detach (terminate "flutter run" but leave application running).
c Clear the screen
q Quit (terminate the application on the device).

Debug service listening on ws://127.0.0.1:63992/sDAnGDm2OdQ=/ws
A Dart VM Service on Chrome is available at: http://127.0.0.1:63992/sDAnGDm2OdQ=
The Flutter DevTools debugger and profiler on Chrome is available at:
http://127.0.0.1:63992/sDAnGDm2OdQ=/devtools/?uri=ws://127.0.0.1:63992/sDAnGDm2OdQ=/ws
Starting application from main method in: org-dartlang-app:/web_entrypoint.dart.

## server: run
Executing task: mvn -pl server-app spring-boot:run 

[INFO] Scanning for projects...
[INFO] 
[INFO] ---------------------< com.urbanbloom:server-app >----------------------
[INFO] Building School Library Application 0.0.1-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] >>> spring-boot:3.2.0:run (default-cli) > test-compile @ server-app >>>
[WARNING] The POM for com.urbanbloom:urbanbloom-shared:jar:0.0.1-SNAPSHOT is missing, no dependency information available
[WARNING] The POM for com.urbanbloom:urbanbloom-module-lending:jar:0.0.1-SNAPSHOT is missing, no dependency information available
[WARNING] The POM for com.urbanbloom:urbanbloom-module-catalog:jar:0.0.1-SNAPSHOT is missing, no dependency information available
[WARNING] The POM for com.urbanbloom:urbanbloom-module-notification:jar:0.0.1-SNAPSHOT is missing, no dependency information available  
[WARNING] The POM for com.urbanbloom:urbanbloom-module-reminding:jar:0.0.1-SNAPSHOT is missing, no dependency information available     
[WARNING] The POM for com.urbanbloom:urbanbloom-module-user:jar:0.0.1-SNAPSHOT is missing, no dependency information available
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-web/3.2.0/spring-boot-starter-web-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-web/3.2.0/spring-boot-starter-web-3.2.0.pom (2.9 kB at 6.8 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter/3.2.0/spring-boot-starter-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter/3.2.0/spring-boot-starter-3.2.0.pom (3.0 kB at 58 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot/3.2.0/spring-boot-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot/3.2.0/spring-boot-3.2.0.pom (2.2 kB at 41 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-autoconfigure/3.2.0/spring-boot-autoconfigure-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-autoconfigure/3.2.0/spring-boot-autoconfigure-3.2.0.pom (2.1 kB at 42 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/jakarta/annotation/jakarta.annotation-api/2.1.1/jakarta.annotation-api-2.1.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/jakarta/annotation/jakarta.annotation-api/2.1.1/jakarta.annotation-api-2.1.1.pom (16 kB at 200 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/yaml/snakeyaml/2.2/snakeyaml-2.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/yaml/snakeyaml/2.2/snakeyaml-2.2.pom (21 kB at 417 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-json/3.2.0/spring-boot-starter-json-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-json/3.2.0/spring-boot-starter-json-3.2.0.pom (3.1 kB at 62 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-web/6.1.1/spring-web-6.1.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-web/6.1.1/spring-web-6.1.1.pom (2.4 kB at 42 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.3/jackson-databind-2.15.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.3/jackson-databind-2.15.3.pom (19 kB at 345 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/jackson-base/2.15.3/jackson-base-2.15.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/jackson-base/2.15.3/jackson-base-2.15.3.pom (11 kB at 262 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/junit/junit-bom/5.9.2/junit-bom-5.9.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/junit/junit-bom/5.9.2/junit-bom-5.9.2.pom (5.6 kB at 117 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.15.3/jackson-annotations-2.15.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.15.3/jackson-annotations-2.15.3.pom (7.1 kB at 136 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-core/2.15.3/jackson-core-2.15.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-core/2.15.3/jackson-core-2.15.3.pom (9.8 kB at 102 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.15.3/jackson-datatype-jdk8-2.15.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.15.3/jackson-datatype-jdk8-2.15.3.pom (2.6 kB at 54 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/module/jackson-modules-java8/2.15.3/jackson-modules-java8-2.15.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/module/jackson-modules-java8/2.15.3/jackson-modules-java8-2.15.3.pom (3.1 kB at 55 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.15.3/jackson-datatype-jsr310-2.15.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.15.3/jackson-datatype-jsr310-2.15.3.pom (4.9 kB at 89 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/module/jackson-module-parameter-names/2.15.3/jackson-module-parameter-names-2.15.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/module/jackson-module-parameter-names/2.15.3/jackson-module-parameter-names-2.15.3.pom (4.4 kB at 86 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-tomcat/3.2.0/spring-boot-starter-tomcat-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-tomcat/3.2.0/spring-boot-starter-tomcat-3.2.0.pom (3.1 kB at 62 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-core/10.1.16/tomcat-embed-core-10.1.16.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-core/10.1.16/tomcat-embed-core-10.1.16.pom (1.8 kB at 32 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-el/10.1.16/tomcat-embed-el-10.1.16.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-el/10.1.16/tomcat-embed-el-10.1.16.pom (1.5 kB at 31 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-websocket/10.1.16/tomcat-embed-websocket-10.1.16.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-websocket/10.1.16/tomcat-embed-websocket-10.1.16.pom (1.8 kB at 33 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-webmvc/6.1.1/spring-webmvc-6.1.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-webmvc/6.1.1/spring-webmvc-6.1.1.pom (2.9 kB at 64 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-data-jpa/3.2.0/spring-boot-starter-data-jpa-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-data-jpa/3.2.0/spring-boot-starter-data-jpa-3.2.0.pom (2.9 kB at 67 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-aop/3.2.0/spring-boot-starter-aop-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-aop/3.2.0/spring-boot-starter-aop-3.2.0.pom (2.5 kB at 42 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/aspectj/aspectjweaver/1.9.20.1/aspectjweaver-1.9.20.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/aspectj/aspectjweaver/1.9.20.1/aspectjweaver-1.9.20.1.pom (2.1 kB at 42 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-jdbc/3.2.0/spring-boot-starter-jdbc-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-jdbc/3.2.0/spring-boot-starter-jdbc-3.2.0.pom (2.5 kB at 44 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/zaxxer/HikariCP/5.0.1/HikariCP-5.0.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/zaxxer/HikariCP/5.0.1/HikariCP-5.0.1.pom (25 kB at 475 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-jdbc/6.1.1/spring-jdbc-6.1.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-jdbc/6.1.1/spring-jdbc-6.1.1.pom (2.4 kB at 21 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-tx/6.1.1/spring-tx-6.1.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-tx/6.1.1/spring-tx-6.1.1.pom (2.2 kB at 46 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/hibernate/orm/hibernate-core/6.3.1.Final/hibernate-core-6.3.1.Final.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/hibernate/orm/hibernate-core/6.3.1.Final/hibernate-core-6.3.1.Final.pom (5.8 kB at 117 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/jakarta/transaction/jakarta.transaction-api/2.0.1/jakarta.transaction-api-2.0.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/jakarta/transaction/jakarta.transaction-api/2.0.1/jakarta.transaction-api-2.0.1.pom (14 kB at 243 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/eclipse/ee4j/project/1.0.6/project-1.0.6.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/eclipse/ee4j/project/1.0.6/project-1.0.6.pom (13 kB at 261 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/jboss/logging/jboss-logging/3.5.3.Final/jboss-logging-3.5.3.Final.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/jboss/logging/jboss-logging/3.5.3.Final/jboss-logging-3.5.3.Final.pom (19 kB at 344 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/jboss/logging/logging-parent/1.0.1.Final/logging-parent-1.0.1.Final.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/jboss/logging/logging-parent/1.0.1.Final/logging-parent-1.0.1.Final.pom (6.0 kB at 121 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/hibernate/common/hibernate-commons-annotations/6.0.6.Final/hibernate-commons-annotations-6.0.6.Final.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/hibernate/common/hibernate-commons-annotations/6.0.6.Final/hibernate-commons-annotations-6.0.6.Final.pom (2.1 kB at 37 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/io/smallrye/jandex/3.1.2/jandex-3.1.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/io/smallrye/jandex/3.1.2/jandex-3.1.2.pom (7.0 kB at 102 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/io/smallrye/jandex-parent/3.1.2/jandex-parent-3.1.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/io/smallrye/jandex-parent/3.1.2/jandex-parent-3.1.2.pom (7.2 kB at 155 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/io/smallrye/smallrye-build-parent/39/smallrye-build-parent-39.pom
Downloaded from central: https://repo.maven.apache.org/maven2/io/smallrye/smallrye-build-parent/39/smallrye-build-parent-39.pom (28 kB at 602 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/classmate/1.6.0/classmate-1.6.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/classmate/1.6.0/classmate-1.6.0.pom (6.6 kB at 133 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/oss-parent/55/oss-parent-55.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/oss-parent/55/oss-parent-55.pom (24 kB at 445 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/jakarta/xml/bind/jakarta.xml.bind-api/4.0.1/jakarta.xml.bind-api-4.0.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/jakarta/xml/bind/jakarta.xml.bind-api/4.0.1/jakarta.xml.bind-api-4.0.1.pom (13 kB at 227 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/jakarta/xml/bind/jakarta.xml.bind-api-parent/4.0.1/jakarta.xml.bind-api-parent-4.0.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/jakarta/xml/bind/jakarta.xml.bind-api-parent/4.0.1/jakarta.xml.bind-api-parent-4.0.1.pom (9.2 kB at 167 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/jakarta/activation/jakarta.activation-api/2.1.2/jakarta.activation-api-2.1.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/jakarta/activation/jakarta.activation-api/2.1.2/jakarta.activation-api-2.1.2.pom (18 kB at 335 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/jaxb-runtime/4.0.4/jaxb-runtime-4.0.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/jaxb-runtime/4.0.4/jaxb-runtime-4.0.4.pom (11 kB at 221 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/sun/xml/bind/mvn/jaxb-runtime-parent/4.0.4/jaxb-runtime-parent-4.0.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/sun/xml/bind/mvn/jaxb-runtime-parent/4.0.4/jaxb-runtime-parent-4.0.4.pom (1.2 kB at 21 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/sun/xml/bind/mvn/jaxb-parent/4.0.4/jaxb-parent-4.0.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/sun/xml/bind/mvn/jaxb-parent/4.0.4/jaxb-parent-4.0.4.pom (35 kB at 637 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/sun/xml/bind/jaxb-bom-ext/4.0.4/jaxb-bom-ext-4.0.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/sun/xml/bind/jaxb-bom-ext/4.0.4/jaxb-bom-ext-4.0.4.pom (3.5 kB at 68 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/jaxb-core/4.0.4/jaxb-core-4.0.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/jaxb-core/4.0.4/jaxb-core-4.0.4.pom (3.7 kB at 89 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/angus-activation/2.0.1/angus-activation-2.0.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/angus-activation/2.0.1/angus-activation-2.0.1.pom (4.0 kB at 87 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/angus-activation-project/2.0.1/angus-activation-project-2.0.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/angus-activation-project/2.0.1/angus-activation-project-2.0.1.pom (20 kB at 434 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/txw2/4.0.4/txw2-4.0.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/txw2/4.0.4/txw2-4.0.4.pom (1.8 kB at 37 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/sun/xml/bind/mvn/jaxb-txw-parent/4.0.4/jaxb-txw-parent-4.0.4.pom     
Downloaded from central: https://repo.maven.apache.org/maven2/com/sun/xml/bind/mvn/jaxb-txw-parent/4.0.4/jaxb-txw-parent-4.0.4.pom (1.2 kB at 26 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/sun/istack/istack-commons-runtime/4.1.2/istack-commons-runtime-4.1.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/sun/istack/istack-commons-runtime/4.1.2/istack-commons-runtime-4.1.2.pom (1.6 kB at 37 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/sun/istack/istack-commons/4.1.2/istack-commons-4.1.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/sun/istack/istack-commons/4.1.2/istack-commons-4.1.2.pom (26 kB at 598 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/jakarta/inject/jakarta.inject-api/2.0.1/jakarta.inject-api-2.0.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/jakarta/inject/jakarta.inject-api/2.0.1/jakarta.inject-api-2.0.1.pom (5.9 kB at 132 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/antlr/antlr4-runtime/4.10.1/antlr4-runtime-4.10.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/antlr/antlr4-runtime/4.10.1/antlr4-runtime-4.10.1.pom (3.5 kB at 65 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/antlr/antlr4-master/4.10.1/antlr4-master-4.10.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/antlr/antlr4-master/4.10.1/antlr4-master-4.10.1.pom (4.4 kB at 64 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/data/spring-data-jpa/3.2.0/spring-data-jpa-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/data/spring-data-jpa/3.2.0/spring-data-jpa-3.2.0.pom (11 kB at 217 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/data/spring-data-jpa-parent/3.2.0/spring-data-jpa-parent-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/data/spring-data-jpa-parent/3.2.0/spring-data-jpa-parent-3.2.0.pom (6.9 kB at 140 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/data/build/spring-data-parent/3.2.0/spring-data-parent-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/data/build/spring-data-parent/3.2.0/spring-data-parent-3.2.0.pom (41 kB at 817 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/data/build/spring-data-build/3.2.0/spring-data-build-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/data/build/spring-data-build/3.2.0/spring-data-build-3.2.0.pom (7.3 kB at 153 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/testcontainers/testcontainers-bom/1.19.2/testcontainers-bom-1.19.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/testcontainers/testcontainers-bom/1.19.2/testcontainers-bom-1.19.2.pom (9.0 kB at 180 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-framework-bom/6.1.0/spring-framework-bom-6.1.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-framework-bom/6.1.0/spring-framework-bom-6.1.0.pom (5.8 kB at 87 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/data/spring-data-commons/3.2.0/spring-data-commons-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/data/spring-data-commons/3.2.0/spring-data-commons-3.2.0.pom (9.9 kB at 155 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-orm/6.1.1/spring-orm-6.1.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-orm/6.1.1/spring-orm-6.1.1.pom (2.6 kB at 44 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-aspects/6.1.1/spring-aspects-6.1.1.pom        
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-aspects/6.1.1/spring-aspects-6.1.1.pom (2.0 kB at 38 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-security/3.2.0/spring-boot-starter-security-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-security/3.2.0/spring-boot-starter-security-3.2.0.pom (2.7 kB at 48 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-config/6.2.0/spring-security-config-6.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-config/6.2.0/spring-security-config-6.2.0.pom (2.8 kB at 52 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-core/6.2.0/spring-security-core-6.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-core/6.2.0/spring-security-core-6.2.0.pom (3.2 kB at 62 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-crypto/6.2.0/spring-security-crypto-6.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-crypto/6.2.0/spring-security-crypto-6.2.0.pom (1.9 kB at 35 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-web/6.2.0/spring-security-web-6.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-web/6.2.0/spring-security-web-6.2.0.pom (3.2 kB at 55 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-actuator/3.2.0/spring-boot-starter-actuator-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-actuator/3.2.0/spring-boot-starter-actuator-3.2.0.pom (2.8 kB at 59 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-actuator-autoconfigure/3.2.0/spring-boot-actuator-autoconfigure-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-actuator-autoconfigure/3.2.0/spring-boot-actuator-autoconfigure-3.2.0.pom (2.9 kB at 54 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-actuator/3.2.0/spring-boot-actuator-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-actuator/3.2.0/spring-boot-actuator-3.2.0.pom (2.0 kB at 46 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/io/micrometer/micrometer-jakarta9/1.12.0/micrometer-jakarta9-1.12.0.pom  
Downloaded from central: https://repo.maven.apache.org/maven2/io/micrometer/micrometer-jakarta9/1.12.0/micrometer-jakarta9-1.12.0.pom (3.8 kB at 80 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/io/micrometer/micrometer-core/1.12.0/micrometer-core-1.12.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/io/micrometer/micrometer-core/1.12.0/micrometer-core-1.12.0.pom (10 kB at 227 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/hdrhistogram/HdrHistogram/2.1.12/HdrHistogram-2.1.12.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/hdrhistogram/HdrHistogram/2.1.12/HdrHistogram-2.1.12.pom (11 kB at 180 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/latencyutils/LatencyUtils/2.0.3/LatencyUtils-2.0.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/latencyutils/LatencyUtils/2.0.3/LatencyUtils-2.0.3.pom (7.2 kB at 129 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/postgresql/postgresql/42.7.1/postgresql-42.7.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/postgresql/postgresql/42.7.1/postgresql-42.7.1.pom (2.9 kB at 55 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/checkerframework/checker-qual/3.41.0/checker-qual-3.41.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/checkerframework/checker-qual/3.41.0/checker-qual-3.41.0.pom (2.1 kB at 37 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/flywaydb/flyway-core/9.22.3/flyway-core-9.22.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/flywaydb/flyway-core/9.22.3/flyway-core-9.22.3.pom (8.8 kB at 200 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/flywaydb/flyway-parent/9.22.3/flyway-parent-9.22.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/flywaydb/flyway-parent/9.22.3/flyway-parent-9.22.3.pom (35 kB at 379 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/dataformat/jackson-dataformat-toml/2.15.3/jackson-dataformat-toml-2.15.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/dataformat/jackson-dataformat-toml/2.15.3/jackson-dataformat-toml-2.15.3.pom (3.5 kB at 68 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/dataformat/jackson-dataformats-text/2.15.3/jackson-dataformats-text-2.15.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/dataformat/jackson-dataformats-text/2.15.3/jackson-dataformats-text-2.15.3.pom (3.5 kB at 71 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.pom (9.4 kB at 191 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/google/code/gson/gson-parent/2.10.1/gson-parent-2.10.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/google/code/gson/gson-parent/2.10.1/gson-parent-2.10.1.pom (13 kB at 266 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-oauth2-resource-server/3.2.0/spring-boot-starter-oauth2-resource-server-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-oauth2-resource-server/3.2.0/spring-boot-starter-oauth2-resource-server-3.2.0.pom (3.0 kB at 57 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-resource-server/6.2.0/spring-security-oauth2-resource-server-6.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-resource-server/6.2.0/spring-security-oauth2-resource-server-6.2.0.pom (2.7 kB at 58 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-core/6.2.0/spring-security-oauth2-core-6.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-core/6.2.0/spring-security-oauth2-core-6.2.0.pom (2.5 kB at 52 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-jose/6.2.0/spring-security-oauth2-jose-6.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-jose/6.2.0/spring-security-oauth2-jose-6.2.0.pom (2.7 kB at 60 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/nimbusds/nimbus-jose-jwt/9.24.4/nimbus-jose-jwt-9.24.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/nimbusds/nimbus-jose-jwt/9.24.4/nimbus-jose-jwt-9.24.4.pom (12 kB at 226 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/github/stephenc/jcip/jcip-annotations/1.0-1/jcip-annotations-1.0-1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/github/stephenc/jcip/jcip-annotations/1.0-1/jcip-annotations-1.0-1.pom (5.4 kB at 81 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-mail/3.2.0/spring-boot-starter-mail-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-mail/3.2.0/spring-boot-starter-mail-3.2.0.pom (2.5 kB at 49 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-context-support/6.1.1/spring-context-support-6.1.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-context-support/6.1.1/spring-context-support-6.1.1.pom (2.4 kB at 48 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/jakarta.mail/2.0.2/jakarta.mail-2.0.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/jakarta.mail/2.0.2/jakarta.mail-2.0.2.pom (12 kB at 239 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/all/2.0.2/all-2.0.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/all/2.0.2/all-2.0.2.pom (31 kB at 533 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/mapstruct/mapstruct/1.5.5.Final/mapstruct-1.5.5.Final.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/mapstruct/mapstruct/1.5.5.Final/mapstruct-1.5.5.Final.pom (1.8 kB at 32 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-test/3.2.0/spring-boot-starter-test-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-test/3.2.0/spring-boot-starter-test-3.2.0.pom (5.0 kB at 92 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-test/3.2.0/spring-boot-test-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-test/3.2.0/spring-boot-test-3.2.0.pom (2.0 kB at 41 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-test-autoconfigure/3.2.0/spring-boot-test-autoconfigure-3.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-test-autoconfigure/3.2.0/spring-boot-test-autoconfigure-3.2.0.pom (2.5 kB at 54 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/jayway/jsonpath/json-path/2.8.0/json-path-2.8.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/jayway/jsonpath/json-path/2.8.0/json-path-2.8.0.pom (1.9 kB at 32 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/net/minidev/json-smart/2.5.0/json-smart-2.5.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/net/minidev/json-smart/2.5.0/json-smart-2.5.0.pom (9.2 kB at 191 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/net/minidev/accessors-smart/2.5.0/accessors-smart-2.5.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/net/minidev/accessors-smart/2.5.0/accessors-smart-2.5.0.pom (11 kB at 168 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/ow2/asm/asm/9.3/asm-9.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/ow2/asm/asm/9.3/asm-9.3.pom (2.4 kB at 49 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/ow2/ow2/1.5/ow2-1.5.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/ow2/ow2/1.5/ow2-1.5.pom (11 kB at 194 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/awaitility/awaitility/4.2.0/awaitility-4.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/awaitility/awaitility/4.2.0/awaitility-4.2.0.pom (3.5 kB at 68 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/awaitility/awaitility-parent/4.2.0/awaitility-parent-4.2.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/awaitility/awaitility-parent/4.2.0/awaitility-parent-4.2.0.pom (10 kB at 180 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest/2.2/hamcrest-2.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest/2.2/hamcrest-2.2.pom (1.1 kB at 26 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/junit/jupiter/junit-jupiter/5.10.1/junit-jupiter-5.10.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/junit/jupiter/junit-jupiter/5.10.1/junit-jupiter-5.10.1.pom (3.2 kB at 63 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/junit/jupiter/junit-jupiter-params/5.10.1/junit-jupiter-params-5.10.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/junit/jupiter/junit-jupiter-params/5.10.1/junit-jupiter-params-5.10.1.pom (3.0 kB at 51 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/mockito/mockito-junit-jupiter/5.7.0/mockito-junit-jupiter-5.7.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/mockito/mockito-junit-jupiter/5.7.0/mockito-junit-jupiter-5.7.0.pom (2.3 kB at 47 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/skyscreamer/jsonassert/1.5.1/jsonassert-1.5.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/skyscreamer/jsonassert/1.5.1/jsonassert-1.5.1.pom (5.2 kB at 96 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/vaadin/external/google/android-json/0.0.20131108.vaadin1/android-json-0.0.20131108.vaadin1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/vaadin/external/google/android-json/0.0.20131108.vaadin1/android-json-0.0.20131108.vaadin1.pom (2.8 kB at 54 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/xmlunit/xmlunit-core/2.9.1/xmlunit-core-2.9.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/xmlunit/xmlunit-core/2.9.1/xmlunit-core-2.9.1.pom (2.4 kB at 39 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/xmlunit/xmlunit-parent/2.9.1/xmlunit-parent-2.9.1.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/xmlunit/xmlunit-parent/2.9.1/xmlunit-parent-2.9.1.pom (21 kB at 424 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/testcontainers/testcontainers/1.19.4/testcontainers-1.19.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/testcontainers/testcontainers/1.19.4/testcontainers-1.19.4.pom (2.6 kB at 59 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/junit/junit/4.13.2/junit-4.13.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/junit/junit/4.13.2/junit-4.13.2.pom (27 kB at 563 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest-core/2.2/hamcrest-core-2.2.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest-core/2.2/hamcrest-core-2.2.pom (1.4 kB at 27 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/commons/commons-compress/1.25.0/commons-compress-1.25.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/commons/commons-compress/1.25.0/commons-compress-1.25.0.pom (22 kB at 482 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/commons/commons-parent/64/commons-parent-64.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/commons/commons-parent/64/commons-parent-64.pom (78 kB at 1.5 MB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/rnorth/duct-tape/duct-tape/1.0.8/duct-tape-1.0.8.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/rnorth/duct-tape/duct-tape/1.0.8/duct-tape-1.0.8.pom (6.2 kB at 107 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/jetbrains/annotations/17.0.0/annotations-17.0.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/jetbrains/annotations/17.0.0/annotations-17.0.0.pom (1.4 kB at 30 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-api/3.3.4/docker-java-api-3.3.4.pom   
Downloaded from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-api/3.3.4/docker-java-api-3.3.4.pom (2.3 kB at 23 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-parent/3.3.4/docker-java-parent-3.3.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-parent/3.3.4/docker-java-parent-3.3.4.pom (12 kB at 239 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-transport-zerodep/3.3.4/docker-java-transport-zerodep-3.3.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-transport-zerodep/3.3.4/docker-java-transport-zerodep-3.3.4.pom (4.1 kB at 79 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-transport/3.3.4/docker-java-transport-3.3.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-transport/3.3.4/docker-java-transport-3.3.4.pom (1.6 kB at 31 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/testcontainers/postgresql/1.19.4/postgresql-1.19.4.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/testcontainers/postgresql/1.19.4/postgresql-1.19.4.pom (1.5 kB at 28 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/testcontainers/jdbc/1.19.3/jdbc-1.19.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/testcontainers/jdbc/1.19.3/jdbc-1.19.3.pom (1.5 kB at 29 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/testcontainers/database-commons/1.19.3/database-commons-1.19.3.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/testcontainers/database-commons/1.19.3/database-commons-1.19.3.pom (1.5 kB at 34 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/testcontainers/testcontainers/1.19.3/testcontainers-1.19.3.pom       
Downloaded from central: https://repo.maven.apache.org/maven2/org/testcontainers/testcontainers/1.19.3/testcontainers-1.19.3.pom (2.6 kB at 58 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/commons/commons-compress/1.24.0/commons-compress-1.24.0.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/commons/commons-compress/1.24.0/commons-compress-1.24.0.pom (22 kB at 495 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/commons/commons-parent/61/commons-parent-61.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/commons/commons-parent/61/commons-parent-61.pom (81 kB at 1.6 MB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-web/3.2.0/spring-boot-starter-web-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-web/3.2.0/spring-boot-starter-web-3.2.0.jar (4.8 kB at 94 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter/3.2.0/spring-boot-starter-3.2.0.jar
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot/3.2.0/spring-boot-3.2.0.jar
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-autoconfigure/3.2.0/spring-boot-autoconfigure-3.2.0.jar
Downloading from central: https://repo.maven.apache.org/maven2/org/yaml/snakeyaml/2.2/snakeyaml-2.2.jar
Downloading from central: https://repo.maven.apache.org/maven2/jakarta/annotation/jakarta.annotation-api/2.1.1/jakarta.annotation-api-2.1.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter/3.2.0/spring-boot-starter-3.2.0.jar (4.8 kB at 95 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-json/3.2.0/spring-boot-starter-json-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-json/3.2.0/spring-boot-starter-json-3.2.0.jar (4.7 kB at 47 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.3/jackson-databind-2.15.3.jar
Downloaded from central: https://repo.maven.apache.org/maven2/jakarta/annotation/jakarta.annotation-api/2.1.1/jakarta.annotation-api-2.1.1.jar (26 kB at 209 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.15.3/jackson-datatype-jdk8-2.15.3.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.15.3/jackson-datatype-jdk8-2.15.3.jar (36 kB at 190 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.15.3/jackson-datatype-jsr310-2.15.3.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/yaml/snakeyaml/2.2/snakeyaml-2.2.jar (334 kB at 1.4 MB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/module/jackson-module-parameter-names/2.15.3/jackson-module-parameter-names-2.15.3.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/module/jackson-module-parameter-names/2.15.3/jackson-module-parameter-names-2.15.3.jar (10 kB at 33 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-tomcat/3.2.0/spring-boot-starter-tomcat-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.15.3/jackson-datatype-jsr310-2.15.3.jar (123 kB at 390 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-core/10.1.16/tomcat-embed-core-10.1.16.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-tomcat/3.2.0/spring-boot-starter-tomcat-3.2.0.jar (4.8 kB at 12 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-el/10.1.16/tomcat-embed-el-10.1.16.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot/3.2.0/spring-boot-3.2.0.jar (1.6 MB at 2.6 MB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-websocket/10.1.16/tomcat-embed-websocket-10.1.16.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-el/10.1.16/tomcat-embed-el-10.1.16.jar (261 kB at 404 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-web/6.1.1/spring-web-6.1.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-websocket/10.1.16/tomcat-embed-websocket-10.1.16.jar (281 kB at 350 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-webmvc/6.1.1/spring-webmvc-6.1.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-autoconfigure/3.2.0/spring-boot-autoconfigure-3.2.0.jar (1.9 MB at 2.4 MB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-data-jpa/3.2.0/spring-boot-starter-data-jpa-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-data-jpa/3.2.0/spring-boot-starter-data-jpa-3.2.0.jar (4.8 kB at 5.6 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-aop/3.2.0/spring-boot-starter-aop-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-aop/3.2.0/spring-boot-starter-aop-3.2.0.jar (4.8 kB at 5.3 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/aspectj/aspectjweaver/1.9.20.1/aspectjweaver-1.9.20.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-webmvc/6.1.1/spring-webmvc-6.1.1.jar (1.0 MB at 876 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-jdbc/3.2.0/spring-boot-starter-jdbc-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.3/jackson-databind-2.15.3.jar (1.6 MB at 1.3 MB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/zaxxer/HikariCP/5.0.1/HikariCP-5.0.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-jdbc/3.2.0/spring-boot-starter-jdbc-3.2.0.jar (4.8 kB at 3.0 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-jdbc/6.1.1/spring-jdbc-6.1.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/aspectj/aspectjweaver/1.9.20.1/aspectjweaver-1.9.20.1.jar (2.1 MB at 1.2 MB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/hibernate/orm/hibernate-core/6.3.1.Final/hibernate-core-6.3.1.Final.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-jdbc/6.1.1/spring-jdbc-6.1.1.jar (456 kB at 237 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/jakarta/transaction/jakarta.transaction-api/2.0.1/jakarta.transaction-api-2.0.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-web/6.1.1/spring-web-6.1.1.jar (1.9 MB at 959 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/jboss/logging/jboss-logging/3.5.3.Final/jboss-logging-3.5.3.Final.jar
Downloaded from central: https://repo.maven.apache.org/maven2/jakarta/transaction/jakarta.transaction-api/2.0.1/jakarta.transaction-api-2.0.1.jar (29 kB at 14 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/hibernate/common/hibernate-commons-annotations/6.0.6.Final/hibernate-commons-annotations-6.0.6.Final.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/jboss/logging/jboss-logging/3.5.3.Final/jboss-logging-3.5.3.Final.jar (59 kB at 29 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/io/smallrye/jandex/3.1.2/jandex-3.1.2.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/hibernate/common/hibernate-commons-annotations/6.0.6.Final/hibernate-commons-annotations-6.0.6.Final.jar (68 kB at 33 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/classmate/1.6.0/classmate-1.6.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/classmate/1.6.0/classmate-1.6.0.jar (69 kB at 32 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/jaxb-runtime/4.0.4/jaxb-runtime-4.0.4.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/zaxxer/HikariCP/5.0.1/HikariCP-5.0.1.jar (162 kB at 74 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/jaxb-core/4.0.4/jaxb-core-4.0.4.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-core/10.1.16/tomcat-embed-core-10.1.16.jar (3.5 MB at 1.5 MB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/txw2/4.0.4/txw2-4.0.4.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/jaxb-core/4.0.4/jaxb-core-4.0.4.jar (139 kB at 57 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/sun/istack/istack-commons-runtime/4.1.2/istack-commons-runtime-4.1.2.jar
Downloaded from central: https://repo.maven.apache.org/maven2/io/smallrye/jandex/3.1.2/jandex-3.1.2.jar (327 kB at 135 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/jakarta/inject/jakarta.inject-api/2.0.1/jakarta.inject-api-2.0.1.jar     
Downloaded from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/txw2/4.0.4/txw2-4.0.4.jar (73 kB at 29 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/antlr/antlr4-runtime/4.10.1/antlr4-runtime-4.10.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/sun/istack/istack-commons-runtime/4.1.2/istack-commons-runtime-4.1.2.jar (26 kB at 10 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/data/spring-data-jpa/3.2.0/spring-data-jpa-3.2.0.jar 
Downloaded from central: https://repo.maven.apache.org/maven2/jakarta/inject/jakarta.inject-api/2.0.1/jakarta.inject-api-2.0.1.jar (11 kB at 4.2 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/data/spring-data-commons/3.2.0/spring-data-commons-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/glassfish/jaxb/jaxb-runtime/4.0.4/jaxb-runtime-4.0.4.jar (920 kB at 339 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-orm/6.1.1/spring-orm-6.1.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/antlr/antlr4-runtime/4.10.1/antlr4-runtime-4.10.1.jar (322 kB at 111 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-tx/6.1.1/spring-tx-6.1.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-tx/6.1.1/spring-tx-6.1.1.jar (284 kB at 88 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-aspects/6.1.1/spring-aspects-6.1.1.jar        
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-orm/6.1.1/spring-orm-6.1.1.jar (234 kB at 71 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-security/3.2.0/spring-boot-starter-security-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-aspects/6.1.1/spring-aspects-6.1.1.jar (50 kB at 15 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-config/6.2.0/spring-security-config-6.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-security/3.2.0/spring-boot-starter-security-3.2.0.jar (4.7 kB at 1.3 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-web/6.2.0/spring-security-web-6.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/data/spring-data-commons/3.2.0/spring-data-commons-3.2.0.jar (1.4 MB at 375 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-actuator/3.2.0/spring-boot-starter-actuator-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/data/spring-data-jpa/3.2.0/spring-data-jpa-3.2.0.jar (1.4 MB at 360 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-actuator-autoconfigure/3.2.0/spring-boot-actuator-autoconfigure-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-actuator/3.2.0/spring-boot-starter-actuator-3.2.0.jar (4.8 kB at 1.2 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-actuator/3.2.0/spring-boot-actuator-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-web/6.2.0/spring-security-web-6.2.0.jar (778 kB at 180 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/io/micrometer/micrometer-jakarta9/1.12.0/micrometer-jakarta9-1.12.0.jar  
Downloaded from central: https://repo.maven.apache.org/maven2/io/micrometer/micrometer-jakarta9/1.12.0/micrometer-jakarta9-1.12.0.jar (32 kB at 7.1 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/io/micrometer/micrometer-core/1.12.0/micrometer-core-1.12.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-actuator/3.2.0/spring-boot-actuator-3.2.0.jar (649 kB at 133 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/hdrhistogram/HdrHistogram/2.1.12/HdrHistogram-2.1.12.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-actuator-autoconfigure/3.2.0/spring-boot-actuator-autoconfigure-3.2.0.jar (748 kB at 153 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/latencyutils/LatencyUtils/2.0.3/LatencyUtils-2.0.3.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-config/6.2.0/spring-security-config-6.2.0.jar (1.8 MB at 369 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/postgresql/postgresql/42.7.1/postgresql-42.7.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/latencyutils/LatencyUtils/2.0.3/LatencyUtils-2.0.3.jar (30 kB at 6.0 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/checkerframework/checker-qual/3.41.0/checker-qual-3.41.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/checkerframework/checker-qual/3.41.0/checker-qual-3.41.0.jar (229 kB at 43 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/flywaydb/flyway-core/9.22.3/flyway-core-9.22.3.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/hdrhistogram/HdrHistogram/2.1.12/HdrHistogram-2.1.12.jar (174 kB at 32 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/dataformat/jackson-dataformat-toml/2.15.3/jackson-dataformat-toml-2.15.3.jar
Downloaded from central: https://repo.maven.apache.org/maven2/io/micrometer/micrometer-core/1.12.0/micrometer-core-1.12.0.jar (882 kB at 164 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-core/2.15.3/jackson-core-2.15.3.jar   
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/dataformat/jackson-dataformat-toml/2.15.3/jackson-dataformat-toml-2.15.3.jar (56 kB at 10 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar (283 kB at 49 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-oauth2-resource-server/3.2.0/spring-boot-starter-oauth2-resource-server-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-oauth2-resource-server/3.2.0/spring-boot-starter-oauth2-resource-server-3.2.0.jar (4.8 kB at 816 B/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-core/6.2.0/spring-security-core-6.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/postgresql/postgresql/42.7.1/postgresql-42.7.1.jar (1.1 MB at 184 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-crypto/6.2.0/spring-security-crypto-6.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-core/2.15.3/jackson-core-2.15.3.jar (549 kB at 93 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-resource-server/6.2.0/spring-security-oauth2-resource-server-6.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-crypto/6.2.0/spring-security-crypto-6.2.0.jar (84 kB at 14 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-core/6.2.0/spring-security-oauth2-core-6.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/hibernate/orm/hibernate-core/6.3.1.Final/hibernate-core-6.3.1.Final.jar (11 MB at 1.9 MB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-jose/6.2.0/spring-security-oauth2-jose-6.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-resource-server/6.2.0/spring-security-oauth2-resource-server-6.2.0.jar (99 kB at 16 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/nimbusds/nimbus-jose-jwt/9.24.4/nimbus-jose-jwt-9.24.4.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-jose/6.2.0/spring-security-oauth2-jose-6.2.0.jar (97 kB at 16 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/github/stephenc/jcip/jcip-annotations/1.0-1/jcip-annotations-1.0-1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-oauth2-core/6.2.0/spring-security-oauth2-core-6.2.0.jar (105 kB at 17 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-mail/3.2.0/spring-boot-starter-mail-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-mail/3.2.0/spring-boot-starter-mail-3.2.0.jar (4.8 kB at 772 B/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/spring-context-support/6.1.1/spring-context-support-6.1.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/github/stephenc/jcip/jcip-annotations/1.0-1/jcip-annotations-1.0-1.jar (4.7 kB at 764 B/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/jakarta.mail/2.0.2/jakarta.mail-2.0.2.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/security/spring-security-core/6.2.0/spring-security-core-6.2.0.jar (513 kB at 82 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/jakarta/activation/jakarta.activation-api/2.1.2/jakarta.activation-api-2.1.2.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/spring-context-support/6.1.1/spring-context-support-6.1.1.jar (174 kB at 28 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/angus-activation/2.0.1/angus-activation-2.0.1.jar      
Downloaded from central: https://repo.maven.apache.org/maven2/jakarta/activation/jakarta.activation-api/2.1.2/jakarta.activation-api-2.1.2.jar (66 kB at 10 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/mapstruct/mapstruct/1.5.5.Final/mapstruct-1.5.5.Final.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/angus-activation/2.0.1/angus-activation-2.0.1.jar (27 kB at 4.3 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-test/3.2.0/spring-boot-starter-test-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-test/3.2.0/spring-boot-starter-test-3.2.0.jar (4.8 kB at 750 B/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-test/3.2.0/spring-boot-test-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/mapstruct/mapstruct/1.5.5.Final/mapstruct-1.5.5.Final.jar (29 kB at 4.6 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-test-autoconfigure/3.2.0/spring-boot-test-autoconfigure-3.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-test-autoconfigure/3.2.0/spring-boot-test-autoconfigure-3.2.0.jar (219 kB at 34 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/jayway/jsonpath/json-path/2.8.0/json-path-2.8.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-test/3.2.0/spring-boot-test-3.2.0.jar (245 kB at 37 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/jakarta/xml/bind/jakarta.xml.bind-api/4.0.1/jakarta.xml.bind-api-4.0.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/jayway/jsonpath/json-path/2.8.0/json-path-2.8.0.jar (278 kB at 42 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/net/minidev/json-smart/2.5.0/json-smart-2.5.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/flywaydb/flyway-core/9.22.3/flyway-core-9.22.3.jar (885 kB at 134 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/net/minidev/accessors-smart/2.5.0/accessors-smart-2.5.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/nimbusds/nimbus-jose-jwt/9.24.4/nimbus-jose-jwt-9.24.4.jar (681 kB at 103 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/ow2/asm/asm/9.3/asm-9.3.jar
Downloaded from central: https://repo.maven.apache.org/maven2/net/minidev/json-smart/2.5.0/json-smart-2.5.0.jar (120 kB at 18 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/awaitility/awaitility/4.2.0/awaitility-4.2.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/net/minidev/accessors-smart/2.5.0/accessors-smart-2.5.0.jar (30 kB at 4.5 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest/2.2/hamcrest-2.2.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/eclipse/angus/jakarta.mail/2.0.2/jakarta.mail-2.0.2.jar (717 kB at 107 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/junit/jupiter/junit-jupiter/5.10.1/junit-jupiter-5.10.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/jakarta/xml/bind/jakarta.xml.bind-api/4.0.1/jakarta.xml.bind-api-4.0.1.jar (130 kB at 19 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/junit/jupiter/junit-jupiter-params/5.10.1/junit-jupiter-params-5.10.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/ow2/asm/asm/9.3/asm-9.3.jar (122 kB at 18 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/skyscreamer/jsonassert/1.5.1/jsonassert-1.5.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/junit/jupiter/junit-jupiter/5.10.1/junit-jupiter-5.10.1.jar (6.4 kB at 938 B/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/vaadin/external/google/android-json/0.0.20131108.vaadin1/android-json-0.0.20131108.vaadin1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/skyscreamer/jsonassert/1.5.1/jsonassert-1.5.1.jar (31 kB at 4.5 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/xmlunit/xmlunit-core/2.9.1/xmlunit-core-2.9.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/vaadin/external/google/android-json/0.0.20131108.vaadin1/android-json-0.0.20131108.vaadin1.jar (18 kB at 2.7 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/testcontainers/testcontainers/1.19.4/testcontainers-1.19.4.jar       
Downloaded from central: https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest/2.2/hamcrest-2.2.jar (123 kB at 18 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/awaitility/awaitility/4.2.0/awaitility-4.2.0.jar (96 kB at 14 kB/s)   
Downloading from central: https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest-core/2.2/hamcrest-core-2.2.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/junit/jupiter/junit-jupiter-params/5.10.1/junit-jupiter-params-5.10.1.jar (586 kB at 85 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/commons/commons-compress/1.25.0/commons-compress-1.25.0.jar   
Downloaded from central: https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest-core/2.2/hamcrest-core-2.2.jar (1.5 kB at 215 B/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/rnorth/duct-tape/duct-tape/1.0.8/duct-tape-1.0.8.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/rnorth/duct-tape/duct-tape/1.0.8/duct-tape-1.0.8.jar (25 kB at 3.6 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/jetbrains/annotations/17.0.0/annotations-17.0.0.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/xmlunit/xmlunit-core/2.9.1/xmlunit-core-2.9.1.jar (175 kB at 25 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-api/3.3.4/docker-java-api-3.3.4.jar   
Downloaded from central: https://repo.maven.apache.org/maven2/org/jetbrains/annotations/17.0.0/annotations-17.0.0.jar (19 kB at 2.7 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.15.3/jackson-annotations-2.15.3.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.15.3/jackson-annotations-2.15.3.jar (76 kB at 10 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-transport-zerodep/3.3.4/docker-java-transport-zerodep-3.3.4.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/commons/commons-compress/1.25.0/commons-compress-1.25.0.jar (1.1 MB at 149 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-transport/3.3.4/docker-java-transport-3.3.4.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-transport/3.3.4/docker-java-transport-3.3.4.jar (38 kB at 5.1 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/testcontainers/postgresql/1.19.4/postgresql-1.19.4.jar
Downloaded from central: https://repo.maven.apache.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar (385 kB at 52 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/testcontainers/jdbc/1.19.3/jdbc-1.19.3.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/testcontainers/jdbc/1.19.3/jdbc-1.19.3.jar (29 kB at 3.9 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/testcontainers/database-commons/1.19.3/database-commons-1.19.3.jar   
Downloaded from central: https://repo.maven.apache.org/maven2/org/testcontainers/postgresql/1.19.4/postgresql-1.19.4.jar (9.7 kB at 1.3 kB/s)
Downloaded from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-api/3.3.4/docker-java-api-3.3.4.jar (471 kB at 63 kB/s)
Downloaded from central: https://repo.maven.apache.org/maven2/org/testcontainers/database-commons/1.19.3/database-commons-1.19.3.jar (15 kB at 2.0 kB/s)
Downloaded from central: https://repo.maven.apache.org/maven2/com/github/docker-java/docker-java-transport-zerodep/3.3.4/docker-java-transport-zerodep-3.3.4.jar (2.0 MB at 251 kB/s)
Downloaded from central: https://repo.maven.apache.org/maven2/org/testcontainers/testcontainers/1.19.4/testcontainers-1.19.4.jar (18 MB at 1.7 MB/s)
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  22.076 s
[INFO] Finished at: 2026-03-09T22:14:26+01:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal on project server-app: Could not resolve dependencies for project com.urbanbloom:server-app:jar:0.0.1-SNAPSHOT
[ERROR] dependency: com.urbanbloom:urbanbloom-shared:jar:0.0.1-SNAPSHOT (compile)
[ERROR]         Could not find artifact com.urbanbloom:urbanbloom-shared:jar:0.0.1-SNAPSHOT
[ERROR] dependency: com.urbanbloom:urbanbloom-module-lending:jar:0.0.1-SNAPSHOT (compile)
[ERROR]         Could not find artifact com.urbanbloom:urbanbloom-module-lending:jar:0.0.1-SNAPSHOT
[ERROR] dependency: com.urbanbloom:urbanbloom-module-catalog:jar:0.0.1-SNAPSHOT (compile)
[ERROR]         Could not find artifact com.urbanbloom:urbanbloom-module-catalog:jar:0.0.1-SNAPSHOT
[ERROR] dependency: com.urbanbloom:urbanbloom-module-notification:jar:0.0.1-SNAPSHOT (compile)
[ERROR]         Could not find artifact com.urbanbloom:urbanbloom-module-notification:jar:0.0.1-SNAPSHOT
[ERROR] dependency: com.urbanbloom:urbanbloom-module-reminding:jar:0.0.1-SNAPSHOT (compile)
[ERROR]         Could not find artifact com.urbanbloom:urbanbloom-module-reminding:jar:0.0.1-SNAPSHOT
[ERROR] dependency: com.urbanbloom:urbanbloom-module-user:jar:0.0.1-SNAPSHOT (compile)
[ERROR]         Could not find artifact com.urbanbloom:urbanbloom-module-user:jar:0.0.1-SNAPSHOT
[ERROR]
[ERROR] -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/DependencyResolutionException

 *  The terminal process "C:\WINDOWS\System32\WindowsPowerShell\v1.0\powershell.exe -Command mvn -pl server-app spring-boot:run" terminated with exit code: 1. 
 *  Terminal will be reused by tasks, press any key to close it.