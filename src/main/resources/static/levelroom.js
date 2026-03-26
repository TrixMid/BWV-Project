const params = new URLSearchParams(window.location.search);
const level = params.get("level") || "1";
const sessionId = params.get("sessionId");

const ROOM_WIDTH = 2500;
const ROOM_HEIGHT = Math.max(1100, window.innerHeight + 160);
const PLAYER_SPEED = 300;

const BASE_WIDTH = 1920;
const BASE_HEIGHT = 1080;
let uiScale = 1;

function getUiScale() {
    return Math.max(0.9, Math.min(1.15, Math.min(window.innerWidth / BASE_WIDTH, window.innerHeight / BASE_HEIGHT)));
}

let questions = [];
let currentQuestionIndex = 0;
let currentQuestionType = null;      // 'MC', 'TF', 'GAP'
let currentQuestionDetail = null;    // vollständige Frage-Daten (inkl. gaps)
let currentGapAnswers = [];          // [{ gapId, selectedOptionId, userText }]

let sceneRef;
let player;
let tables = [];
let computerDesk = null;             // Referenz auf den Computer-Tisch
let nextDoor = null;
let nextDoorLabel = null;
let doorTriggered = false;
let gapOverlayVisible = false;       // Status des Overlays

let cursors;
let keyA, keyD, keyW, keyS;

const config = {
    type: Phaser.CANVAS,
    parent: document.body,
    width: window.innerWidth,
    height: window.innerHeight,
    backgroundColor: "#dfe8f5",
    physics: {
        default: "arcade",
        arcade: {
            gravity: { y: 0 },
            debug: false
        }
    },
    scale: {
        mode: Phaser.Scale.RESIZE,
        autoCenter: Phaser.Scale.CENTER_BOTH
    },
    scene: {
        preload,
        create,
        update
    }
};

window.addEventListener("load", () => {
    new Phaser.Game(config);
});

function preload() {
    createTextures.call(this);
}

function create() {
    uiScale = getUiScale();
    sceneRef = this;

    this.physics.world.setBounds(0, 0, ROOM_WIDTH, ROOM_HEIGHT);
    this.cameras.main.setBounds(0, 0, ROOM_WIDTH, ROOM_HEIGHT);
    this.cameras.main.setBackgroundColor("#dfe8f5");

    cursors = this.input.keyboard.createCursorKeys();
    keyA = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.A);
    keyD = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.D);
    keyW = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.W);
    keyS = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.S);

    drawQuestionRoom.call(this);
    createPlayer.call(this);

    this.cameras.main.startFollow(player, true, 0.08, 0.08);
    const zoom = Math.max(0.72, Math.min(0.95, Math.min(window.innerWidth / 1600, window.innerHeight / 900)));
    this.cameras.main.setZoom(zoom);

    this.scale.on("resize", onResize, this);

    loadLevelQuestions();
}

function update() {
    if (!player) return;

    handleMovement();
    if (currentQuestionType === 'GAP') {
        handleGapInteraction();
    } else {
        handleTableSelection();
        resetTableTouchFlags();
    }
    handleDoorOverlap();
}

/* =========================
   API
========================= */

function loadLevelQuestions() {
    fetch("/api/levels/" + level)
        .then(res => res.json())
        .then(data => {
            questions = Array.isArray(data) ? data : [];

            if (questions.length === 0) {
                showBoardMessage("Keine Fragen für dieses Level gefunden.");
                return;
            }

            if (sessionId) {
                fetch("/quizgame/session/" + sessionId + "/progress")
                    .then(res => res.json())
                    .then(progress => {
                        currentQuestionIndex = progress.currentQuestionIndex || 0;

                        if (currentQuestionIndex >= questions.length) {
                            window.location.href = "/levels2";
                            return;
                        }

                        loadQuestion();

                    })
                    .catch(() => {
                        currentQuestionIndex = 0;
                        loadQuestion();
                    });
            } else {
                currentQuestionIndex = 0;
                loadQuestion();
            }
        })
        .catch(() => {
            showBoardMessage("Fehler beim Laden des Levels.");
        });
}

