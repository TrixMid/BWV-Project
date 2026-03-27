# Frontend-Analyse – BWV-Projekt

> Analysedatum: 26. März 2026  
> Betroffene Dateien: `levels.html`, `levels2.html`, `levelroom.html`, `levelroom.js`, `reviewroom.html`, `reviewroom.js`, `quiz.html`, `index.html`

---

## 1. Übersicht der Frontend-Dateien

| Datei | Technologie | Zweck |
|---|---|---|
| `templates/index.html` | HTML + Vanilla JS | Admin-Startseite (verstecktes Menü) |
| `templates/levels.html` | Phaser 3 (inline) | Plattformer-Levelauswahl (Wirtschaft & Sozialkunde) |
| `templates/levels2.html` | Phaser 3 (inline) | Klassenraum-Levelauswahl (11 Bücher) |
| `templates/levelroom.html` | HTML (Loader) | Lädt `/levelroom.js` als Phaser-Szene |
| `static/levelroom.js` | Phaser 3 | Quizraum – Fragen & Antworten (MC / TF / GAP) |
| `templates/reviewroom.html` | HTML (Loader) | Lädt `/reviewroom.js` als Phaser-Szene |
| `static/reviewroom.js` | Phaser 3 | Review-Raum nach Level-Abschluss + Medaillen-Zusammenfassung |
| `templates/quiz.html` | HTML + Fetch-API | Einfacher Quiz-Prototyp (veraltet, nicht aktiv verlinkt) |

---

## 2. Identifizierte Probleme

### 2.1 Spawn-Punkt in `levels2.html` (Issue #21)

**Problem:** Der Spieler wird bei `ROOM_HEIGHT - 150` gespawnt, also knapp über der unteren Wand (Welt-Grenze). Bei typischen Bildschirmhöhen von ~900 px liegt der Spawn bei y ≈ 1100 − 150 = **950 px**, was außerhalb des sichtbaren Bereichs liegt. Der Spieler ist beim Start nicht zu sehen.

```js
// Zeile 508 – levels2.html
player = this.physics.add.sprite(ROOM_WIDTH / 2, ROOM_HEIGHT - 150, "nerdPlayer");
//                                                 ^^^^^^^^^^^^^^^^^
// ROOM_HEIGHT = Math.max(1100, window.innerHeight + 160)
// z. B. 1100 - 150 = 950 → außerhalb des sichtbaren Viewports
```

**Empfehlung:** Spawn-Y auf einen festen Wert setzen, der sich innerhalb des sichtbaren Klassenzimmers befindet, z. B. `y = 500` (Bereich der Schülertische, Boden beginnt bei y ≈ 320).

---

### 2.2 Tafel und Lehrertisch zu weit rechts in `levelroom.js` (Issue #22)

**Problem:** Tafel (`board`), Lehrertisch (`teacherDesk`) und alle darauf gerenderten Texte verwenden den festen Offset `ROOM_WIDTH / 2 - 120`. Der Raum ist 2500 px breit; der tatsächliche Mittelpunkt liegt bei x = 1250. Der Offset verschiebt alle drei Elemente auf x = 1130, was **120 px links** von der Mitte liegt. Optisch wirkt die Tafel nicht zentriert.

```js
// Zeile 501 – levelroom.js (drawQuestionRoom)
scene.add.image(ROOM_WIDTH / 2 - 120, 190, "board")          // x = 1130 statt 1250
scene.add.image(ROOM_WIDTH / 2 - 120, 365, "teacherDesk")    // x = 1130

// Zeile 532, 438 – createBoardQuestion / updateBoardWithGapAnswers
const boardText = sceneRef.add.text(ROOM_WIDTH / 2 - 120, 190, ...)
```

**Empfehlung:** Offset `-120` aus allen drei Stellen entfernen, sodass Tafel, Tisch und Text exakt auf `ROOM_WIDTH / 2` (= 1250) positioniert sind.

---

### 2.3 Reviewraum: Scrollen nach unten nicht möglich (Issue #23)

**Problem:** `ROOM_HEIGHT` ist definiert als `window.innerHeight + 120`. Bei einem Full-HD-Monitor (1080 px) ergibt das 1200 px. Die unterste Reihe der Review-Tische liegt aber bei **y = 1050**. Der Spieler kann diesen Bereich bei kleineren Bildschirmen (< ~930 px) **nicht** erreichen, da die Welt-Grenze vor dieser y-Koordinate endet.

