-- ----- CREATE DATABASE -------
DROP DATABASE IF EXISTS league;
CREATE DATABASE league;
USE league;
SET GLOBAL event_scheduler = ON;


-- ------ DELETE EVERYTHING FROM DATABASE -------

DROP TRIGGER IF EXISTS onUpdateTeamStatus;
DROP TRIGGER IF EXISTS defaultDate;

DROP EVENT IF EXISTS cleanContractTable;

DROP PROCEDURE IF EXISTS newSeason;
DROP PROCEDURE IF EXISTS renewContract;
DROP PROCEDURE IF EXISTS saveContract;
DROP PROCEDURE IF EXISTS startSeason;

DROP VIEW IF EXISTS matchlist;
DROP VIEW IF EXISTS playercontracthistory;
DROP VIEW IF EXISTS scorerstandings;
DROP VIEW IF EXISTS teamsaway;
DROP VIEW IF EXISTS teamshome;
DROP VIEW IF EXISTS teamsoverall;

DROP TABLE IF EXISTS scorers;
DROP TABLE IF EXISTS football_match;
DROP TABLE IF EXISTS contract;
DROP TABLE IF EXISTS contract_history;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS team;
DROP TABLE IF EXISTS coach;
DROP TABLE IF EXISTS contract_status;
DROP TABLE IF EXISTS match_round;
DROP TABLE IF EXISTS match_status;
DROP TABLE IF EXISTS nationality;
DROP TABLE IF EXISTS player_position;
DROP TABLE IF EXISTS player_role;
DROP TABLE IF EXISTS team_status;
DROP TABLE IF EXISTS application;
DROP TABLE IF EXISTS event_league;
DROP TABLE IF EXISTS user_league;

-- ----- CREATE LOOKUP TABLES -------