function loadQuestion() {
    const question = questions[currentQuestionIndex];
    if (!question) {
        showBoardMessage("Keine weitere Frage vorhanden.");
        return;
    }

    const id = question.questionId ?? question.id;

    fetch("/api/questions/" + id)
        .then(res => res.json())
        .then(detail => {
            currentQuestionType = detail.questionType; // 'MC', 'TF', 'GAP'
            currentQuestionDetail = detail;
            buildQuestion(detail);
        })
        .catch(() => {
            showBoardMessage("Fehler beim Laden der Frage.");
        });
}

async function goToNextQuestion() {
    if (doorTriggered) return;

    doorTriggered = true;

    if (nextDoor) {
        nextDoor.setTexture("doorOpen");
    }

    const saved = await saveCurrentAnswer();

    if (!saved) {
        doorTriggered = false;
        if (nextDoor) {
            nextDoor.setTexture("doorClosed");
        }
        showBoardMessage("Fehler beim Speichern der Antwort.");
        return;
    }

    sceneRef.time.delayedCall(250, () => {
        currentQuestionIndex++;

        if (currentQuestionIndex >= questions.length) {
            window.location.href = "/reviewroom?sessionId=" + sessionId + "&level=" + level;
        } else {
            loadQuestion();
        }
    });
}

/* =========================
   Room Build
========================= */

function buildQuestion(question) {
    clearDynamicObjects();

    drawQuestionRoom.call(sceneRef);
    createPlayer.call(sceneRef);
    sceneRef.cameras.main.startFollow(player, true, 0.08, 0.08);

    createBoardQuestion(question);

    if (currentQuestionType === 'GAP') {
        createComputerDesk();
        tables = []; // keine Antworttische
    } else {
        createAnswerTables(question.answers || []);
    }
    createNextDoor();
}

function createComputerDesk() {
    const pos = { x: ROOM_WIDTH / 2, y: 590 };
    computerDesk = sceneRef.add.image(pos.x, pos.y, "computerDesk").setDepth(2);
    sceneRef.physics.add.existing(computerDesk, true);

    const hint = sceneRef.add.text(pos.x, pos.y - 70, "Drücke E", {
        fontFamily: "Arial, sans-serif",
        fontSize: `${Math.round(24 * uiScale)}px`,
        color: "#ffffff",
        stroke: "#000000",
        strokeThickness: 3
    }).setOrigin(0.5).setDepth(3);
    computerDesk.hintText = hint;
}

function handleGapInteraction() {
    if (!computerDesk) return;
    const dx = Math.abs(player.x - computerDesk.x);
    const dy = Math.abs(player.y - computerDesk.y);
    const isNear = dx < 210 * uiScale && dy < 80 * uiScale;

    if (isNear && Phaser.Input.Keyboard.JustDown(sceneRef.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.E))) {
        showGapOverlay();
    }
}

function createGapOverlay() {
    if (document.getElementById("gapOverlay")) return;
    const overlay = document.createElement("div");
    overlay.id = "gapOverlay";
    overlay.style.cssText = `
        position: fixed;
        bottom: 20px;
        left: 20px;
        right: 20px;
        background: rgba(0,0,0,0.85);
        padding: 20px;
        border-radius: 15px;
        color: white;
        font-family: Arial, sans-serif;
        z-index: 10000;
        display: none;
        backdrop-filter: blur(5px);
        max-height: 40%;
        overflow-y: auto;
    `;
    const container = document.createElement("div");
    container.id = "gapFieldsContainer";
    const button = document.createElement("button");
    button.id = "submitGap";
    button.textContent = "Antworten speichern";
    button.style.cssText = "margin-top:15px; padding:10px 20px; font-size:18px; background:#4CAF50; color:white; border:none; border-radius:5px; cursor:pointer;";
    overlay.appendChild(container);
    overlay.appendChild(button);
    document.body.appendChild(overlay);

    button.addEventListener("click", () => {
        saveGapAnswersFromOverlay();
    });
}

