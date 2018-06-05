-- start new season

DELIMITER $$
DROP PROCEDURE IF EXISTS startSeason $$
CREATE PROCEDURE  startSeason(
IN startDate DATE,
IN minPlayerNo INT,
OUT status INT)
BEGIN

	DECLARE activeTeamNo, constraintedTeamNo, roundNo, i INT DEFAULT 0;
	DECLARE tempStartDate DATE;

	SELECT COUNT(id_team) INTO activeTeamNo FROM team WHERE team_status_code='A';

	SELECT COUNT(t.idTeam) INTO constraintedTeamNo 
	FROM (SELECT c.id_team AS idTeam, (SELECT COUNT(c1.id_player) FROM contract c1 INNER JOIN player p ON c1.id_player=p.id_player WHERE c1.id_team=c.id_team AND p.player_position_code='GK' AND c1.status_code='A') AS goalKeeperNo,
		(SELECT COUNT(c2.id_player) FROM contract c2 INNER JOIN player p1 ON c2.id_player=p1.id_player WHERE c2.id_team=c.id_team AND c2.status_code='A') AS playerNO
		FROM contract c INNER JOIN team t1 ON c.id_team=t1.id_team
		WHERE t1.team_status_code='A'
		GROUP BY c.id_team) t
	WHERE t.goalKeeperNo >= 1 AND t.playerNo >= minPlayerNo;

	IF activeTeamNo > 0 AND MOD(activeTeamNo,2) = 0 THEN
		IF 	activeTeamNo = constraintedTeamNo THEN
			SET status = 1;
			UPDATE application SET status = 1, start_date = startDate WHERE code = 'S';
			INSERT INTO football_match (id_home_team,id_away_team) 
										SELECT t1.id_team, t2.id_team
										FROM team t1 INNER JOIN team t2 ON t1.id_team<>t2.id_team
										WHERE t1.team_status_code ='A' AND t2.team_status_code='A';	
										
			SET roundNo = (activeTeamNo - 1)*2;
			SET tempStartDate = startDate;
			
			REPEAT
				SET i = i + 1;
				INSERT INTO match_round (code, name, start_date, end_date) VALUES (i, CONCAT("R",i), tempStartDate, DATE_ADD(tempStartDate,INTERVAL 2 DAY));
				SET tempStartDate = DATE_ADD(tempStartDate,INTERVAL 7 DAY);
			UNTIL i = roundNo
			END REPEAT;
		ELSE
			SET status = 2;
		END IF;
	ELSE
		SET status = 0;
	END IF;

END$$

-- clear database for a new season

DELIMITER $$
DROP PROCEDURE IF EXISTS newSeason $$
CREATE PROCEDURE newSeason()
BEGIN

UPDATE application SET status = 0, start_date=null WHERE code = 'S';
DELETE FROM scorers;
DELETE FROM football_match;
DELETE FROM match_round;
INSERT INTO match_round (code, name) VALUES (0, "-");

END$$

-- renew contract

DELIMITER $$
DROP PROCEDURE IF EXISTS renewContract $$
CREATE PROCEDURE  renewContract(
IN contractId INT,
IN startDate DATE,
IN endDate DATE,
IN roleCode VARCHAR(2),
IN salaryV INT)
BEGIN

	INSERT INTO contract_history(id_contract,id_player,id_team, status_code,player_role_code,start_date,end_date,salary) 
								SELECT id_contract, id_player, id_team, 'R', player_role_code, start_date, end_date, salary
								FROM contract
								WHERE id_contract=contractId;
	UPDATE contract SET start_date=startDate, end_date=endDate, player_role_code=roleCode, salary=salaryV WHERE id_contract=contractId;
	
END$$

-- Capitalize first letter 

DROP FUNCTION IF EXISTS ucfirst;
DELIMITER $$
CREATE FUNCTION ucfirst(str_value VARCHAR(5000))
RETURNS VARCHAR(5000)
DETERMINISTIC
BEGIN
    RETURN CONCAT(UCASE(LEFT(str_value, 1)),SUBSTRING(str_value, 2));
END
$$

-- save contract 

DELIMITER $$
DROP PROCEDURE IF EXISTS saveContract $$
CREATE PROCEDURE  saveContract(
IN playerId INT,
IN teamId INT,
IN roleCode VARCHAR(2),
IN startDate DATE,
IN endDate DATE,
IN salaryV INT,
IN cnpV VARCHAR(13),
IN nationalityCode VARCHAR(2),
IN firstName VARCHAR(30),
IN lastName VARCHAR(30),
IN dateOfBirth DATE,
IN heightV INT,
IN weightV INT,
IN posCode VARCHAR(3),
OUT contractId INT)
BEGIN
	
	IF playerId=0 THEN
		INSERT INTO player (cnp, nationality_code, first_name, last_name,date_of_birth,height,weight,player_position_code) VALUES (cnpV, nationalityCode, ucfirst(firstName), ucfirst(lastName), dateOfBirth, heightV, weightV, posCode);
		INSERT INTO contract (id_player, id_team, status_code, player_role_code, start_date, end_date, salary) VALUES ((SELECT LAST_INSERT_ID()), teamId, 'A', roleCode, startDate, endDate, salaryV);
	ELSE
		INSERT INTO contract (id_player, id_team, status_code, player_role_code, start_date, end_date, salary) VALUES (playerId, teamId, 'A', roleCode, startDate, endDate, salaryV);
	END IF;

	SET contractId := (SELECT LAST_INSERT_ID()); 

END$$