CREATE TABLE IF NOT EXISTS team_status (
    team_status_code VARCHAR(1) PRIMARY KEY NOT NULL,
    team_status VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS coach (
    coach_code VARCHAR(3) PRIMARY KEY NOT NULL,
    coach_name VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS nationality (
    nationality_code VARCHAR(2) PRIMARY KEY NOT NULL,
    nationality VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS player_position (
    player_position_code VARCHAR(3) PRIMARY KEY NOT NULL,
    player_position VARCHAR(40)
);

CREATE TABLE IF NOT EXISTS contract_status (
    contract_status_code VARCHAR(1) PRIMARY KEY NOT NULL,
    contract_status VARCHAR(20)
);
 
CREATE TABLE IF NOT EXISTS player_role (
    player_role_code VARCHAR(2) PRIMARY KEY NOT NULL,
    player_role VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS match_status(
 match_status_code varchar(1) primary key not null,
 match_status varchar(30)
 );
 
-- ----- CREATE TABLES -------

CREATE TABLE IF NOT EXISTS team (
    id_team INT(3) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    budget INT(9),
    team_name VARCHAR(30) NOT NULL,
    team_short_name VARCHAR(5) NOT NULL,
    coach_code VARCHAR(3) NOT NULL,
    ground VARCHAR(30) NOT NULL,
    founded DATE NOT NULL,
    team_status_code VARCHAR(1) NOT NULL,
    CONSTRAINT fk_team_coach FOREIGN KEY (coach_code)
        REFERENCES coach (coach_code),
    CONSTRAINT fk_team_team_status FOREIGN KEY (team_status_code)
        REFERENCES team_status (team_status_code)
);

CREATE TABLE IF NOT EXISTS player (
    id_player INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    cnp varchar(13) NOT NULL,
    nationality_code VARCHAR(2) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    date_of_birth DATE NOT NULL,
    height INT(3),
    weight INT(3),
    player_position_code VARCHAR(3) NOT NULL,
    CONSTRAINT fk_player_nationality FOREIGN KEY (nationality_code)
        REFERENCES Nationality (nationality_code),
    CONSTRAINT fk_player_player_position FOREIGN KEY (player_position_code)
        REFERENCES Player_Position (player_position_code)
);
 
CREATE TABLE IF NOT EXISTS contract (
    id_contract INT(3) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    id_player INT(3) NOT NULL,
    id_team INT(3) NOT NULL,
    status_code VARCHAR(1) NOT NULL,
    player_role_code VARCHAR(2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    salary INT(9) NOT NULL,
    CONSTRAINT fk_contract_player FOREIGN KEY (id_player)
        REFERENCES player (id_player),
    CONSTRAINT fk_contract_team FOREIGN KEY (id_team)
        REFERENCES team (id_team),
    CONSTRAINT fk_contract_contract_status FOREIGN KEY (status_code)
        REFERENCES contract_status (contract_status_code),
    CONSTRAINT fk_contract_player_role FOREIGN KEY (player_role_code)
        REFERENCES player_role (player_role_code)
);

CREATE TABLE IF NOT EXISTS contract_history (
    id_contract INT(3) NOT NULL,
    id_player INT(3) NOT NULL,
    id_team INT(3) NOT NULL,
    status_code VARCHAR(1) NOT NULL,
    player_role_code VARCHAR(2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    salary INT(9) NOT NULL,
    update_date DATE NOT NULL,
    CONSTRAINT fk_contract_history_player FOREIGN KEY (id_player)
        REFERENCES player (id_player),
    CONSTRAINT fk_contract_history_team FOREIGN KEY (id_team)
        REFERENCES team (id_team)
);
 
 CREATE TABLE IF NOT EXISTS match_round (
	code INT(3) PRIMARY KEY NOT NULL,
	name VARCHAR(10) NOT NULL,
	start_date DATE,
	end_date DATE
);
 
CREATE TABLE IF NOT EXISTS football_match (
    id_match INT(3) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    id_home_team INT(3) NOT NULL,
    id_away_team INT(3) NOT NULL,
	round_code INT(3),
    match_date DATE,
    nr_goals_home_team INT(3),
    nr_goals_away_team INT(3),
    match_status_code VARCHAR(1) DEFAULT 'U',
    CONSTRAINT fk_football_match_team FOREIGN KEY (id_home_team)
        REFERENCES team (id_team),
    CONSTRAINT fk_football_match_team2 FOREIGN KEY (id_away_team)
        REFERENCES team (id_team),
    CONSTRAINT fk_football_match_match_status FOREIGN KEY (match_status_code)
        REFERENCES match_status (match_status_code),
	CONSTRAINT fk_football_match_round FOREIGN KEY (round_code)
        REFERENCES match_round (code)
);
 
 CREATE TABLE IF NOT EXISTS scorers (
    id_match INT(3) NOT NULL,
    id_player INT(3) NOT NULL,
    CONSTRAINT fk_scorers_football_match FOREIGN KEY (id_match)
        REFERENCES football_match (id_match),
    CONSTRAINT fk_scorers_player FOREIGN KEY (id_player)
        REFERENCES player (id_player)
);

 CREATE TABLE IF NOT EXISTS user_league (
 username VARCHAR(20) PRIMARY KEY NOT NULL,
 password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS application (
	code varchar(2) PRIMARY KEY NOT NULL,
	action VARCHAR(20) NOT NULL,
	status TINYINT(1) NOT NULL,
	start_date DATE 
);

 CREATE TABLE IF NOT EXISTS event_league(
 id_event INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
 date_event TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 action VARCHAR(10)
 );
 
-- ----- CREATE INDEXES -------
 
CREATE UNIQUE INDEX unique_team_name ON team (team_name);
CREATE UNIQUE INDEX unique_team_short_name ON team (team_short_name);

CREATE UNIQUE INDEX unique_player_cnp ON player (cnp);

CREATE UNIQUE INDEX unique_id_player ON contract (id_player);



DROP USER 'admin'@'localhost', 'user'@'localhost';



GRANT ALL PRIVILEGES ON league.* TO 'admin'@'localhost' IDENTIFIED BY 'admin';
CREATE USER 'user'@'localhost' IDENTIFIED BY 'user';
GRANT SELECT ON league.* TO 'user'@'localhost';



FLUSH PRIVILEGES;



INSERT INTO user_league VALUES ('admin',SHA1('admin'));
INSERT INTO user_league VALUES ('user',SHA1('user'));


INSERT INTO match_round (code, name) VALUES (0, "-");

INSERT INTO application (action, status, start_date, code) VALUES ('Start Season', '0', NULL, 'S');
 
INSERT INTO team_status VALUES ("A","Active");
INSERT INTO team_status VALUES ("I","Inactive");

INSERT INTO contract_status VALUES ("A","Active");
INSERT INTO contract_status VALUES ("B","Bankruptcy"); -- faliment echipa
INSERT INTO contract_status VALUES ("C","Cancelled"); -- contract reziliat
INSERT INTO contract_status VALUES ("T","Terminate"); -- perioada de contract terminata
INSERT INTO contract_status VALUES ("R","Renewed"); -- reinnoit

INSERT INTO match_status VALUES ("U","Unscheduled");
INSERT INTO match_status VALUES ("P","Pending");
INSERT INTO match_status VALUES ("B","Bankruptcy");
INSERT INTO match_status VALUES ("F","Finished");

INSERT INTO nationality VALUES ("AR","Argentina");
INSERT INTO nationality VALUES ("BE","Belgium");
INSERT INTO nationality VALUES ("BR","Brazil");
INSERT INTO nationality VALUES ("CO","Colombia");
INSERT INTO nationality VALUES ("DE","Germany");
INSERT INTO nationality VALUES ("ES","Spain");
INSERT INTO nationality VALUES ("FR","France");
INSERT INTO nationality VALUES ("IT","Italy");
INSERT INTO nationality VALUES ("MX","Mexico");
INSERT INTO nationality VALUES ("NG","Nigeria");
INSERT INTO nationality VALUES ("GR","Greece");
INSERT INTO nationality VALUES ("RO","Romania");
INSERT INTO nationality VALUES ("PL","Poland");
INSERT INTO nationality VALUES ("PT","Portugal");
INSERT INTO nationality VALUES ("TR","Turkey");
INSERT INTO nationality VALUES ("VE","Venezuela");
INSERT INTO nationality VALUES ("CR","Croatia");
INSERT INTO nationality VALUES ("HU","Hungary");
INSERT INTO nationality VALUES ("IS","Iceland");
INSERT INTO nationality VALUES ("JP","Japan");
INSERT INTO nationality VALUES ("SE","Sweden");
INSERT INTO nationality VALUES ("RU","Russia");
INSERT INTO nationality VALUES ("US","United State");
INSERT INTO nationality VALUES ("EN","England");

INSERT INTO player_role VALUES ("CR", "Crucial");
INSERT INTO player_role VALUES ("IM", "Important");
INSERT INTO player_role VALUES ("FU", "Future 1st team");

INSERT INTO player_position VALUES ("GK","Goalkeeper");
INSERT INTO player_position VALUES ("LB","Left Back");
INSERT INTO player_position VALUES ("CB","Central Back");
INSERT INTO player_position VALUES ("RB","Right Back");
INSERT INTO player_position VALUES ("LM","Left Midfielder");
INSERT INTO player_position VALUES ("CM","Central Midfielder");
INSERT INTO player_position VALUES ("RM","Right Midfielder");
INSERT INTO player_position VALUES ("CAM","Central Attacking Midfielder");
INSERT INTO player_position VALUES ("CDM","Central Defending Midfielder");
INSERT INTO player_position VALUES ("LAM","Left Attacking Midfielder");
INSERT INTO player_position VALUES ("RAM","Right Attacking Midfielder");
INSERT INTO player_position VALUES ("LW","Left Winger");
INSERT INTO player_position VALUES ("RW","Right Winger");
INSERT INTO player_position VALUES ("CF","Central Forward");
INSERT INTO player_position VALUES ("ST","Striker");

INSERT INTO coach VALUES ("MOU","Jose Mourinho");
INSERT INTO coach VALUES ("GUA","Josep Guardiola");
INSERT INTO coach VALUES ("SIM","Diego Simeone");
INSERT INTO coach VALUES ("EME","Unai Emery");
INSERT INTO coach VALUES ("WEN","Arsene Wenger");
INSERT INTO coach VALUES ("SAC","Eusebio Sacristan");
INSERT INTO coach VALUES ("SCO","Luiz Felipe Scolari");
INSERT INTO coach VALUES ("ALL","Massimiliano Allegri");
INSERT INTO coach VALUES ("ANC","Carlo Ancelotti");
INSERT INTO coach VALUES ("SOU","Paulo Sousa");
INSERT INTO coach VALUES ("SPA","Luciano Spalletti");
INSERT INTO coach VALUES ("SCH","Roger Schmidt");
INSERT INTO coach VALUES ("KLP","Jurgen Klopp");
INSERT INTO coach VALUES ("CTE","Antonio Conte");
INSERT INTO coach VALUES ("ZID","Zinedin Zidane");
INSERT INTO coach VALUES ("FON","Paulo Fonseca");
INSERT INTO coach VALUES ("CAP","Fabio Capello");
INSERT INTO coach VALUES ("SAR","Maurizio Sarri");
INSERT INTO coach VALUES ("POC","Mauricio Pochettino");
INSERT INTO coach VALUES ("KOE","Ronald Koeman");
INSERT INTO coach VALUES ("PUE","Claude Puel");
INSERT INTO coach VALUES ("PUL","Tony Pulls");
INSERT INTO coach VALUES ("MAZ","Walter Mazzari");
INSERT INTO coach VALUES ("BIL","Slaven Bilic");
INSERT INTO coach VALUES ("HOW","Eddie Howe");
INSERT INTO coach VALUES ("HUG","Mark Hughes");
INSERT INTO coach VALUES ("DYC","Sean Dyche");

-- ----- TEST DATA -------

INSERT INTO team (budget, team_name, team_short_name, coach_code, ground, founded, team_status_code) VALUES (10000000,'Chelsea FC','CFC','CTE','Stamford Bridge','1905-03-10','A'),(10000002,'Liverpool FC','LFC','KLP','Anfield','1892-06-03','A'),(100000009,'Manchester City FC','MCFC','GUA','City of Manchester Stadium','1894-04-16','A'),(10000000,'Arsenal FC','AFC','WEN','Emirates Stadium','1886-05-20','A'),(10000000,'Tottenham Hotspur FC','THFC','POC','White Hart Lane','1882-09-05','A'),(10000000,'Manchester United FC','MUFC','MOU','Old Trafford','1878-12-11','A'),(10000000,'Everton FC','EFC','KOE','Goodison Park','1878-06-10','A'),(10000000,'Southampton FC','SFC','PUE','The Dell','1885-11-21','A'),(10000000,'West Bromwich Albion FC','WBA','PUL','The Hawthorns','1878-07-07','A'),(10000000,'Watford FC','WFC','MAZ','Vicarage Road','1881-09-03','A'),(10000000,'West Ham United FC','WHU','BIL','London Stadium','1895-06-29','A'),(10000000,'AFC Bournemouth','AFCB','HOW','Dean Court','1899-11-20','A'),(10000000,'Stoke City FC','SCFC','HUG','Bet365 Stadium','1863-10-17','A'),(10000000,'Burnley FC','BFC','DYC','Turf Moor','1882-03-20','A'),(10000000,'Middlesbrough FC','MFC','SIM','Rivierside Stadium','1878-10-10','A'),(10000000,'Leicester City FC','LCFC','SOU','King Power Stadium','1884-05-09','A'),(10000000,'Crystal Palace FC','CPFC','FON','Selhurst Park','1905-09-10','A');
INSERT INTO player (cnp, nationality_code, first_name, last_name, date_of_birth,height, weight, player_position_code) VALUES ('1871230385081','BE','Thibaut','Courtois','1987-12-30',NULL,NULL,'GK'),('1871230139091','CO','Asmir','Begovic','1987-12-30',NULL,NULL,'GK'),('1960502116718','CO','David','Ospina','1996-05-02',NULL,NULL,'GK'),('1950801282003','EN','Ryan','Allsop','1995-08-01',NULL,NULL,'GK'),('1940511403758','AR','Willy','Caballero','1994-05-11',NULL,NULL,'GK'),('1950320294223','ES','Sergio','Romero','1995-03-20',NULL,NULL,'GK'),('1950704364361','AR','David','Geo','1995-07-04',NULL,NULL,'GK'),('1900511071527','EN','Tom','Heaton','1990-05-11',NULL,NULL,'GK'),('1970327393811','ES','Santiago','Cazoria','1997-03-27',190,90,'LM'),('1980604179421','FR','Francis','Coquelin','1998-06-04',179,70,'CM'),('1900413135724','EN','Kieran','Gibbs','1990-04-13',179,89,'CF'),('1911118183526','NG','Alex','Iwobi','1991-11-18',178,67,'CDM'),('1930719095880','ES','Diego','Costa','1993-07-19',180,87,'CF'),('1890316338579','NG','Ola','Aina','1989-03-16',168,78,'CDM'),('1951130122389','BR','David','Luiz','1995-11-30',179,78,'CDM'),('1950530121401','EN','Nathaniel','Chalobah','1995-05-30',189,90,'CM'),('1950727217347','DE','Emre','Can','1995-07-27',179,67,'CAM'),('1940524123701','EN','Jordon','Henderson','1994-05-24',187,99,'CDM'),('1940404454595','EN','Daniel','Sturridge','1994-04-04',168,78,'ST'),('1980618275118','BR','Lucas','Leiva','1998-06-18',167,65,'CM'),('1921117199044','BE','Vincent','Kompany','1992-11-17',190,99,'CDM'),('1940908129686','AR','Pablo','Zabaleta','1994-09-08',189,88,'CM'),('1890202130698','AR','Sergio','Aguero','1989-02-02',167,78,'ST'),('1950203302951','EN','Raheem','Sterling','1995-02-03',168,76,'CF'),('1871230026158','EN','Phil','Jones','1987-12-30',NULL,NULL,'LM'),('1871230272818','AR','Chris','Smalling','1987-12-30',NULL,NULL,'LM'),('1860912225244','EN','Daley','Blind','1986-09-12',NULL,NULL,'RM'),('1860912161778','EN','Antonio','Valencia','1986-09-12',NULL,NULL,'RM'),('1860912247234','ES','Marcos','Rojo','1986-09-12',NULL,NULL,'RM'),('1860912309057','EN','Luke','Shaw','1986-09-12',NULL,NULL,'RM'),('1991120199144','EN','Matteo','Darmian','1986-11-20',NULL,NULL,'CAM'),('1991120278054','ES','Juan','Mata','1986-11-20',NULL,NULL,'CAM'),('1991120030967','EN','Ander','Herrera','1986-11-20',NULL,NULL,'CAM'),('1991120467724','EN','Jesse','Lingard','1986-11-20',NULL,NULL,'CAM'),('1991120085727','EN','Memphis','Depay','1986-11-20',NULL,NULL,'CAM'),('1991120066572','AR','Anthony','Martial','1986-11-20',NULL,NULL,'CAM');

UPDATE team SET team_status_code='I' WHERE team_name IN ('Arsenal FC','Crystal Palace FC','Burnley FC','Everton FC','West Ham United FC','AFC Bournemouth','Leicester City FC','Middlesbrough FC','Southampton FC','Stoke City FC','Watford FC');






use league;
CREATE USER 'user1'@'localhost' IDENTIFIED BY 'user1';
GRANT SELECT ON league.* TO 'user1'@'localhost';

CREATE USER 'user2'@'localhost' IDENTIFIED BY 'user2';
GRANT SELECT ON league.* TO 'user2'@'localhost';


INSERT INTO user_league VALUES ('user1',SHA1('user1'));
INSERT INTO user_league VALUES ('user2',SHA1('user2'));