function populateGapOverlay(question) {
    const gaps = question.gaps || [];
    const container = document.getElementById("gapFieldsContainer");
    if (!container) return;
    container.innerHTML = "";

    gaps.forEach(field => {
        const fieldDiv = document.createElement("div");
        fieldDiv.className = "gap-field";
        fieldDiv.style.marginBottom = "20px";
        fieldDiv.style.padding = "10px";
        fieldDiv.style.borderBottom = "1px solid #ccc";

        // Text vor der Lücke
        if (field.textBefore) {
            const beforeSpan = document.createElement("span");
            beforeSpan.textContent = field.textBefore;
            fieldDiv.appendChild(beforeSpan);
        }

        // Dropdown
        const select = document.createElement("select");
        select.setAttribute("data-gap-id", field.id);
        select.style.margin = "0 10px";
        select.style.padding = "5px";
        select.style.fontSize = "16px";

        const placeholder = document.createElement("option");
        placeholder.textContent = "Bitte auswählen";
        placeholder.value = "";
        select.appendChild(placeholder);

        (field.options || []).forEach(opt => {
            const option = document.createElement("option");
            option.value = opt.gapOptionId;
            option.textContent = opt.optionText;
            select.appendChild(option);
        });

        // Vorauswahl, falls bereits Antwort gespeichert
        const existing = currentGapAnswers.find(a => a.gapId === field.id);
        if (existing) {
            select.value = existing.selectedOptionId;
        }

        select.addEventListener("change", (e) => {
            const selectedId = e.target.value ? parseInt(e.target.value) : null;
            const idx = currentGapAnswers.findIndex(a => a.gapId === field.id);
            if (selectedId) {
                const selectedOption = field.options.find(opt => opt.gapOptionId === selectedId);
                if (idx >= 0) {
                    currentGapAnswers[idx].selectedOptionId = selectedId;
                    currentGapAnswers[idx].userText = selectedOption ? selectedOption.optionText : "";
                } else {
                    currentGapAnswers.push({
                        gapId: field.id,
                        selectedOptionId: selectedId,
                        userText: selectedOption ? selectedOption.optionText : ""
                    });
                }
            } else {
                if (idx >= 0) currentGapAnswers.splice(idx, 1);
            }
        });

        fieldDiv.appendChild(select);

        // Text nach der Lücke
        if (field.textAfter) {
            const afterSpan = document.createElement("span");
            afterSpan.textContent = field.textAfter;
            fieldDiv.appendChild(afterSpan);
        }

        container.appendChild(fieldDiv);
    });
}

function showGapOverlay() {
    if (gapOverlayVisible) return;
    createGapOverlay();               // stellt sicher, dass Overlay existiert
    populateGapOverlay(currentQuestionDetail);
    document.getElementById("gapOverlay").style.display = "block";
    gapOverlayVisible = true;
}

function buildGapPlaceholderText(question) {
    let fullText = question.startText || "";  // beginnt mit start_text

    const gaps = question.gaps || [];
    for (let i = 0; i < gaps.length; i++) {
        const gap = gaps[i];
        fullText += "___" + (gap.textAfter || "");
    }

    if (question.endText) {
        fullText += question.endText;
    }

    return fullText;
}

function saveGapAnswersFromOverlay() {
    const selects = document.querySelectorAll("#gapFieldsContainer .gap-field select");
    const gaps = currentQuestionDetail.gaps || [];
    if (selects.length !== gaps.length) return;

    for (let i = 0; i < selects.length; i++) {
        const selectedVal = selects[i].value;
        if (!selectedVal) {
            alert("Bitte fülle alle Lücken aus.");
            return;
        }
        const gapId = gaps[i].id;
        const existingIndex = currentGapAnswers.findIndex(a => a.gapId === gapId);
        const selectedOption = gaps[i].options.find(opt => opt.gapOptionId == selectedVal);
        if (existingIndex >= 0) {
            currentGapAnswers[existingIndex].selectedOptionId = parseInt(selectedVal);
            currentGapAnswers[existingIndex].userText = selectedOption.optionText;
        } else {
            currentGapAnswers.push({
                gapId: gapId,
                selectedOptionId: parseInt(selectedVal),
                userText: selectedOption.optionText
            });
        }
    }

    updateBoardWithGapAnswers();
    document.getElementById("gapOverlay").style.display = "none";
    gapOverlayVisible = false;

    if (computerDesk) {
        computerDesk.setTexture("computerDesk");
    }
}

