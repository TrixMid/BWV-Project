const params = new URLSearchParams(window.location.search);
const sessionId = params.get("sessionId");
const level = params.get("level") || "1";

const ROOM_WIDTH = 2000;
const ROOM_HEIGHT = Math.max(1500, window.innerHeight + 500);
const PLAYER_SPEED = 300;

let summaryShown = false;

let reviewQuestions = [];
let currentReviewIndex = 0;

let sceneRef;
let player;
let tables = [];
let nextDoor = null;
let nextDoorLabel = null;
let doorTriggered = false;

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
    sceneRef = this;

    this.physics.world.setBounds(0, 0, ROOM_WIDTH, ROOM_HEIGHT);
    this.cameras.main.setBounds(0, 0, ROOM_WIDTH, ROOM_HEIGHT);
    this.cameras.main.setBackgroundColor("#dfe8f5");

    cursors = this.input.keyboard.createCursorKeys();
    keyA = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.A);
    keyD = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.D);
    keyW = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.W);
    keyS = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.S);

    drawReviewRoom.call(this);
    createPlayer.call(this);

    this.cameras.main.startFollow(player, true, 0.08, 0.08);
    this.cameras.main.setZoom(1);

    this.scale.on("resize", onResize, this);

    loadReviewQuestions();
}

function update() {
    if (!player) return;

    handleMovement();
    handleDoorOverlap();
}

function loadReviewQuestions() {
    fetch("/quizgame/session/" + sessionId + "/review")
        .then(res => res.json())
        .then(data => {
            reviewQuestions = Array.isArray(data) ? data : [];
            currentReviewIndex = 0;

            if (reviewQuestions.length === 0) {
                showBoardMessage("Keine Review-Daten gefunden.");
                return;
            }

            buildReviewQuestion(reviewQuestions[currentReviewIndex]);
        })
        .catch(() => {
            showBoardMessage("Fehler beim Laden der Review-Daten.");
        });
}

function buildReviewQuestion(reviewQuestion) {
    clearDynamicObjects();

    drawReviewRoom.call(sceneRef);
    createPlayer.call(sceneRef);

    sceneRef.cameras.main.startFollow(player, true, 0.08, 0.08);

    createBoardQuestion(reviewQuestion);
    createReviewTables(reviewQuestion.answers || []);
    createPointsInfo(reviewQuestion);
    createNextDoor();
}

function goToNextReviewQuestion() {
    if (doorTriggered) return;

    doorTriggered = true;

    if (nextDoor) {
        nextDoor.setTexture("doorOpen");
    }

    sceneRef.time.delayedCall(250, () => {
        if (summaryShown) {
            window.location.href = "/levels2";
            return;
        }

        currentReviewIndex++;

        if (currentReviewIndex >= reviewQuestions.length) {
            loadSummary();
        } else {
            buildReviewQuestion(reviewQuestions[currentReviewIndex]);
        }
    });
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
    summaryShown = false;
}

function showSummaryScreen(summary) {
    clearDynamicObjects();
    drawReviewRoom.call(sceneRef);
    createPlayer.call(sceneRef);

    summaryShown = true;

    // Titel oben auf der Tafel
    sceneRef.add.text(ROOM_WIDTH / 2, 108, "ABSCHLUSS", {
        fontFamily: "Arial Black, Arial, sans-serif",
        fontSize: "30px",
        color: "#ffe066",
        stroke: "#111111",
        strokeThickness: 5
    }).setOrigin(0.5).setDepth(20);

    // Zusammenfassung – in der Tafel anzeigen
    const medal = summary.medal || "NONE";
    const medalLabel = medal === "GOLD" ? "GOLD" : medal === "SILVER" ? "SILBER" : medal === "BRONZE" ? "BRONZE" : "KEINE";
    const text =
        "Team: " + (summary.playerName || "-") + "\n" +
        "Punkte: " + (summary.totalPoints ?? 0) + " / " + (summary.maxPoints ?? 0) + "\n" +
        "Score: " + (summary.scoreOutOf100 ?? 0) + " / 100\n" +
        "Medaille: " + medalLabel;

    const summaryText = sceneRef.add.text(ROOM_WIDTH / 2, 213, text, {
        fontFamily: "Arial Black, Arial, sans-serif",
        fontSize: "24px",
        color: "#ffffff",
        align: "center",
        stroke: "#111111",
        strokeThickness: 4,
        lineSpacing: 10
    }).setOrigin(0.5).setDepth(20);

    // fitTextToBox(text, maxWidth, maxHeight, startFontSize, minFontSize)
    fitTextToBox(summaryText, 800, 160, 24, 14);

    createNextDoor();

    if (nextDoorLabel) {
        nextDoorLabel.setText("ZURÜCK");
    }
}


