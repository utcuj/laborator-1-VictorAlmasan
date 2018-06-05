-- Team standings (overall)

CREATE OR REPLACE VIEW teamsOverall AS
SELECT id_team, team_name, team_short_name, matches_played, draws, wins, losses, (ifnull(draws,0) + ifnull(wins,0)*3) as points
FROM 
(SELECT id_team, team_name, team_short_name, (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code='F' AND (fm.id_away_team=id_team OR fm.id_home_team=id_team)) AS matches_played,
 (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code IN ('F','B') AND (fm.id_away_team=id_team OR fm.id_home_team=id_team) AND fm.nr_goals_away_team=fm.nr_goals_home_team) AS draws,
 (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code IN ('F','B') AND ((fm.id_away_team=id_team AND fm.nr_goals_away_team>fm.nr_goals_home_team) OR (fm.id_home_team=id_team AND fm.nr_goals_away_team<fm.nr_goals_home_team))) AS wins,
 (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code IN ('F','B') AND ((fm.id_away_team=id_team AND fm.nr_goals_away_team<fm.nr_goals_home_team) OR (fm.id_home_team=id_team AND fm.nr_goals_away_team>fm.nr_goals_home_team))) AS losses
FROM team
WHERE team_status_code = 'A') t1;

-- Team standings (home)

CREATE OR REPLACE VIEW teamsHome AS
SELECT id_team, team_name, team_short_name, matches_played, draws, wins, losses, (ifnull(draws,0) + ifnull(wins,0)*3) as points
FROM 
(SELECT id_team, team_name, team_short_name, (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code='F' AND  fm.id_home_team=id_team) AS matches_played,
 (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code IN ('F','B') AND fm.id_home_team=id_team AND fm.nr_goals_away_team=fm.nr_goals_home_team) AS draws,
 (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code IN ('F','B') AND fm.id_home_team=id_team AND fm.nr_goals_away_team<fm.nr_goals_home_team) AS wins,
 (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code IN ('F','B') AND fm.id_home_team=id_team AND fm.nr_goals_away_team>fm.nr_goals_home_team) AS losses
FROM team
WHERE team_status_code = 'A') t1;

-- Team standings (away)

CREATE OR REPLACE VIEW teamsAway AS
SELECT id_team, team_name, team_short_name, matches_played, draws, wins, losses, (ifnull(draws,0) + ifnull(wins,0)*3) as points
FROM 
(SELECT id_team, team_name, team_short_name, (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code='F' AND  fm.id_away_team=id_team) AS matches_played,
 (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code IN ('F','B') AND fm.id_away_team=id_team AND fm.nr_goals_away_team=fm.nr_goals_home_team) AS draws,
 (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code IN ('F','B') AND fm.id_away_team=id_team AND fm.nr_goals_away_team>fm.nr_goals_home_team) AS wins,
 (SELECT COUNT(fm.id_match) FROM football_match fm WHERE fm.match_status_code IN ('F','B') AND fm.id_away_team=id_team AND fm.nr_goals_away_team<fm.nr_goals_home_team) AS losses
FROM team
WHERE team_status_code = 'A') t1;

-- Scorers

CREATE OR REPLACE VIEW scorerStandings AS
SELECT c.id_team, t.team_name, p.id_player, CONCAT_WS(' ',p.first_name, UPPER(p.last_name)) AS player_name, pp.player_position_code, pp.player_position, (SELECT COUNT(s.id_match) FROM scorers s WHERE s.id_player=p.id_player) AS goals
FROM contract c INNER JOIN team t ON c.id_team=t.id_team INNER JOIN player p ON c.id_player=p.id_player INNER JOIN player_position pp ON p.player_position_code=pp.player_position_code;

-- Match list

CREATE OR REPLACE VIEW matchList AS
SELECT fm.id_match, t1.team_name AS away_team, t2.team_name AS home_team, fm.match_status_code, fm.match_date, IFNULL(mr.name,'-') AS round_code, CONCAT_WS(':',fm.nr_goals_home_team,fm.nr_goals_away_team) AS score, t2.ground
FROM football_match fm INNER JOIN team t1 ON fm.id_away_team=t1.id_team INNER JOIN team t2 ON fm.id_home_team=t2.id_team LEFT JOIN match_round mr ON fm.round_code=mr.code;

-- Player Contract History in Premier League

CREATE OR REPLACE VIEW playerContractHistory AS
SELECT id_contract, team_name, player_name, weeks, weeks*salary AS total_salary, status_code, start_date, end_date
FROM(
SELECT c.id_contract, t.team_name, CONCAT_WS(' ',p.first_name, UPPER(p.last_name)) AS player_name, CEIL(DATEDIFF(c.update_date,c.start_date)/7) AS weeks, c.salary, c.status_code, c.start_date, c.end_date
FROM contract_history c INNER JOIN team t ON c.id_team=t.id_team INNER JOIN player p ON c.id_player=p.id_player) t;