function updateBoardWithGapAnswers() {
    if (!currentQuestionDetail) return;
    const gaps = currentQuestionDetail.gaps || [];
    let fullText = "";

    for (let i = 0; i < gaps.length; i++) {
        const gap = gaps[i];
        const answer = currentGapAnswers.find(a => a.gapId === gap.id);
        const userText = answer ? answer.userText : "___";
        fullText += (gap.textBefore || "") + userText + (gap.textAfter || "");
    }

    // Alten Tafeltext entfernen (falls vorhanden)
    const oldBoard = sceneRef.children.getByName("boardText");
    if (oldBoard) oldBoard.destroy();

    const newText = sceneRef.add.text(
        ROOM_WIDTH / 2,
        190,
        fullText,
        {
            fontFamily: "Arial Black, Arial, sans-serif",
            fontSize: `${Math.round(32 * uiScale)}px`,
            color: "#f5f5f5",
            wordWrap: { width: 760 * uiScale },
            align: "center",
            lineSpacing: 8
        }
    ).setOrigin(0.5).setDepth(10).setName("boardText");

    fitTextToBox(newText, 760 * uiScale, 170 * uiScale, 32 * uiScale, 18);
}

function clearDynamicObjects() {
    if (!sceneRef) return;

    sceneRef.children.removeAll();

    if (sceneRef.physics && sceneRef.physics.world) {
        sceneRef.physics.world.colliders.destroy();
    }

    tables = [];
    player = null;
    nextDoor = null;
    nextDoorLabel = null;
    doorTriggered = false;
    computerDesk = null;
    currentGapAnswers = [];
    gapOverlayVisible = false;
    const overlay = document.getElementById("gapOverlay");
    if (overlay) overlay.style.display = "none";
}

function drawQuestionRoom() {
    const scene = sceneRef || this;

    /* Wand */
    scene.add.rectangle(ROOM_WIDTH / 2, 170, ROOM_WIDTH, 340, 0xf4efe6).setDepth(-20);

    /* Boden */
    scene.add.rectangle(ROOM_WIDTH / 2, ROOM_HEIGHT - 210, ROOM_WIDTH, 520, 0xc7925b).setDepth(-20);

    /* Bodenmuster */
    for (let y = 360; y < ROOM_HEIGHT; y += 64) {
        for (let x = 0; x < ROOM_WIDTH; x += 64) {
            scene.add.image(x + 32, y + 32, "floor").setDepth(-19);
        }
    }

    /* Deckenleiste */
    scene.add.rectangle(ROOM_WIDTH / 2, 26, ROOM_WIDTH, 52, 0xe7dfd1).setDepth(-18);

    /* Fenster */
    const windowY = 160;
    [180, 340, 1660].forEach(x => {
        scene.add.image(x, windowY, "window").setDepth(-10).setScale(uiScale);
    });

    /* Tafel zentriert */
    scene.add.image(ROOM_WIDTH / 2, 190, "board").setDepth(-9).setScale(uiScale);

    /* Uhr */
    const clockX = 1510;
    const clockY = 45;
    scene.add.circle(clockX, clockY, 30, 0xffffff).setDepth(-8);
    scene.add.circle(clockX, clockY, 30).setStrokeStyle(3, 0x444444).setDepth(-7);
    scene.add.line(0, 0, clockX, clockY, clockX, clockY - 18, 0x444444).setLineWidth(3).setDepth(-6);
    scene.add.line(0, 0, clockX, clockY, clockX + 12, clockY, 0x444444).setLineWidth(2).setDepth(-6);

    /* Lehrertisch */
    scene.add.image(ROOM_WIDTH / 2, 365, "teacherDesk").setDepth(1).setScale(uiScale);

    scene.add.text(20, 56, `Level ${level} - Frage ${currentQuestionIndex + 1}${questions.length ? " / " + questions.length : ""}`, {
        fontFamily: "Arial, sans-serif",
        fontSize: `${Math.round(19 * uiScale)}px`,
        color: "#ffffff",
        stroke: "#000000",
        strokeThickness: 4
    }).setOrigin(0, 0).setScrollFactor(0).setDepth(100);
}

function createBoardQuestion(question) {
    let text = "";
    if (currentQuestionType === 'GAP') {
        text = buildGapPlaceholderText(question);
    } else {
        text = question?.startText || "Keine Frage vorhanden.";
    }

    const boardText = sceneRef.add.text(
        ROOM_WIDTH / 2,
        190,
        text,
        {
            fontFamily: "Arial Black, Arial, sans-serif",
            fontSize: `${Math.round(32 * uiScale)}px`,
            color: "#f5f5f5",
            wordWrap: { width: 760 * uiScale },
            align: "center",
            lineSpacing: 8
        }
    ).setOrigin(0.5).setDepth(10).setName("boardText");

    fitTextToBox(boardText, 760 * uiScale, 170 * uiScale, 32 * uiScale, 18);
}

