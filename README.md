# BWV-Projekt
## Ziel
Eine einfache Version eines Rollenspiels als Lernprogramm für die IHK-Abschlussprüfung zu erstellen.

## Im Backend
- Klassendiagramm zeichnen
- Implementierung in Java (Team Tobias & Melih)
Das spiel soll:
- Eine **Spielfigur** soll vom Spielergesteuert werden
- Das Spiel deckt die **zu lernenden Themenbereiche** der IHK Abschlussprüfung ab.
- Es soll "Belohnungen" für bestimmte **Güte-Stufen der Aufgabenbewältigung** geben.
- Spielstände sollen **gespeichert und geladen** werden können.
- Jedes Team erstellt **mindestens 40 Fragen, welche automatisch auswertbar sind**

## Datenbank
- Die relationale Datenbank für die Fragen wird durch das Team "Daten- und Prozessanalyse" gestaltet. 
### Welches Package benötigt wird für SQL
- DB steht in MySQL. XAMPP öffnen und dann starte MySQL/Apache server.
- DB erstellen unter der Name: quizdb
- DB MySQL & DBGUI verwendet werden und die Daten genau in quizdb gespeichert werden. (Khishig)
- Im Backend braucht ein Web-Framework. Hier vorschlaegt: Spring & Springboot. bitte euren Rechner installisieren: https://spring.io/quickstart
- Im Backend in Java Projekt - pom.xml konfigration machen. Schritte und properties folgt bald. 
- DB Fragen in Excel zusammenfassen und vor 13.03 auf Moodle hochladen (Team ?) 40 Fragen (Wirtschaft)

## Im Frontend
- Frontend soll in Javascript / HTML / CSS entwickelt werden (Team Khishig & Gleb)
(Es soll ein Fullstack-Projekt sein, deswegen können wir nicht JFrame anwenden, sonst müssen wir ein Web-Framework Springboot einsetzen, Backend und Frontend soll über REST API kommunizieren

## User story
- Ein Team soll User story schreiben (Team ?)

## Projektdoku und Backlogs
- Bitte regelmäßig Backlogs notieren, was ihr gemacht hat und machen will.,

### Wie man am Projekt beiträgt
- Erstelle einen neuen Branch `git branch <branch-name>` am Besten hier als develop 
- Vor dem Anfang immer Pull Request machen
- Auf feedback warten

## Beteiligte
- Tobias
- Melih
- Khishigbayar 
- Gleb

## Zeitraum
- bis 18.03
