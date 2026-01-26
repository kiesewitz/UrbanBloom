<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Mail bestätigen - UrbanBloom</title>
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
            <div class="logo">🌱</div>
            <h1>UrbanBloom</h1>
        </div>

        <div class="content">
            <p class="greeting">Hallo ${user.firstName}!</p>

            <p class="message">
                Vielen Dank für deine Registrierung bei UrbanBloom! Wir freuen uns, dich in unserer Community begrüßen zu dürfen.
            </p>

            <p class="message">
                Um deine E-Mail-Adresse zu bestätigen und dein Konto zu aktivieren, klicke bitte auf den folgenden Button:
            </p>

            <div class="button-container">
                <a href="${link}" class="button">E-Mail bestätigen</a>
            </div>

            <div class="alternative-link">
                <p>Falls der Button nicht funktioniert, kopiere diesen Link in deinen Browser:</p>
                <a href="${link}">${link}</a>
            </div>

            <div class="info-box">
                <p><strong>⏱️ Wichtig:</strong> Dieser Link ist 24 Stunden gültig. Danach musst du eine neue Bestätigungsmail anfordern.</p>
            </div>

            <p class="message">
                Nach der Bestätigung kannst du dich anmelden und mit dem Dokumentieren deiner grünen Aktionen beginnen, Punkte sammeln und an spannenden Challenges teilnehmen!
            </p>

            <p class="message">
                Wenn du diese E-Mail nicht angefordert hast, kannst du sie einfach ignorieren.
            </p>

            <p class="message" style="margin-top: 30px;">
                Viel Spaß beim Grüner machen deiner Stadt! 🌿<br>
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
                Du erhältst diese E-Mail, weil du ein Konto bei UrbanBloom erstellt hast.<br>
                UrbanBloom UG (haftungsbeschränkt)<br>
                Musterstraße 123, 12345 Musterstadt, Deutschland
            </p>
        </div>
    </div>
</body>
</html>
