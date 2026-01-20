<#-- email-verification.ftl -->
<#import "template.ftl" as layout>
<@layout.emailLayout>
Hallo ${user.firstName} ${user.lastName},

Willkommen bei der Schulbibliothek! Um dein Konto zu aktivieren, klicke bitte auf den folgenden Link:

<@layout.button href="${link}"; type="primary">
    Konto aktivieren
</@layout.button>

Der Link ist 24 Stunden gültig.

Falls du dich nicht registriert hast, ignoriere diese E-Mail.

Mit freundlichen Grüßen,
Dein Schulbibliothek-Team
</@layout.emailLayout>