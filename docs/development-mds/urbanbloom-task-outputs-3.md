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
Waiting for connection from debug service on Chrome...             40,1s

Flutter run key commands.
r Hot reload. 
R Hot restart.
h List all available interactive commands.
d Detach (terminate "flutter run" but leave application running).
c Clear the screen
q Quit (terminate the application on the device).

Debug service listening on ws://127.0.0.1:55455/sb1n2DxcTwo=/ws
A Dart VM Service on Chrome is available at: http://127.0.0.1:55455/sb1n2DxcTwo=
The Flutter DevTools debugger and profiler on Chrome is available at:
http://127.0.0.1:55455/sb1n2DxcTwo=/devtools/?uri=ws://127.0.0.1:55455/sb1n2DxcTwo=/ws
Starting application from main method in: org-dartlang-app:/web_entrypoint.dart. 

## web: run
Executing task: flutter run -d chrome 

Waiting for another flutter command to release the startup lock...
Launching lib\main.dart on Chrome in debug mode...
Waiting for connection from debug service on Chrome...             26,6s

Flutter run key commands.
r Hot reload. 
R Hot restart.
h List all available interactive commands.
d Detach (terminate "flutter run" but leave application running).
c Clear the screen
q Quit (terminate the application on the device).

Debug service listening on ws://127.0.0.1:52427/R3pUmnwSU_s=/ws
A Dart VM Service on Chrome is available at: http://127.0.0.1:52427/R3pUmnwSU_s=
The Flutter DevTools debugger and profiler on Chrome is available at:
http://127.0.0.1:52427/R3pUmnwSU_s=/devtools/?uri=ws://127.0.0.1:52427/R3pUmnwSU_s=/ws
Starting application from main method in: org-dartlang-app:/web_entrypoint.dart.
A message on the flutter/lifecycle channel was discarded before it could be handled.
This happens when a plugin sends messages to the framework side before the framework has had an opportunity to register a listener. See 
the ChannelBuffers API documentation for details on how to configure the channel to expect more messages, or to expect messages to get  
discarded:
  https://api.flutter.dev/flutter/dart-ui/ChannelBuffers-class.html
A message on the flutter/lifecycle channel was discarded before it could be handled.
This happens when a plugin sends messages to the framework side before the framework has had an opportunity to register a listener. See 
the ChannelBuffers API documentation for details on how to configure the channel to expect more messages, or to expect messages to get  
discarded:
  https://api.flutter.dev/flutter/dart-ui/ChannelBuffers-class.html

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
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.163 s
[INFO] Finished at: 2026-03-09T22:23:08+01:00
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