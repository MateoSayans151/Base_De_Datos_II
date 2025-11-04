CREATE KEYSPACE tpo with replication= {'class':'SimpleStrategy','replication_factor':1};
USE tpo;

CREATE TABLE Mediciones(
id uuid,
idSensor int,
cod text,
tipo text,
latitud double,
longitud double,
ciudad text,
pais text,
estado text,
fechayHora timestamp,
temperatura double,
humedad double,
PRIMARY KEY(idSensor,fechayHora)
)WITH CLUSTERING ORDER BY (fechayHora DESC);

CREATE TABLE Alertas(
id uuid,
idSensor int,
cod text,
tipo text,
latitud double,
longitud double,
ciudad text,
pais text,
estado text,
fechayHora timestamp,
descripcion text,
PRIMARY KEY((id),idSensor,fechayHora)
);

