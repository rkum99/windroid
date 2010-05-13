-- Create Database Script
-- 
-- $Id
-- 
-- 
-- Tables
-- 
CREATE TABLE IF NOT EXISTS internal (_id INTEGER PRIMARY KEY AUTOINCREMENT, keyword TEXT NOT NULL , value text);
CREATE TABLE IF NOT EXISTS spot (_id INTEGER PRIMARY KEY AUTOINCREMENT, spotid TEXT NOT NULL, continentid TEXT NOT NULL, countryid TEXT NOT NULL, regionid TEXT NOT NULL, name TEXT NOT NULL, keyword TEXT NOT NULL, superforecast BOOLEAN, report BOOLEAN, forecast BOOLEAN, statistic BOOLEAN, wavereport BOOLEAN, waveforecast BOOLEAN, longitude REAL, latitude REAL);
CREATE TABLE IF NOT EXISTS continent (_id INTEGER PRIMARY KEY AUTOINCREMENT ,id INTEGER, name TEXT);
CREATE TABLE IF NOT EXISTS country (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, continentid TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS region (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, countryid TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS forecast_releation (_id INTEGER PRIMARY KEY AUTOINCREMENT, selectedid INTEGER, forecastid INTEGER UNIQUE);
CREATE TABLE IF NOT EXISTS forecast (_id INTEGER PRIMARY KEY AUTOINCREMENT, date LONG, time INTEGER,  wave_period FLOAT,wave_period_unit TEXT,  wind_direction TEXT, wave_direction TEXT, precipitation FLOAT, precipitation_unit TEXT, air_pressure FLOAT, air_pressure_unit TEXT, wind_gusts FLOAT, wind_gusts_unit TEXT, water_temperature FLOAT,water_temperature_unit TEXT, air_temperature FLOAT, air_temperature_unit TEXT, wave_height FLOAT, wave_height_unit TEXT, clouds TEXT, wind_speed FLOAT,wind_speed_unit TEXT);

CREATE INDEX spotindex ON spot (spotid);
CREATE INDEX countryindex ON country (id);
CREATE INDEX regionindex ON region (id);
CREATE INDEX continentindex ON continent (id);

CREATE INDEX conid ON continent (id); 
CREATE INDEX regid ON region (id); 
CREATE INDEX coid ON country (id); 
--  selected
CREATE TABLE IF NOT EXISTS selected (_id INTEGER PRIMARY KEY AUTOINCREMENT, spotid text NOT NULL, activ BOOLEAN, lastupdate LONG, updatefailed BOOLEAN , usedirection BOOLEAN, starting TEXT, till TEXT, windmeasure TEXT NOT NULL, minwind INTEGER, maxwind INTEGER); 
CREATE INDEX IF NOT EXISTS selectedid ON selected (spotid); 
-- schedule 
-- CREATE TABLE IF NOT EXISTS schedule (_id INTEGER PRIMARY KEY AUTOINCREMENT, selectedid INTEGER);
-- CREATE INDEX scheduleid ON schedule (selectedid); 
-- repeat
CREATE TABLE IF NOT EXISTS repeat (_id INTEGER PRIMARY KEY AUTOINCREMENT, weekday INTEGER, daytime LONG, activ BOOLEAN);
-- schedule_repeat_relation
CREATE TABLE IF NOT EXISTS schedule_repeat_relation (_id INTEGER PRIMARY KEY AUTOINCREMENT, selectedid INTEGER, repeatid INTEGER);
-- 
-- Triggers
-- 
-- Delete selected cacade trigger
CREATE TRIGGER IF NOT EXISTS selected_cacade_delete AFTER  DELETE ON selected            
    BEGIN                                                                 
                                                                          
    DELETE  FROM forecast_releation WHERE selectedid=old._id;
    END
-- Delete forecast casade trigger after delete on forecast_releation
CREATE TRIGGER IF NOT EXISTS forecast_releation_cacade_delete AFTER  DELETE ON forecast_releation            
     BEGIN                                                                 
                                                                          
     DELETE  FROM forecast WHERE _id=old.forecastid;
     END
-- Preferences
CREATE TABLE IF NOT EXISTS preferences (_id INTEGER PRIMARY KEY AUTOINCREMENT, key TEXT UNIQUE NOT NULL, value TEXT);
-- Pre selected Values
INSERT INTO preferences ('key','value') VALUES ('alarmtone','');
INSERT INTO preferences ('key','value') VALUES ('vibrate_on_alarm','false');
INSERT INTO preferences ('key','value') VALUES ('preferred_unit','knt');
INSERT INTO preferences ('key','value') VALUES ('update_while_roaming','false');
INSERT INTO preferences ('key','value') VALUES ('launch_on_boot','true');
INSERT INTO preferences ('key','value') VALUES ('preferred_continent','North America');
INSERT INTO preferences ('key','value') VALUES ('warn_when_update_failed','false');
INSERT INTO preferences ('key','value') VALUES ('music_on_alarm','false');
INSERT INTO preferences ('key','value') VALUES ('isLicenceAccepted','false');
