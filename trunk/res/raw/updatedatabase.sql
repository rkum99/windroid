-- Update Database Script 
-- 
-- $Id
-- 

DROP TABLE IF EXISTS spot;
DROP TABLE IF EXISTS continent;
DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS region;

CREATE TABLE IF NOT EXISTS internal (id TEXT PRIMARY KEY, value text);
CREATE TABLE IF NOT EXISTS spot (_id INTEGER PRIMARY KEY AUTOINCREMENT, spotid TEXT NOT NULL, continentid TEXT NOT NULL, countryid TEXT NOT NULL, regionid TEXT NOT NULL, name TEXT NOT NULL, keyword TEXT NOT NULL, superforecast BOOLEAN, forecast BOOLEAN, statistic BOOLEAN, wavereport BOOLEAN, waveforecast BOOLEAN);
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

CREATE TABLE IF NOT EXISTS selected (_id INTEGER PRIMARY KEY AUTOINCREMENT,spotid text NOT NULL,activ BOOLEAN);
CREATE INDEX IF NOT EXISTS selectedsid ON selected (spotid);

--  Preferences Table
CREATE TABLE IF NOT EXISTS preferences (_id INTEGER PRIMARY KEY AUTOINCREMENT, key TEXT NOT NULL, value TEXT);
-- Insert Values into table so that update will work on preferences
INSERT INTO preferences ('key','value') VALUES ('warn_when_update_failed','test');
INSERT INTO preferences ('key','value') VALUES ('launch_on_boot','test1');
INSERT INTO preferences ('key','value') VALUES ('update_while_roaming','test2');
INSERT INTO preferences ('key','value') VALUES ('music_on_alarm','test3');