-- Update Database Script 
-- 
-- $Id
-- 
-- Tables
-- 
CREATE TABLE IF NOT EXISTS internal (_id INTEGER PRIMARY KEY AUTOINCREMENT, keyword TEXT NOT NULL , value text);
CREATE TABLE IF NOT EXISTS spot (_id INTEGER PRIMARY KEY AUTOINCREMENT, spotid TEXT NOT NULL, continentid TEXT NOT NULL, countryid TEXT NOT NULL, regionid TEXT NOT NULL, name TEXT NOT NULL, keyword TEXT NOT NULL, superforecast BOOLEAN, report BOOLEAN, forecast BOOLEAN, statistic BOOLEAN, wavereport BOOLEAN, waveforecast BOOLEAN);
CREATE TABLE IF NOT EXISTS continent (_id INTEGER PRIMARY KEY AUTOINCREMENT ,id INTEGER, name TEXT);
CREATE TABLE IF NOT EXISTS country (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, continentid TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS region (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, countryid TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS forecast_releation (_id INTEGER PRIMARY KEY AUTOINCREMENT, updatefailed BOOLEAN, selectedid INTEGER, forecastid INTEGER UNIQUE);
CREATE TABLE IF NOT EXISTS forecast (_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, time INTEGER,  wave_period FLOAT,wave_period_unit TEXT,  wind_direction TEXT, wave_direction TEXT, precipitation FLOAT, precipitation_unit TEXT, air_pressure FLOAT, air_pressure_unit TEXT, wind_gusts FLOAT, wind_gusts_unit TEXT, water_temperature FLOAT,water_temperature_unit TEXT, air_temperature FLOAT, air_temperature_unit TEXT, wave_height FLOAT, wave_height_unit TEXT, clouds TEXT, wind_speed FLOAT,wind_speed_unit TEXT);

CREATE INDEX IF NOT EXISTS spotindex ON spot (spotid);
CREATE INDEX IF NOT EXISTS countryindex ON country (id);
CREATE INDEX IF NOT EXISTS regionindex ON region (id);
CREATE INDEX IF NOT EXISTS continentindex ON continent (id);

CREATE INDEX IF NOT EXISTS conid ON continent (id); 
CREATE INDEX IF NOT EXISTS regid ON region (id); 
CREATE INDEX IF NOT EXISTS coid ON country (id); 
--  selected
CREATE TABLE IF NOT EXISTS selected (_id INTEGER PRIMARY KEY AUTOINCREMENT, spotid text NOT NULL, activ BOOLEAN, usedirection BOOLEAN, starting TEXT, till TEXT, windmeasure TEXT NOT NULL, minwind INTEGER, maxwind INTEGER); 
CREATE INDEX IF NOT EXISTS selectedid ON selected (spotid); 

--  schedule 
-- CREATE TABLE IF NOT EXISTS schedule (_id INTEGER PRIMARY KEY AUTOINCREMENT, selectedid INTEGER);
-- CREATE INDEX scheduleid ON schedule (selectedid); 
-- repeat
CREATE TABLE IF NOT EXISTS repeat (_id INTEGER PRIMARY KEY AUTOINCREMENT, weekday INTEGER, daytime LONG, activ BOOLEAN);
-- schedule_repeat_relation
CREATE TABLE IF NOT EXISTS schedule_repeat_relation (_id INTEGER PRIMARY KEY AUTOINCREMENT, selectedid INTEGER, repeatid INTEGER);

-- 
-- Triggers
-- 
-- CREATE TRIGGER IF NOT EXISTS repeat_insert_trigger AFTER  INSERT ON repeat            
--      BEGIN                                                                 
--                                                                           
--      INSERT  INTO schedule_repeat_relation (scheduleid, repeatid) values (new.scheduleid,new._id);
--      END
CREATE TABLE IF NOT EXISTS preferences (_id INTEGER PRIMARY KEY AUTOINCREMENT, key TEXT UNIQUE NOT NULL, value TEXT);
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