```js
// Zeile 6 – reviewroom.js
const ROOM_HEIGHT = window.innerHeight + 120;
// Review-Tische: y = 590, 820, 1050  (Zeile 271 ff.)
// → bei window.innerHeight = 900: ROOM_HEIGHT = 1020 → y=1050 nicht erreichbar!
```

**Empfehlung:** `ROOM_HEIGHT` so definieren, dass die unterste Tisch-Zeile bei y = 1050 + Puffer immer innerhalb der Weltgrenzen liegt, z. B.:

```js
const ROOM_HEIGHT = Math.max(1500, window.innerHeight + 500);
```

---

### 2.4 Medaillen-/Zusammenfassungsseite außerhalb der Tafel (Issue #24)

**Problem:** Der Zusammenfassungstext wird bei **y = 330** gerendert, obwohl die Tafel nur bis y ≈ 310 reicht (Mittelpunkt y = 190, Höhe 240 → unterer Rand ≈ 310). Der Titel steht bei y = 160, wäre also noch innerhalb, aber der lange Textkörper mit Zeilenabständen `\n\n` fällt weit unterhalb der Tafel heraus.

```js
// Zeile 183 – reviewroom.js (showSummaryScreen)
const summaryText = sceneRef.add.text(ROOM_WIDTH / 2, 330, text, ...)
// Tafelrahmen endet bei ca. y = 310 → Text erscheint UNTER der Tafel
```

Außerdem enthält der Text doppelte Zeilenumbrüche (`\n\n`), die bei begrenzter Tafelfläche zu Platzverschwendung führen.

**Empfehlung:**
- Titel auf y ≈ 108 (oberer Bereich der Tafel) platzieren
- Zusammenfassungstext auf y ≈ 213 (Mitte der Tafel) platzieren
- `\n\n` durch `\n` ersetzen und `fitTextToBox()` für automatische Schriftgrößenanpassung nutzen
- Medaillenbezeichnung auf Deutsch lokalisieren (GOLD / SILBER / BRONZE / KEINE)

---

### 2.5 Nur 11 Level in `levels2.html` (Issue #25)

**Problem:** Die `levelConfigs`-Liste in `createLevelDesk()` enthält nur 11 Einträge (Level 1–11). Issue #25 fordert weitere 5 Level (Level 12–16). Dazu fehlen:
- 5 neue Buch-Texturen in `createTextures()`
- 5 neue `levelConfigs`-Einträge
- Angepasste Tafel-Beschriftung (aktuell: `"11 Level zur Auswahl"`)
- Ggf. verbreiterte Kollisionsbox des Lehrertisches (aktuell: 1300 px)

```js
// Zeile 452–464 – levels2.html (createLevelDesk)
const levelConfigs = [
    { level: 1,  texture: "bookRed"       },
    // ...
    { level: 11, texture: "bookNavy"      }
    // → Level 12–16 fehlen
];
```

**Empfehlung:** 5 neue Texturen (z. B. Violet `0x884ea0`, Teal `0x117a65`, Brown `0x784212`, Gray `0x717d7e`, Gold `0xd4ac0d`) in `createTextures()` anlegen, entsprechende Einträge ergänzen, Tafeltext auf `"16 Level zur Auswahl"` aktualisieren, Kollisionsbreite auf 1400 px erhöhen.

---

### 2.6 Veralteter Quiz-Prototyp (`quiz.html`)

**Problem:** `quiz.html` ist ein einfacher HTML-Prototyp, der direkt `/api/levels/:level` und `/api/questions/:id` aufruft. Er ist **nicht in die Spiellogik integriert** (kein Session-Handling, keine Punktevergabe, keine Navigation zum Review-Raum). Er ist von keiner anderen Seite aus verlinkt und hat keinen praktischen Nutzen im aktuellen Stand.

**Empfehlung:** Datei entweder entfernen oder klar als veralteten Prototypen kennzeichnen (z. B. Hinweistext im `<title>` oder Kommentar im `<head>`).

---

### 2.7 Admin-Menü-Zugänglichkeit in `index.html`

