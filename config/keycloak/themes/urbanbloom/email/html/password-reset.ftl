<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Passwort zurücksetzen - Schulbibliothek</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f8f8f5;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #ffffff;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            text-align: center;
            border-radius: 5px 5px 0 0;
            margin: -20px -20px 20px -20px;
        }
        .header h1 {
            margin: 0;
            font-size: 24px;
        }
        .content {
            padding: 20px 0;
        }
        .button {
            display: inline-block;
            padding: 12px 30px;
            margin: 20px 0;
            background-color: #667eea;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
        }
        .button:hover {
            background-color: #764ba2;
        }
        .footer {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
            font-size: 12px;
            color: #666;
        }
        .warning {
            background-color: #fff3cd;
            border: 1px solid #ffc107;
            color: #856404;
            padding: 15px;
            border-radius: 5px;
            margin: 20px 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Schulbibliothek - Passwort zurücksetzen</h1>
        </div>
        
        <div class="content">
            <p>Hallo ${user.firstName} ${user.lastName},</p>
            
            <p>du hast angefordert, dein Passwort für die Schulbibliothek zurückzusetzen.</p>
            
            <p>Klicke bitte auf den folgenden Button, um ein neues Passwort festzulegen:</p>
            
            <center>
                <a href="${link}" class="button">Passwort zurücksetzen</a>
            </center>
            
            <p>Oder kopiere diesen Link in deinen Browser:</p>
            <p style="word-break: break-all; background-color: #f5f5f5; padding: 10px; border-radius: 3px;">
                ${link}
            </p>
            
            <div class="warning">
                <strong>Wichtig:</strong> Dieser Link ist nur begrenzt gültig. Falls du diese E-Mail nicht angefordert hast, ignoriere sie bitte.
            </div>
        </div>
        
        <div class="footer">
            <p>Mit freundlichen Grüßen,<br>
            <strong>Dein Schulbibliothek-Team</strong></p>
            <p style="margin-top: 15px; color: #999;">
                Dies ist eine automatisch generierte E-Mail. Bitte antworte nicht direkt auf diese Nachricht.
            </p>
        </div>
    </div>
</body>
</html>
