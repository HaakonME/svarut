DROP TABLE "FORSENDELSESARKIV" IF EXISTS;
CREATE TABLE "FORSENDELSESARKIV" ( ID VARCHAR(60) NOT NULL,
 FODSELSNR CHAR(11),
  ORGNR INTEGER,
  NAVN VARCHAR(50),
  ADRESSE1 VARCHAR(100),
  ADRESSE2 VARCHAR(100),
  ADRESSE3 VARCHAR(100) ,
  POSTNR VARCHAR(10) ,
  POSTSTED VARCHAR(50) ,
  LAND VARCHAR(100) ,
  AVSENDER_NAVN VARCHAR(50),
  AVSENDER_ADRESSE1 VARCHAR(100),
  AVSENDER_ADRESSE2 VARCHAR(100),
  AVSENDER_ADRESSE3 VARCHAR(100) ,
  AVSENDER_POSTNR VARCHAR(10) ,
  AVSENDER_POSTSTED VARCHAR(50) ,
  TITTEL VARCHAR(100) ,
  MELDING VARCHAR(300),
  LEST DATE,
  SENDT TIMESTAMP,
  NORGEDOTNO DATE,
  UTSKREVET DATE,
  APPID VARCHAR(20),
  PRINT_ID VARCHAR(60),
  FORSENDELSES_MATE VARCHAR(60),
  EPOST VARCHAR(100),
  REPLY_TO VARCHAR(100),
  EPOST_SENDT DATE,
  NESTE_FORSOK TIMESTAMP,
  STOPPET DATE,
  PRINT_FARGE CHAR(1),
  ALTINN_SENDT DATE,
  ANTALLSIDER NUMERIC(4),
  ANTALLSIDERPOSTLAGT NUMERIC(4),
  TIDSPUNKTPOSTLAGT TIMESTAMP,
  ANSVARSSTED VARCHAR(255),
  ALTINN_RECEIPT_ID INTEGER,
  KONTERINGKODE VARCHAR(30));