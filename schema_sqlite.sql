

DROP TABLE IF EXISTS `Question_Theme`;
DROP TABLE IF EXISTS `gap_option`;
DROP TABLE IF EXISTS `gap_field`;
DROP TABLE IF EXISTS `mc_answer`;
DROP TABLE IF EXISTS `question`;
DROP TABLE IF EXISTS `question_set`;
DROP TABLE IF EXISTS `theme`;
DROP TABLE IF EXISTS `team`; 


CREATE TABLE `team` (
  `team_id` INTEGER PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`team_id`)
);

CREATE TABLE `theme` (
  `theme_id` INTEGER PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT NULL,
  PRIMARY KEY (`theme_id`)
);

CREATE TABLE `question_set` (
  `question_set_id` INTEGER PRIMARY KEY,
  `team_id` INT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`question_set_id`),
  CONSTRAINT `fk_question_set_team`
    FOREIGN KEY (`team_id`) REFERENCES `team`(`team_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE `question` (
  `question_id` INTEGER PRIMARY KEY,
  `question_set_id` INT NOT NULL,
  `question_type` VARCHAR(255) NOT NULL,
  `start_text` TEXT NULL,
  `image_url` TEXT NULL,
  `end_text` TEXT NULL,
  `allows_multiple` BOOLEAN NOT NULL DEFAULT false,
  `points` INT NOT NULL DEFAULT true,
  PRIMARY KEY (`question_id`),
  CONSTRAINT `fk_question_question_set`
    FOREIGN KEY (`question_set_id`) REFERENCES `question_set`(`question_set_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE `Question_Theme` (
  `question_id` INT NOT NULL,
  `theme_id` INT NOT NULL,
  PRIMARY KEY (`question_id`, `theme_id`),
  CONSTRAINT `fk_qt_question`
    FOREIGN KEY (`question_id`) REFERENCES `question`(`question_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_qt_theme`
    FOREIGN KEY (`theme_id`) REFERENCES `theme`(`theme_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

CREATE TABLE `mc_answer` (
  `answer_id` INTEGER PRIMARY KEY,
  `question_id` INT NOT NULL,
  `option_text` TEXT NOT NULL,
  `is_correct` BOOLEAN NOT NULL DEFAULT false,
  `option_order` INT NOT NULL,
  PRIMARY KEY (`answer_id`),
  CONSTRAINT `fk_mc_answer_question`
    FOREIGN KEY (`question_id`) REFERENCES `question`(`question_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE `gap_field` (
  `gap_id` INTEGER PRIMARY KEY,
  `question_id` INT NOT NULL,
  `gap_index` INT NOT NULL,
  `text_before` TEXT NULL,
  `text_after` TEXT NULL,
  PRIMARY KEY (`gap_id`),
  CONSTRAINT `fk_gap_field_question`
    FOREIGN KEY (`question_id`) REFERENCES `question`(`question_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE `gap_option` (
  `gap_option_id` INTEGER PRIMARY KEY,
  `gap_id` INT NOT NULL,
  `option_text` TEXT NOT NULL,
  `is_correct` BOOLEAN NOT NULL DEFAULT false,
  `option_order` INT NOT NULL,
  PRIMARY KEY (`gap_option_id`),
  CONSTRAINT `fk_gap_option_gap`
    FOREIGN KEY (`gap_id`) REFERENCES `gap_field`(`gap_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- =========================
-- SEED DATA (auto-generated; edit via GUI)
-- =========================
-- === SEED DATA: BEGIN ===
INSERT INTO theme (theme_id, name, description) VALUES (2, 'Recht', 'Alles was mit Gesetzesgrundlagen zu tun hat');
INSERT INTO theme (theme_id, name, description) VALUES (3, 'Wirtschaft', 'Alles, was NICHT mit Recht, aber mit Wirtschaft zu tun hat (Kalkulation, Prozesse,...)');
INSERT INTO theme (theme_id, name, description) VALUES (4, 'Datenbanken Modellierung', 'ERD, relationales Tabellenmodell');
INSERT INTO theme (theme_id, name, description) VALUES (5, 'Datenbank - SQL', NULL);
INSERT INTO theme (theme_id, name, description) VALUES (6, 'UML', 'Klassendiagramm, Sequenzdiagramm, Zustandsdiagramm, Use-Case Diagramm, Aktivitätsdiagramm');
INSERT INTO theme (theme_id, name, description) VALUES (7, 'Maschinelles Lernen', NULL);
INSERT INTO theme (theme_id, name, description) VALUES (8, 'Programmierung Pseudocode', NULL);
-- === SEED DATA: END ===


