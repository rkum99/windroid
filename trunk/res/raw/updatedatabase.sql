-- Update Database Script 
-- 
-- $Id
-- 

DROP TABLE IF EXISTS spot;
DROP TABLE IF EXISTS continent;
DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS region;
DROP TABLE IF EXISTS selected;

CREATE TABLE IF NOT EXISTS internal (id TEXT PRIMARY KEY, value text);
CREATE TABLE IF NOT EXISTS spot (_id INTEGER PRIMARY KEY AUTOINCREMENT, spotid TEXT NOT NULL, continentid TEXT NOT NULL, countryid TEXT NOT NULL, regionid TEXT NOT NULL, name TEXT NOT NULL, keyword TEXT NOT NULL, superforecast BOOLEAN, report BOOLEAN, forecast BOOLEAN, statistic BOOLEAN, wavereport BOOLEAN, waveforecast BOOLEAN);
CREATE TABLE IF NOT EXISTS continent (_id INTEGER PRIMARY KEY AUTOINCREMENT ,id INTEGER, name TEXT);
CREATE TABLE IF NOT EXISTS country (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, continentid TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS region (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, countryid TEXT NOT NULL);

CREATE INDEX IF NOT EXISTS spotindex ON spot (spotid);
CREATE INDEX IF NOT EXISTS countryindex ON country (id);
CREATE INDEX IF NOT EXISTS regionindex ON region (id);
CREATE INDEX IF NOT EXISTS continentindex ON continent (id);

CREATE INDEX IF NOT EXISTS conid ON continent (id);
CREATE INDEX IF NOT EXISTS regid ON region (id);
CREATE INDEX IF NOT EXISTS coid ON country (id);

--  selected table

CREATE TABLE IF NOT EXISTS selected (_id INTEGER PRIMARY KEY AUTOINCREMENT, spotid text NOT NULL, activ BOOLEAN, usedirection BOOLEAN, starting TEXT, till TEXT, windmeasure TEXT NOT NULL, minwind INTEGER, maxwind INTEGER); 
CREATE INDEX IF NOT EXISTS selectedsid ON selected (spotid);

--  Preferences Table
CREATE TABLE IF NOT EXISTS preferences (_id INTEGER PRIMARY KEY AUTOINCREMENT, key TEXT NOT NULL, value TEXT);
-- Insert Values into table so that update will work on preferences
INSERT INTO preferences ('key','value') VALUES ('spot_winddirection_to','n/a');
INSERT INTO preferences ('key','value') VALUES ('spot_station_keyword','alger-port');
INSERT INTO preferences ('key','value') VALUES ('vibrate_on_alarm','false');
INSERT INTO preferences ('key','value') VALUES ('spot_station_id','dz2');
INSERT INTO preferences ('key','value') VALUES ('spot_station_has_statistic','true');
INSERT INTO preferences ('key','value') VALUES ('launch_on_boot','true');
INSERT INTO preferences ('key','value') VALUES ('spot_windspeed_max','63');
INSERT INTO preferences ('key','value') VALUES ('warn_when_update_failed','false');
INSERT INTO preferences ('key','value') VALUES ('spot_station_has_superforecast','true');
INSERT INTO preferences ('key','value') VALUES ('music_on_alarm','false');
INSERT INTO preferences ('key','value') VALUES ('spot_winddirection_from','n/a');
INSERT INTO preferences ('key','value') VALUES ('spot_station_name','Alger-Port');
INSERT INTO preferences ('key','value') VALUES ('preferred_continent','North America');
INSERT INTO preferences ('key','value') VALUES ('spot_windspeed_min','0');
INSERT INTO preferences ('key','value') VALUES ('update_while_roaming','false');
INSERT INTO preferences ('key','value') VALUES ('spot_preferred_unit','kts');
