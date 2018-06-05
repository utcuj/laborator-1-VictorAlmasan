SET GLOBAL event_scheduler = ON;

DELIMITER $$
DROP EVENT IF EXISTS cleanContractTable $$
CREATE 
 EVENT cleanContractTable 
 ON SCHEDULE EVERY 40 SECOND STARTS '2018-05-26 00:00:01' 
 DO BEGIN
  DECLARE tempStringArray TEXT;
  DECLARE appStatus TINYINT(1);
  
  -- update contract status='T' whose end_date=current_date
UPDATE league.contract 
SET 
    status_code = 'T'
WHERE
    id_contract <> 0
        AND end_date = CURDATE();
  
  -- if the season is started
SELECT 
    application.status
INTO appStatus FROM
    application
WHERE
    code = 'S';
  
  IF appStatus <> 0 THEN 
  
   SELECT   IFNULL( GROUP_CONCAT(t.id_team) ,'') INTO tempStringArray FROM (SELECT c.id_team FROM league.contract c WHERE c.status_code='A' GROUP BY c.id_team HAVING COUNT(c.id_team)<3) t;
   
   -- update team status='I' whose number of players < min_player_no=5
UPDATE league.team 
SET 
    team_status_code = 'I'
WHERE
    id_team <> 0
        AND FIND_IN_SET(id_team, tempStringArray);
  
  END IF;
  
        -- save into contract_history all contracts having status_code in ('C','B','T') and then delete all from contract
        INSERT INTO league.contract_history(id_contract,id_player,id_team, status_code,player_role_code,start_date,end_date,salary) 
        SELECT id_contract, id_player, id_team, status_code, player_role_code, start_date, end_date, salary
        FROM contract
        WHERE status_code IN ('T', 'B', 'C');
DELETE FROM league.contract 
WHERE
    status_code IN ('T' , 'B', 'C');
  
  -- keep track of event execution
        INSERT INTO league.event_league (action) VALUES ('clean');
 END$$