function createAnswerTables(answers) {
    const positions = [
        { x: 520, y: 590 },
        { x: 1480, y: 590 },
        { x: 520, y: 820 },
        { x: 1480, y: 820 },
        { x: 520, y: 1050 },
        { x: 1480, y: 1050 }
    ];

    answers.forEach((answer, index) => {
        const pos = positions[index] || {
            x: 520 + (index % 2) * 960,
            y: 590 + Math.floor(index / 2) * 230
        };

        const desk = sceneRef.add.image(pos.x, pos.y, "answerDesk").setDepth(2).setScale(uiScale);
        sceneRef.physics.add.existing(desk, true);

        desk.answerId = answer.answerId ?? answer.id;
        desk.isSelected = false;
        desk.playerInside = false;
        desk.setTexture("answerDesk");

        tables.push(desk);

        const answerText = sceneRef.add.text(
            pos.x,
            pos.y - 6,
            answer.optionText || "",
            {
                fontFamily: "Arial, sans-serif",
                fontSize: `${Math.round(24 * uiScale)}px`,
                color: "#ffffff",
                wordWrap: { width: 420 * uiScale },
                align: "center"
            }
        ).setOrigin(0.5).setDepth(3);

        fitTextToBox(answerText, 420 * uiScale, 78 * uiScale, 24 * uiScale, 16);
    });
}

function handleTableSelection() {
    if (!player) return;

    tables.forEach(table => {
        const dx = Math.abs(player.x - table.x);
        const dy = Math.abs(player.y - table.y);

        const isNearTable = dx < 210 * uiScale && dy < 80 * uiScale;

        if (isNearTable && !table.playerInside) {
            table.playerInside = true;
            table.isSelected = !table.isSelected;

            if (table.isSelected) {
                table.setTexture("answerDeskSelected");
            } else {
                table.setTexture("answerDesk");
            }
        }
    });
}

function createNextDoor() {
    const doorX = 1860;
    const doorY = 230;

    nextDoor = sceneRef.add.image(doorX, doorY, "doorClosed").setDepth(2).setScale(uiScale);
    sceneRef.physics.add.existing(nextDoor, true);

    nextDoorLabel = sceneRef.add.text(doorX, doorY - 130, "NEXT", {
        fontFamily: "Arial Black, Arial, sans-serif",
        fontSize: `${Math.round(24 * uiScale)}px`,
        color: "#ffffff",
        stroke: "#111111",
        strokeThickness: 5
    }).setOrigin(0.5).setDepth(3);
}

function handleDoorOverlap() {
    if (!nextDoor || !player || doorTriggered) return;

    const isOverlapping = sceneRef.physics.overlap(player, nextDoor);

    if (isOverlapping) {
        goToNextQuestion();
    } else {
        nextDoor.setTexture("doorClosed");
    }
}

function fitTextToBox(textObject, maxWidth, maxHeight, startSize, minSize) {
    let size = startSize;
    textObject.setFontSize(size);
    textObject.setWordWrapWidth(maxWidth);

    while ((textObject.width > maxWidth || textObject.height > maxHeight) && size > minSize) {
        size -= 1;
        textObject.setFontSize(size);
        textObject.setWordWrapWidth(maxWidth);
    }
}

function showBoardMessage(message) {
    clearDynamicObjects();
    drawQuestionRoom.call(sceneRef);
    createPlayer.call(sceneRef);

    const msg = sceneRef.add.text(
        ROOM_WIDTH / 2,
        190,
        message,
        {
            fontFamily: "Arial Black, Arial, sans-serif",
            fontSize: `${Math.round(32 * uiScale)}px`,
            color: "#f5f5f5",
            wordWrap: { width: 760 * uiScale },
            align: "center"
        }
    ).setOrigin(0.5).setDepth(10);

    fitTextToBox(msg, 760 * uiScale, 170 * uiScale, 32 * uiScale, 18);
}

/* =========================
   Answer Logic
========================= */

function resetTableTouchFlags() {
    if (!player) return;

    tables.forEach(table => {
        const dx = Math.abs(player.x - table.x);
        const dy = Math.abs(player.y - table.y);

        const isNearTable = dx < 220 * uiScale && dy < 90 * uiScale;

        if (!isNearTable) {
            table.playerInside = false;
        }
    });
}

function getSelectedAnswerIds() {
    return tables
        .filter(table => table.isSelected)
        .map(table => table.answerId);
}

