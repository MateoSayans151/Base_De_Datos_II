CREATE KEYSPACE baseCass with replication= {'class':'SimpleStrategy','replication_factor':1};
USE baseCass;

CREATE TABLE Mediciones(
idMedicion uuid,
idSensor uuid,
cod int,
tipo text,
latitud double,
longitud double,
ciudad text,
pais text,
estado text,
fechaIni text,
fechayHora timestamp,
temperatura int,
humedad int,
PRIMARY KEY((idMedicion),idSensor,fechayHora)
);

CREATE TABLE Alertas(
idAlerta uuid,
idSensor uuid,
tipo text,
fechayHora timestamp,
descripcion text,
estado text,
PRIMARY KEY((idAlerta),idSensor,fechayHora)
);

