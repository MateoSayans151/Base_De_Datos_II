CREATE KEYSPACE tpo with replication= {'class':'SimpleStrategy','replication_factor':1};
USE tpo;

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
fechayHora timestamp,
temperatura int,
humedad int,
PRIMARY KEY((idMedicion),idSensor,fechayHora)
)WITH CLUSTERING ORDER BY (idSensor ASC,fechayHora DESC);

CREATE TABLE Alertas(
idAlerta uuid,
idSensor uuid,
tipo text,
fechayHora timestamp,
descripcion text,
estado text,
PRIMARY KEY((idAlerta),idSensor,fechayHora)
);