async function saveCurrentMcAnswer() {
    if (!sessionId) return true;

    const question = questions[currentQuestionIndex];
    if (!question) return false;

    const questionId = question.questionId ?? question.id;
    const selectedAnswerIds = getSelectedAnswerIds();

    if (selectedAnswerIds.length === 0) {
        alert("Bitte wähle eine Antwort aus.");
        return false;
    }

    try {
        const response = await fetch(`/quizgame/session/${sessionId}/answer/mc`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                questionId: questionId,
                selectedAnswerIds: selectedAnswerIds
            })
        });
        return response.ok;
    } catch (error) {
        console.error("Fehler beim Speichern der MC-Antwort:", error);
        return false;
    }
}

async function saveCurrentGapAnswer() {
    if (!sessionId) return true;

    const question = questions[currentQuestionIndex];
    if (!question) return false;

    const questionId = question.questionId ?? question.id;

    // Prüfen, ob alle Lücken ausgefüllt sind
    const gaps = currentQuestionDetail.gaps || [];
    if (currentGapAnswers.length !== gaps.length) {
        alert("Bitte fülle alle Lücken aus (über den Computer).");
        return false;
    }

    // Sicherstellen, dass jede Lücke eine gültige Option ID hat
    for (let answer of currentGapAnswers) {
        if (answer.selectedOptionId === null) {
            alert("Eine oder mehrere Lücken enthalten ungültige Eingaben. Bitte korrigiere sie.");
            return false;
        }
    }

    // Array im erwarteten Format für Backend erstellen
    const gapAnswerPayload = currentGapAnswers.map(answer => ({
        questionId: questionId,
        gapFieldId: answer.gapId,
        selectedOptionId: answer.selectedOptionId
    }));

    try {
        const response = await fetch(`/quizgame/session/${sessionId}/answer/gap`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(gapAnswerPayload)
        });
        return response.ok;
    } catch (error) {
        console.error("Fehler beim Speichern der GAP-Antwort:", error);
        return false;
    }
}

async function saveCurrentAnswer() {
    if (currentQuestionType === 'GAP') {
        return await saveCurrentGapAnswer();
    } else {
        return await saveCurrentMcAnswer();
    }
}

/* =========================
   Player
========================= */

function createPlayer() {
    player = sceneRef.physics.add.sprite(ROOM_WIDTH / 2, ROOM_HEIGHT - 700, "nerdPlayer");
    player.setScale(uiScale);
    player.setCollideWorldBounds(true);
    player.setDepth(5);
    player.body.setSize(34 / uiScale, 62 / uiScale, true);
    player.body.setOffset(11 / uiScale, 20 / uiScale);
}

function handleMovement() {
    const left = cursors.left.isDown || keyA.isDown;
    const right = cursors.right.isDown || keyD.isDown;
    const up = cursors.up.isDown || keyW.isDown;
    const down = cursors.down.isDown || keyS.isDown;

    let vx = 0;
    let vy = 0;

    if (left && !right) vx = -PLAYER_SPEED;
    else if (right && !left) vx = PLAYER_SPEED;

    if (up && !down) vy = -PLAYER_SPEED;
    else if (down && !up) vy = PLAYER_SPEED;

    player.setVelocity(vx, vy);

    if (vx < 0) player.setFlipX(true);
    else if (vx > 0) player.setFlipX(false);
}

/* =========================
   Resize
========================= */

function onResize(gameSize) {
    uiScale = getUiScale();
    this.cameras.resize(gameSize.width, gameSize.height);

    const zoom = Math.max(0.72, Math.min(0.95, Math.min(window.innerWidth / 1600, window.innerHeight / 900)));
    this.cameras.main.setZoom(zoom);
}

/* =========================
   Textures
========================= */