function loadSummary() {
    fetch("/quizgame/session/" + sessionId + "/summary")
        .then(res => res.json())
        .then(summary => {
            showSummaryScreen(summary);
        })
        .catch(() => {
            showBoardMessage("Fehler beim Laden der Zusammenfassung.");
        });
}

function drawReviewRoom() {
    const scene = sceneRef || this;

    scene.add.rectangle(ROOM_WIDTH / 2, 170, ROOM_WIDTH, 340, 0xf4efe6).setDepth(-20);
    scene.add.rectangle(ROOM_WIDTH / 2, ROOM_HEIGHT - 210, ROOM_WIDTH, 520, 0xc7925b).setDepth(-20);

    for (let y = 360; y < ROOM_HEIGHT; y += 64) {
        for (let x = 0; x < ROOM_WIDTH; x += 64) {
            scene.add.image(x + 32, y + 32, "floor").setDepth(-19);
        }
    }

    scene.add.rectangle(ROOM_WIDTH / 2, 26, ROOM_WIDTH, 52, 0xe7dfd1).setDepth(-18);

    [180, 340, 1660].forEach(x => {
        scene.add.image(x, 160, "window").setDepth(-10);
    });

    scene.add.image(ROOM_WIDTH / 2, 190, "board").setDepth(-9);

    scene.add.text(28, 18, "ERGEBNIS / REVIEW", {
        fontFamily: "Arial Black, Arial, sans-serif",
        fontSize: "26px",
        color: "#ffffff",
        stroke: "#16324f",
        strokeThickness: 6
    }).setScrollFactor(0).setDepth(100);

    scene.add.text(28, 56, `Level ${level} - Review ${currentReviewIndex + 1}${reviewQuestions.length ? " / " + reviewQuestions.length : ""}`, {
        fontFamily: "Arial, sans-serif",
        fontSize: "19px",
        color: "#ffffff",
        stroke: "#000000",
        strokeThickness: 4
    }).setScrollFactor(0).setDepth(100);

    scene.add.image(ROOM_WIDTH / 2, 365, "teacherDesk").setDepth(1);
}

function createBoardQuestion(reviewQuestion) {
    const text = reviewQuestion?.startText || "Keine Frage vorhanden.";

    const boardText = sceneRef.add.text(
        ROOM_WIDTH / 2,
        190,
        text,
        {
            fontFamily: "Arial Black, Arial, sans-serif",
            fontSize: "32px",
            color: "#f5f5f5",
            wordWrap: { width: 760 },
            align: "center",
            lineSpacing: 8
        }
    ).setOrigin(0.5).setDepth(10);

    fitTextToBox(boardText, 760, 170, 32, 18);
}

function createReviewTables(answers) {
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

        let texture = "answerDesk";

        if (answer.correct && answer.selected) {
            texture = "answerDeskSelected";
        } else if (answer.correct) {
            texture = "answerDeskCorrect";
        } else if (answer.selected) {
            texture = "answerDeskWrong";
        }

        const desk = sceneRef.add.image(pos.x, pos.y, texture).setDepth(2);
        tables.push(desk);

        const answerText = sceneRef.add.text(
            pos.x,
            pos.y - 6,
            answer.optionText || "",
            {
                fontFamily: "Arial, sans-serif",
                fontSize: "24px",
                color: "#ffffff",
                wordWrap: { width: 420 },
                align: "center"
            }
        ).setOrigin(0.5).setDepth(3);

        fitTextToBox(answerText, 420, 78, 24, 16);
    });
}

function createPointsInfo(reviewQuestion) {
    const infoText =
        "Erhaltene Punkte: " + (reviewQuestion.pointsEarned ?? 0) +
        " / " + (reviewQuestion.maxPoints ?? 0) +
        (reviewQuestion.correct ? "   RICHTIG" : "   FALSCH");

    sceneRef.add.text(ROOM_WIDTH / 2, 420, infoText, {
        fontFamily: "Arial Black, Arial, sans-serif",
        fontSize: "24px",
        color: "#ffffff",
        stroke: "#111111",
        strokeThickness: 5
    }).setOrigin(0.5).setDepth(20);
}

function createNextDoor() {
    const doorX = 1860;
    const doorY = 230;

    nextDoor = sceneRef.add.image(doorX, doorY, "doorClosed").setDepth(2);
    sceneRef.physics.add.existing(nextDoor, true);

    nextDoorLabel = sceneRef.add.text(doorX, doorY - 130, "WEITER", {
        fontFamily: "Arial Black, Arial, sans-serif",
        fontSize: "24px",
        color: "#ffffff",
        stroke: "#111111",
        strokeThickness: 5
    }).setOrigin(0.5).setDepth(3);
}

