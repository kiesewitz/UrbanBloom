# Problem: VS Code zeigt JDK 21, Terminal aber JDK 17

## Problem
- Language Server/Debug nutzt das in `.vscode/settings.json` konfigurierte JDK 21.
- Integriertes Terminal erbt System-Variablen (PATH/JAVA_HOME) und fand zuerst JDK 17, daher `java -version` = 17.

## Lösung
- In `.vscode/settings.json` `terminal.integrated.env.windows` gesetzt mit `JAVA_HOME` und `PATH` auf das JDK 21.
- Terminal neu gestartet, dadurch nutzt PATH/JAVA_HOME im Terminal ebenfalls JDK 21.
- Verifikation mit `where java`, `$env:JAVA_HOME`, `java -version`, `mvn -version` im neuen Terminal.
- Optional global: System-`JAVA_HOME` und PATH-Reihenfolge auf JDK 21 setzen, falls auch außerhalb von VS Code erforderlich.
