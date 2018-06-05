-- ----- UPDATE TEAM STATUS --------

DELIMITER $$
DROP TRIGGER IF EXISTS onUpdateTeamStatus $$
CREATE  TRIGGER  onUpdateTeamStatus
AFTER UPDATE  ON team
FOR EACH ROW
BEGIN
IF NEW.team_status_code <> OLD.team_status_code && NEW.team_status_code = 'I'
THEN

UPDATE football_match SET match_status_code = 'B' , 
nr_goals_home_team = CASE WHEN id_home_team = OLD.id_team THEN 0 ELSE 3 END,
nr_goals_away_team = CASE WHEN id_away_team = OLD.id_team THEN 0 ELSE 3 END
WHERE (id_home_team=OLD.id_team OR id_away_team = OLD.id_team) AND match_status_code NOT IN ('B','F') ;

UPDATE contract c SET c.status_code="B" WHERE c.id_team=OLD.id_team AND c.status_code<> 'C';

END IF;
END$$

-- ----- UPDATE UPDATED DATE ON CONTRACT_HISTORY -------
DELIMITER $$
DROP TRIGGER IF EXISTS defaultDate $$
CREATE TRIGGER defaultDate 
BEFORE INSERT ON contract_history 
FOR EACH ROW
BEGIN
IF (ISNULL(NEW.update_date) ) THEN
 SET NEW.update_date=CURDATE();
END IF;
END$$