function createTextures() {
    const g = this.make.graphics({ x: 0, y: 0, add: false });

    /* Boden */
    g.fillStyle(0xc7925b, 1);
    g.fillRect(0, 0, 64, 64);
    g.lineStyle(2, 0xb17f4f, 1);
    g.lineBetween(0, 16, 64, 16);
    g.lineBetween(0, 32, 64, 32);
    g.lineBetween(0, 48, 64, 48);
    g.generateTexture("floor", 64, 64);
    g.clear();

    /* Fenster */
    g.fillStyle(0xffffff, 1);
    g.fillRoundedRect(0, 0, 120, 140, 8);
    g.fillStyle(0xa8d8ff, 1);
    g.fillRoundedRect(8, 8, 104, 124, 6);
    g.lineStyle(4, 0xffffff, 1);
    g.lineBetween(60, 8, 60, 132);
    g.lineBetween(8, 70, 112, 70);
    g.generateTexture("window", 120, 140);
    g.clear();

    /* Tafel */
    g.fillStyle(0x6b4423, 1);
    g.fillRoundedRect(0, 0, 900, 240, 10);
    g.fillStyle(0x2d5b37, 1);
    g.fillRoundedRect(18, 18, 864, 204, 6);
    g.generateTexture("board", 900, 240);
    g.clear();

    /* Lehrertisch */
    g.fillStyle(0x8b5a2b, 1);
    g.fillRoundedRect(0, 0, 320, 100, 8);
    g.fillStyle(0x6b3f1f, 1);
    g.fillRect(18, 78, 16, 42);
    g.fillRect(286, 78, 16, 42);
    g.generateTexture("teacherDesk", 320, 120);
    g.clear();

    /* Antworttisch */
    g.fillStyle(0x9a6232, 1);
    g.fillRoundedRect(0, 0, 520, 110, 8);
    g.fillStyle(0x73451f, 1);
    g.fillRect(18, 88, 14, 36);
    g.fillRect(488, 88, 14, 36);
    g.generateTexture("answerDesk", 520, 124);
    g.clear();

    /* Antworttisch ausgewählt */
    g.fillStyle(0x3fae4a, 1);
    g.fillRoundedRect(0, 0, 520, 110, 8);
    g.fillStyle(0x2f7d36, 1);
    g.fillRect(18, 88, 14, 36);
    g.fillRect(488, 88, 14, 36);
    g.generateTexture("answerDeskSelected", 520, 124);
    g.clear();

    /* Computer-Tisch */
    g.fillStyle(0x8B4513, 1);
    g.fillRoundedRect(0, 0, 520, 124, 8);
    g.fillStyle(0x2c3e50, 1);
    g.fillRoundedRect(60, 20, 400, 60, 8);
    g.fillStyle(0xecf0f1, 1);
    g.fillRect(70, 30, 380, 40);
    g.generateTexture("computerDesk", 520, 124);
    g.clear();

    /* Tür geschlossen */
    g.fillStyle(0x9a6232, 1);
    g.fillRoundedRect(0, 0, 140, 260, 10);
    g.fillStyle(0x7b4d26, 1);
    g.fillRoundedRect(10, 10, 120, 240, 8);
    g.fillStyle(0xe1c16e, 1);
    g.fillCircle(108, 130, 5);
    g.generateTexture("doorClosed", 140, 260);
    g.clear();

    /* Tür offen */
    g.fillStyle(0x9a6232, 1);
    g.fillRoundedRect(0, 0, 140, 260, 10);
    g.fillStyle(0x5e3417, 1);
    g.fillRoundedRect(10, 10, 120, 240, 8);
    g.fillStyle(0xa6f08a, 0.85);
    g.fillRect(20, 20, 100, 220);
    g.fillStyle(0xe1c16e, 1);
    g.fillCircle(108, 130, 5);
    g.generateTexture("doorOpen", 140, 260);
    g.clear();

    /* Nerd Player */
    g.fillStyle(0x1f1f1f, 1);
    g.fillEllipse(28, 14, 34, 20);

    g.fillStyle(0xf3c9a5, 1);
    g.fillCircle(28, 22, 16);

    g.lineStyle(2, 0x111111, 1);
    g.strokeCircle(21, 22, 6);
    g.strokeCircle(35, 22, 6);
    g.lineBetween(27, 22, 29, 22);

    g.fillStyle(0x4b6cb7, 1);
    g.fillRoundedRect(14, 38, 28, 26, 6);

    g.fillStyle(0xe8e8e8, 1);
    g.fillRect(23, 38, 10, 10);

    g.fillStyle(0x2f2f2f, 1);
    g.fillRect(17, 64, 8, 22);
    g.fillRect(31, 64, 8, 22);

    g.fillStyle(0xf3c9a5, 1);
    g.fillRect(8, 42, 8, 20);
    g.fillRect(40, 42, 8, 20);

    g.generateTexture("nerdPlayer", 56, 88);
    g.clear();
}