**Problem:** Das Admin-Menü (Links zu `/themes`, `/teams`, `/questionsets`, `/questions`) ist mit `display: none` versteckt und nur per `Ctrl+P` sichtbar. Das ist eine fragile „Security-by-obscurity"-Lösung: Es gibt keine Authentifizierung.

```js
// index.html – Zeile 28
if (event.ctrlKey && event.key === 'p') {
    // Zeigt/versteckt Admin-Menü
}
```

**Empfehlung:** Admin-Bereich mit einer echten Authentifizierung schützen (z. B. Spring Security). Kurzfristig zumindest ein Passwortfeld einbauen.

---

### 2.8 Fehlende Spieler-Kollision mit Möbeln in `levels2.html`

**Problem:** In `createFurnitureCollisions()` sind Kollisionsrechtecke für Schülertische definiert, jedoch **nicht für den Lehrertisch-Bereich** unterhalb der Bücher (y < 320). Der Spieler kann den Tisch von unten durchlaufen.

```js
// Zeile 429–436 – levels2.html
function createFurnitureCollisions() {
    createCollisionRect(ROOM_WIDTH / 2, 320, 1300, 50); // Nur Tischplatte
    // → kein Kollisionsrechteck für den Bereich unterhalb des Tisches
}
```

**Empfehlung:** Kollisionsbereich des Lehrertisches um die Tischbeine (y ≈ 320–380) ergänzen, damit der Spieler nicht durch den Tisch hindurchwandern kann.

---

### 2.9 Doppeltes `handleMovement` in `levels2.html`

**Problem:** In `levels2.html` existiert die Funktion `handleMovement()` **zweimal**: einmal wird `currentInteractable = null` darin gesetzt, dann aber in `update()` nach `handleMovement()` erneut in `updateInteractionState()` überschrieben. Die erste Nullzuweisung in `handleMovement()` hat also keinen Effekt.

```js
// Zeile 559 – handleMovement
function handleMovement() {
    currentInteractable = null;   // ← wird sofort überschrieben
    // ...
}

// Zeile 593 – updateInteractionState
function updateInteractionState() {
    currentInteractable = null;   // ← dies ist der echte Reset
    // ...
}
```

**Empfehlung:** `currentInteractable = null` aus `handleMovement()` entfernen, da es in `updateInteractionState()` korrekt behandelt wird.

---

### 2.10 Fehlende Fehlerbehandlung bei GAP-Antwort-Übermittlung (`levelroom.js`)

**Problem:** In `saveGapAnswersFromOverlay()` wird geprüft, ob alle Selects ausgefüllt sind (`if (!selectedVal) { alert(...); return; }`). Allerdings wird `selectedOption` direkt ohne Null-Prüfung verwendet:

```js
// Zeile 399 – levelroom.js
const selectedOption = gaps[i].options.find(opt => opt.gapOptionId == selectedVal);
// ...
currentGapAnswers[existingIndex].userText = selectedOption.optionText;
// → selectedOption könnte undefined sein, wenn gapOptionId nicht matcht
```

**Empfehlung:** `selectedOption` auf `undefined` prüfen, bevor `.optionText` verwendet wird.

---

## 3. Zusammenfassung der Prioritäten

| # | Issue | Priorität | Datei |
|---|---|---|---|
| 2.1 | Spawn-Punkt unsichtbar | **Hoch** | `levels2.html` |
| 2.2 | Tafel/Tisch Offset | **Hoch** | `levelroom.js` |
| 2.3 | Reviewraum nicht scrollbar | **Hoch** | `reviewroom.js` |
| 2.4 | Medaillen-Text außerhalb Tafel | **Mittel** | `reviewroom.js` |
| 2.5 | Nur 11 Level | **Mittel** | `levels2.html` |
| 2.6 | Veralteter Quiz-Prototyp | **Niedrig** | `quiz.html` |
| 2.7 | Admin ohne Authentifizierung | **Niedrig** | `index.html` |
| 2.8 | Fehlende Tisch-Kollision | **Niedrig** | `levels2.html` |
| 2.9 | Doppeltes `handleMovement` | **Niedrig** | `levels2.html` |
| 2.10 | Fehlende Null-Prüfung GAP | **Niedrig** | `levelroom.js` |