function handleDoorOverlap() {
    if (!nextDoor || !player || doorTriggered) return;

    const isOverlapping = sceneRef.physics.overlap(player, nextDoor);

    if (isOverlapping) {
        goToNextReviewQuestion();
    } else {
        nextDoor.setTexture("doorClosed");
    }
}

function showBoardMessage(message) {
    clearDynamicObjects();
    drawReviewRoom.call(sceneRef);
    createPlayer.call(sceneRef);

    const msg = sceneRef.add.text(
        ROOM_WIDTH / 2,
        190,
        message,
        {
            fontFamily: "Arial Black, Arial, sans-serif",
            fontSize: "32px",
            color: "#f5f5f5",
            wordWrap: { width: 760 },
            align: "center"
        }
    ).setOrigin(0.5).setDepth(10);

    fitTextToBox(msg, 760, 170, 32, 18);
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

function createPlayer() {
    player = sceneRef.physics.add.sprite(ROOM_WIDTH / 2, ROOM_HEIGHT - 900, "nerdPlayer");
    player.setCollideWorldBounds(true);
    player.setDepth(5);
    player.body.setSize(34, 62, true);
    player.body.setOffset(11, 20);
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

function onResize(gameSize) {
    this.cameras.resize(gameSize.width, gameSize.height);
}

function createTextures() {
    const g = this.make.graphics({ x: 0, y: 0, add: false });

    g.fillStyle(0xc7925b, 1);
    g.fillRect(0, 0, 64, 64);
    g.lineStyle(2, 0xb17f4f, 1);
    g.lineBetween(0, 16, 64, 16);
    g.lineBetween(0, 32, 64, 32);
    g.lineBetween(0, 48, 64, 48);
    g.generateTexture("floor", 64, 64);
    g.clear();

    g.fillStyle(0xffffff, 1);
    g.fillRoundedRect(0, 0, 120, 140, 8);
    g.fillStyle(0xa8d8ff, 1);
    g.fillRoundedRect(8, 8, 104, 124, 6);
    g.lineStyle(4, 0xffffff, 1);
    g.lineBetween(60, 8, 60, 132);
    g.lineBetween(8, 70, 112, 70);
    g.generateTexture("window", 120, 140);
    g.clear();

    g.fillStyle(0x6b4423, 1);
    g.fillRoundedRect(0, 0, 900, 240, 10);
    g.fillStyle(0x2d5b37, 1);
    g.fillRoundedRect(18, 18, 864, 204, 6);
    g.generateTexture("board", 900, 240);
    g.clear();

    g.fillStyle(0x8b5a2b, 1);
    g.fillRoundedRect(0, 0, 320, 100, 8);
    g.fillStyle(0x6b3f1f, 1);
    g.fillRect(18, 78, 16, 42);
    g.fillRect(286, 78, 16, 42);
    g.generateTexture("teacherDesk", 320, 120);
    g.clear();

    g.fillStyle(0x9a6232, 1);
    g.fillRoundedRect(0, 0, 520, 110, 8);
    g.fillStyle(0x73451f, 1);
    g.fillRect(18, 88, 14, 36);
    g.fillRect(488, 88, 14, 36);
    g.generateTexture("answerDesk", 520, 124);
    g.clear();

    g.fillStyle(0x3fae4a, 1);
    g.fillRoundedRect(0, 0, 520, 110, 8);
    g.fillStyle(0x2f7d36, 1);
    g.fillRect(18, 88, 14, 36);
    g.fillRect(488, 88, 14, 36);
    g.generateTexture("answerDeskSelected", 520, 124);
    g.clear();

    g.fillStyle(0x2d8fdd, 1);
    g.fillRoundedRect(0, 0, 520, 110, 8);
    g.fillStyle(0x20679f, 1);
    g.fillRect(18, 88, 14, 36);
    g.fillRect(488, 88, 14, 36);
    g.generateTexture("answerDeskCorrect", 520, 124);
    g.clear();

    g.fillStyle(0xc74848, 1);
    g.fillRoundedRect(0, 0, 520, 110, 8);
    g.fillStyle(0x8f3131, 1);
    g.fillRect(18, 88, 14, 36);
    g.fillRect(488, 88, 14, 36);
    g.generateTexture("answerDeskWrong", 520, 124);
    g.clear();

    g.fillStyle(0x9a6232, 1);
    g.fillRoundedRect(0, 0, 140, 260, 10);
    g.fillStyle(0x7b4d26, 1);
    g.fillRoundedRect(10, 10, 120, 240, 8);
    g.fillStyle(0xe1c16e, 1);
    g.fillCircle(108, 130, 5);
    g.generateTexture("doorClosed", 140, 260);
    g.clear();

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