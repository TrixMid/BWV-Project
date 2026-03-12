CREATE TABLE player (
    player_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    team_id INT,
    CONSTRAINT fk_player_team
        FOREIGN KEY (team_id) REFERENCES team(id)
);

CREATE TABLE room (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    theme_id INT NOT NULL,
    min_points_required INT NOT NULL DEFAULT 10,
    max_points INT NOT NULL DEFAULT 30,
    map_x INT,
    map_y INT,
    image_url TEXT,
    room_order INT NOT NULL DEFAULT 0,
    question_set_id INT,
    CONSTRAINT fk_room_theme
        FOREIGN KEY (theme_id) REFERENCES theme(id),
    CONSTRAINT fk_room_question_set
        FOREIGN KEY (question_set_id) REFERENCES question_set(id)
);

CREATE TABLE game_session (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    player_id INT NOT NULL,
    current_room_id INT,
    status ENUM('ACTIVE', 'WON', 'ABANDONED') NOT NULL DEFAULT 'ACTIVE',
    total_points INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_saved_at DATETIME NULL,
    CONSTRAINT fk_game_session_player
        FOREIGN KEY (player_id) REFERENCES player(player_id),
    CONSTRAINT fk_game_session_current_room
        FOREIGN KEY (current_room_id) REFERENCES room(room_id)
);

CREATE TABLE room_connection (
    connection_id INT AUTO_INCREMENT PRIMARY KEY,
    from_room_id INT NOT NULL,
    to_room_id INT NOT NULL,
    label VARCHAR(255),
    requires_completion BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_room_connection_from
        FOREIGN KEY (from_room_id) REFERENCES room(room_id),
    CONSTRAINT fk_room_connection_to
        FOREIGN KEY (to_room_id) REFERENCES room(room_id)
);

CREATE TABLE room_progress (
    progress_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    room_id INT NOT NULL,
    points_earned INT NOT NULL DEFAULT 0,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    medal ENUM('NONE', 'BRONZE', 'SILVER', 'GOLD') NOT NULL DEFAULT 'NONE',
    CONSTRAINT uq_room_progress UNIQUE (session_id, room_id),
    CONSTRAINT fk_room_progress_session
        FOREIGN KEY (session_id) REFERENCES game_session(session_id),
    CONSTRAINT fk_room_progress_room
        FOREIGN KEY (room_id) REFERENCES room(room_id)
);

CREATE TABLE answered_question (
    answered_id INT AUTO_INCREMENT PRIMARY KEY,
    progress_id INT NOT NULL,
    question_id INT NOT NULL,
    points_earned INT NOT NULL,
    correct BOOLEAN NOT NULL,
    answered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_answered_question UNIQUE (progress_id, question_id),
    CONSTRAINT fk_answered_question_progress
        FOREIGN KEY (progress_id) REFERENCES room_progress(progress_id),
    CONSTRAINT fk_answered_question_question
        FOREIGN KEY (question_id) REFERENCES question(id)
);
