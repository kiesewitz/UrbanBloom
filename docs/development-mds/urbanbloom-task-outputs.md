# Tasks

## infra: up
Executing task: docker-compose up -d 

[+] Running 3/3
 ✔ Container urbanbloom-dev-mailpit-1   Running                                                                                    0.0s 
 ✔ Container urbanbloom-dev-postgres-1  Healthy                                                                                    0.5s 
 ✔ Container urbanbloom-dev-keycloak-1  Running                                                                                    0.0s 
 *  Terminal will be reused by tasks, press any key to close it. 

## mobile: run
Executing task: flutter run 

No supported devices connected.

The following devices were found, but are not supported by this project:
Windows (desktop) • windows • windows-x64    • Microsoft Windows [Version 10.0.26200.7840]
Chrome (web)      • chrome  • web-javascript • Google Chrome 145.0.7632.160
Edge (web)        • edge    • web-javascript • Microsoft Edge 145.0.3800.82
If you would like your app to run on web or windows, consider running `flutter create .` to generate projects for these platforms.

 *  The terminal process "C:\WINDOWS\System32\WindowsPowerShell\v1.0\powershell.exe -Command flutter run" terminated with exit code: 1. 
 *  Terminal will be reused by tasks, press any key to close it.

## web: run
Executing task: flutter run -d chrome 

Launching lib\main.dart on Chrome in debug mode...
Waiting for connection from debug service on Chrome...             14,7s

Flutter run key commands.
r Hot reload. 
R Hot restart.
h List all available interactive commands.
d Detach (terminate "flutter run" but leave application running).
c Clear the screen
q Quit (terminate the application on the device).

Debug service listening on ws://127.0.0.1:61580/VUE9VGpMoV8=/ws
A Dart VM Service on Chrome is available at: http://127.0.0.1:61580/VUE9VGpMoV8=
The Flutter DevTools debugger and profiler on Chrome is available at:
http://127.0.0.1:61580/VUE9VGpMoV8=/devtools/?uri=ws://127.0.0.1:61580/VUE9VGpMoV8=/ws
Starting application from main method in: org-dartlang-app:/web_entrypoint.dart.

## server: run
Executing task: mvn spring-boot:run 

[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
[INFO] 
[INFO] School Library Backend                                             [pom]
[INFO] School Library Shared Kernel                                       [jar]
[INFO] School Library - Lending Context (Core Domain)                     [jar]
[INFO] School Library - Catalog Context (Supporting Subdomain)            [jar]
[INFO] School Library - Notification Context (Supporting Subdomain)       [jar]
[INFO] School Library - Reminding Context (Supporting Subdomain)          [jar]
[INFO] School Library - User Context (Generic Subdomain)                  [jar]
[INFO] School Library Application                                         [jar]
[INFO] 
[INFO] -----------------< com.urbanbloom:urbanbloom-backend >------------------
[INFO] Building School Library Backend 0.0.1-SNAPSHOT                     [1/8]
[INFO]   from pom.xml
[INFO] --------------------------------[ pom ]---------------------------------
[INFO] 
[INFO] >>> spring-boot:3.2.0:run (default-cli) > test-compile @ urbanbloom-backend >>>
[INFO]
[INFO] <<< spring-boot:3.2.0:run (default-cli) < test-compile @ urbanbloom-backend <<<
[INFO]
[INFO] 
[INFO] --- spring-boot:3.2.0:run (default-cli) @ urbanbloom-backend ---
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for School Library Backend 0.0.1-SNAPSHOT:
[INFO]
[INFO] School Library Backend ............................. FAILURE [  1.004 s]
[INFO] School Library Shared Kernel ....................... SKIPPED
[INFO] School Library - Lending Context (Core Domain) ..... SKIPPED
[INFO] School Library - Catalog Context (Supporting Subdomain) SKIPPED
[INFO] School Library - Notification Context (Supporting Subdomain) SKIPPED
[INFO] School Library - Reminding Context (Supporting Subdomain) SKIPPED
[INFO] School Library - User Context (Generic Subdomain) .. SKIPPED
[INFO] School Library Application ......................... SKIPPED
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.673 s
[INFO] Finished at: 2026-03-09T22:08:08+01:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.springframework.boot:spring-boot-maven-plugin:3.2.0:run (default-cli) on project urbanbloom-backend: Unable to find a suitable main class, please add a 'mainClass' property -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoExecutionException

 *  The terminal process "C:\WINDOWS\System32\WindowsPowerShell\v1.0\powershell.exe -Command mvn spring-boot:run" terminated with exit code: 1. 
 *  Terminal will be reused by tasks, press any key to close it.