# Lessons learned: JDK version in VS Code

- Unterschied verstehen: Language Server/Debug folgt `.vscode/settings.json`, integriertes Terminal folgt System-Env.
- Workspace-spezifische Lösung: `java.configuration.runtimes` und `java.jdt.ls.java.home` auf JDK 21 gesetzt.
- Terminal angleichen: in `.vscode/settings.json` `terminal.integrated.env.windows` mit `JAVA_HOME` und angepasstem `PATH` hinterlegt; neue Terminals übernehmen JDK 21.
- Falls global nötig: System-`JAVA_HOME` und PATH-Reihenfolge anpassen, danach VS Code neu starten.
- Verifikation: im neuen Terminal `where java`, `echo $env:JAVA_HOME`, `java -version`, `mvn -version` ausführen.
