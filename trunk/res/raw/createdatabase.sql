-- 
-- $Id
-- 
DROP TABLE IF EXISTS spot;
DROP TABLE IF EXISTS continent;
DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS region;
DROP TABLE IF EXISTS selected;

CREATE TABLE IF NOT EXISTS internal (id TEXT PRIMARY KEY, value text);
CREATE TABLE IF NOT EXISTS spot (_id INTEGER PRIMARY KEY AUTOINCREMENT, spotid TEXT NOT NULL, continentid TEXT NOT NULL, countryid TEXT NOT NULL, regionid TEXT NOT NULL, name TEXT NOT NULL, keyword TEXT NOT NULL, superforecast BOOLEAN, forecast BOOLEAN, statistic BOOLEAN, wavereport BOOLEAN, waveforecast BOOLEAN);
CREATE TABLE IF NOT EXISTS continent (_id INTEGER PRIMARY KEY AUTOINCREMENT ,id INTEGER, name TEXT);
CREATE TABLE IF NOT EXISTS country (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, continentid TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS region (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, countryid TEXT NOT NULL);

CREATE INDEX spotindex ON spot (spotid);
CREATE INDEX countryindex ON country (id);
CREATE INDEX regionindex ON region (id);
CREATE INDEX continentindex ON continent (id);

CREATE INDEX conid ON continent (id); 
CREATE INDEX regid ON region (id); 
CREATE INDEX coid ON country (id); 

CREATE TABLE IF NOT EXISTS selected (_id INTEGER PRIMARY KEY AUTOINCREMENT,spotid text NOT NULL,activ BOOLEAN); 
CREATE INDEX IF NOT EXISTS selectedsid ON selected (spotid); 
