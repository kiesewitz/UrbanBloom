<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Passwort zurücksetzen - UrbanBloom</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            background-color: #f5f5f5;
        }
        .email-container {
            max-width: 600px;
            margin: 0 auto;
            background-color: #ffffff;
        }
        .header {
            background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
            padding: 40px 20px;
            text-align: center;
        }
        .logo {
            font-size: 48px;
            margin-bottom: 10px;
        }
        .header h1 {
            color: #ffffff;
            margin: 0;
            font-size: 28px;
        }
        .content {
            padding: 40px 30px;
        }
        .greeting {
            font-size: 20px;
            color: #333333;
            margin-bottom: 20px;
        }
        .message {
            font-size: 16px;
            line-height: 1.6;
            color: #555555;
            margin-bottom: 30px;
        }
        .button-container {
            text-align: center;
            margin: 40px 0;
        }
        .button {
            display: inline-block;
            padding: 16px 40px;
            background-color: #4CAF50;
            color: #ffffff !important;
            text-decoration: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
        }
        .button:hover {
            background-color: #45a049;
        }
        .code-box {
            background-color: #f5f5f5;
            border: 2px dashed #4CAF50;
            border-radius: 8px;
            padding: 20px;
            margin: 30px 0;
            text-align: center;
        }
        .code-box p {
            font-size: 14px;
            color: #666666;
            margin: 0 0 15px 0;
        }
        .code {
            font-size: 32px;
            font-weight: bold;
            color: #4CAF50;
            letter-spacing: 8px;
            font-family: 'Courier New', monospace;
        }
        .alternative-link {
            background-color: #f9f9f9;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            padding: 20px;
            margin: 30px 0;
        }
        .alternative-link p {
            font-size: 14px;
            color: #666666;
            margin: 0 0 10px 0;
        }
        .alternative-link a {
            color: #4CAF50;
            word-break: break-all;
            font-size: 13px;
        }
        .warning-box {
            background-color: #fff3e0;
            border-left: 4px solid #ff9800;
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
        }
        .warning-box p {
            margin: 0;
            font-size: 14px;
            color: #555555;
        }
        .info-box {
            background-color: #e3f2fd;
            border-left: 4px solid #2196F3;
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
        }
        .info-box p {
            margin: 0;
            font-size: 14px;
            color: #555555;
        }
        .security-tips {
            background-color: #f9f9f9;
            border-radius: 8px;
            padding: 20px;
            margin: 30px 0;
        }
        .security-tips h3 {
            color: #333333;
            font-size: 16px;
            margin-top: 0;
            margin-bottom: 15px;
        }
        .security-tips ul {
            margin: 0;
            padding-left: 20px;
        }
        .security-tips li {
            font-size: 14px;
            color: #555555;
            margin-bottom: 8px;
            line-height: 1.5;
        }
        .footer {
            background-color: #f9f9f9;
            padding: 30px;
            text-align: center;
            border-top: 1px solid #e0e0e0;
        }
        .footer p {
            font-size: 13px;
            color: #999999;
            margin: 5px 0;
        }
        .footer a {
            color: #4CAF50;
            text-decoration: none;
        }
        .social-links {
            margin: 20px 0;
        }
        .social-links a {
            display: inline-block;
            margin: 0 10px;
            font-size: 24px;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="email-container">
        <div class="header">
            <div class="logo">🔒</div>
            <h1>Passwort zurücksetzen</h1>
        </div>

        <div class="content">
            <p class="greeting">Hallo ${user.firstName}!</p>

            <p class="message">
                Wir haben eine Anfrage zum Zurücksetzen deines Passworts für dein UrbanBloom-Konto erhalten.
            </p>

            <div class="code-box">
                <p><strong>Dein Verifizierungscode:</strong></p>
                <div class="code">${code}</div>
                <p style="margin-top: 15px; font-size: 13px;">Gib diesen Code in der App ein</p>
            </div>

            <p class="message">
                Alternativ kannst du auch direkt auf den folgenden Button klicken, um ein neues Passwort zu setzen:
            </p>

            <div class="button-container">
                <a href="${link}" class="button">Passwort zurücksetzen</a>
            </div>

            <div class="alternative-link">
                <p>Falls der Button nicht funktioniert, kopiere diesen Link in deinen Browser:</p>
                <a href="${link}">${link}</a>
            </div>

            <div class="info-box">
                <p><strong>⏱️ Wichtig:</strong> Dieser Code und Link sind aus Sicherheitsgründen nur 15 Minuten gültig.</p>
            </div>

            <div class="warning-box">
                <p><strong>🔐 Sicherheitshinweis:</strong> Falls du diese E-Mail nicht angefordert hast, ignoriere sie bitte und kontaktiere umgehend unseren Support. Dein Passwort bleibt unverändert, solange du nicht auf den Link klickst oder den Code verwendest.</p>
            </div>

            <div class="security-tips">
                <h3>💡 Tipps für ein sicheres Passwort:</h3>
                <ul>
                    <li>Verwende mindestens 8 Zeichen</li>
                    <li>Kombiniere Groß- und Kleinbuchstaben</li>
                    <li>Füge Zahlen und Sonderzeichen hinzu</li>
                    <li>Vermeide persönliche Informationen</li>
                    <li>Nutze für jeden Dienst ein eigenes Passwort</li>
                    <li>Verwende einen Passwort-Manager</li>
                </ul>
            </div>

            <p class="message" style="margin-top: 30px;">
                Bei Fragen oder Problemen stehen wir dir gerne zur Verfügung.<br>
                Dein UrbanBloom Team
            </p>
        </div>

        <div class="footer">
            <div class="social-links">
                <a href="#">📘</a>
                <a href="#">📷</a>
                <a href="#">🐦</a>
            </div>

            <p>© ${.now?string("yyyy")} UrbanBloom. Alle Rechte vorbehalten.</p>

            <p>
                <a href="#">Datenschutz</a> |
                <a href="#">Nutzungsbedingungen</a> |
                <a href="#">Support</a>
            </p>

            <p style="margin-top: 15px;">
                Du erhältst diese E-Mail, weil jemand versucht hat, das Passwort für dein UrbanBloom-Konto zurückzusetzen.<br>
                Falls du dies nicht warst, ignoriere diese E-Mail.<br>
                UrbanBloom UG (haftungsbeschränkt)<br>
                Musterstraße 123, 12345 Musterstadt, Deutschland
            </p>
        </div>
    </div>
</body>
</html>
