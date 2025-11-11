package service;

import entity.Medicion;
import entity.Sensor;
import exceptions.ErrorConectionMongoException;
import org.springframework.stereotype.Service;
import repository.cassandra.MedicionRepository;
import repository.mongo.SensorRepository;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MedicionService {

    public static MedicionService instance;
    private final MedicionRepository repo = MedicionRepository.getInstance();

    public MedicionService() {
    }
    public static MedicionService getInstance() {
        if (instance == null)
            instance = new MedicionService();
        return instance;
    }

    // LISTAR TODAS LAS MEDICIONES
    public List<Medicion> getAll() {
        return repo.getMeasurements();
    }

    // CREAR MEDICIÓN
    public void create(Medicion medicion) {
        repo.insertMeasurement(medicion);
    }


    // OBTENER MEDICIONES POR SENSOR
    public List<Medicion> getBySensorId(int sensorId) {
        return repo.getMeasurementBySensor(sensorId);
    }

    // OBTENER MEDICIONES EN UN RANGO DE FECHAS

    public Double getMinByCity(String city) throws ErrorConectionMongoException {
        List<Sensor> sensors = SensorRepository.getInstance().getSensorsByCity(city);
        List<Medicion> measurements = new ArrayList<>();
        for (Sensor sensor : sensors) {
            List<Medicion> mess = repo.getMeasurements();
            for (Medicion m : mess) {
                if (m.getSensor().getCiudad().equalsIgnoreCase(city)) {
                    measurements.add(m);
                }

            }
        }
        Double minTemp = measurements.stream()
                .mapToDouble(Medicion::getTemperatura)
                .min()
                .orElse(Double.NaN);

        return minTemp;
    }
    public Double getMaxByCity(String city) throws ErrorConectionMongoException {
        List<Sensor> sensors = SensorRepository.getInstance().getSensorsByCity(city);
        List<Medicion> measurements = new ArrayList<>();
        for (Sensor sensor : sensors) {
            List<Medicion> mess = repo.getMeasurements();
            for (Medicion m : mess) {
                if (m.getSensor().getCiudad().equalsIgnoreCase(city)) {
                    measurements.add(m);
                }

            }
        }
        Double maxTemp = measurements.stream()
                .mapToDouble(Medicion::getTemperatura)
                .max()
                .orElse(Double.NaN);

        return maxTemp;
    }
    public Double getMinByCountry(String country) throws ErrorConectionMongoException {
        List<Sensor> sensors = SensorRepository.getInstance().getSensorsByCountry(country);
        List<Medicion> measurements = new ArrayList<>();
        for (Sensor sensor : sensors) {
            List<Medicion> mess = repo.getMeasurements();
            for (Medicion m : mess) {
                if (m.getSensor().getPais().equals(country)) {
                    measurements.add(m);
                }

            }
        }
        Double minTemp = measurements.stream()
                .mapToDouble(Medicion::getTemperatura)
                .min()
                .orElse(Double.NaN);

        return minTemp;
    }
    public Double getMaxByCountry(String country) throws ErrorConectionMongoException {
        List<Sensor> sensors = SensorRepository.getInstance().getSensorsByCountry(country);
        List<Medicion> measurements = new ArrayList<>();
        for (Sensor sensor : sensors) {
            List<Medicion> mess = repo.getMeasurements();
            for (Medicion m : mess) {
                if (m.getSensor().getPais().equals(country)) {
                    measurements.add(m);
                }

            }
        }
        Double maxTemp = measurements.stream()
                .mapToDouble(Medicion::getTemperatura)
                .max()
                .orElse(Double.NaN);

        return maxTemp;
    }


    public Double getAverageHumidityBetweenDatesByCity(String city, LocalDateTime from, LocalDateTime until) throws ErrorConectionMongoException {
        List<Sensor> sensors = SensorRepository.getInstance().getSensorsByCity(city);
        List<Medicion> measurements = new ArrayList<>();
        for (Sensor sensor : sensors) {
            List<Medicion> measurementsRange = repo.getMeasurementsBetwenDates(sensor.getId(),from, until);
            for (Medicion m : measurementsRange) {
                if (m.getSensor().getCiudad().equals(city)) {
                    measurements.add(m);
                }


            }
        }

        var avgHum = measurements.stream()
                .mapToDouble(mes -> mes.getHumedad() == null ? 0.0 : mes.getHumedad())
                .average()
                .orElse(0.0);


        return avgHum;
    }
    public Double getAverageTemperatureBetweenDatesByCity(String city, LocalDateTime from, LocalDateTime until) throws ErrorConectionMongoException {
        List<Sensor> sensors = SensorRepository.getInstance().getSensorsByCity(city);
        List<Medicion> measurements = new ArrayList<>();
        for (Sensor sensor : sensors) {
            List<Medicion> measurementsRange = repo.getMeasurementsBetwenDates(sensor.getId(),from, until);
            for (Medicion m : measurementsRange) {
                if (m.getSensor().getCiudad().equals(city)) {
                    measurements.add(m);
                }


            }
        }
        var avgTemp = measurements.stream()
                .mapToDouble(mes -> mes.getTemperatura() == null ? 0.0 : mes.getTemperatura())
                .average()
                .orElse(0.0);
        return avgTemp;
    }
    
    public Double getAverageHumidityBetweenDatesByCountry(String country, LocalDateTime from, LocalDateTime until) throws ErrorConectionMongoException {
        if (country == null || from == null || until == null) return 0.0;
        String normCountry = country.trim();

        List<Sensor> sensors = SensorRepository.getInstance().getSensorsByCountry(normCountry);
        if (sensors == null || sensors.isEmpty()) {
            return 0.0;
        }

        List<Medicion> measurements = new ArrayList<>();
        for (Sensor sensor : sensors) {
            List<Medicion> measurementsRange = repo.getMeasurementsBetwenDates(sensor.getId(), from, until);
            if (measurementsRange == null) continue;
            for (Medicion m : measurementsRange) {
                if (m == null || m.getSensor() == null || m.getSensor().getPais() == null) continue;
                if (m.getSensor().getPais().trim().equalsIgnoreCase(normCountry)) {
                    measurements.add(m);
                }
            }
        }

        return measurements.stream()
                .mapToDouble(mes -> mes.getHumedad() == null ? 0.0 : mes.getHumedad())
                .average()
                .orElse(0.0);
    }

    public Double getAverageTemperatureBetweenDatesByCountry(String country, LocalDateTime from, LocalDateTime until) throws ErrorConectionMongoException {
        if (country == null || from == null || until == null) return 0.0;
        String normCountry = country.trim();

        List<Sensor> sensors = SensorRepository.getInstance().getSensorsByCountry(normCountry);
        if (sensors == null || sensors.isEmpty()) {
            return 0.0;
        }

        List<Medicion> measurements = new ArrayList<>();
        for (Sensor sensor : sensors) {
            List<Medicion> measurementsRange = repo.getMeasurementsBetwenDates(sensor.getId(), from, until);
            if (measurementsRange == null) continue;
            for (Medicion m : measurementsRange) {
                if (m == null || m.getSensor() == null || m.getSensor().getPais() == null) continue;
                if (m.getSensor().getPais().trim().equalsIgnoreCase(normCountry)) {
                    measurements.add(m);
                }
            }
        }

        return measurements.stream()
                .mapToDouble(mes -> mes.getTemperatura() == null ? 0.0 : mes.getTemperatura())
                .average()
                .orElse(0.0);
    }


}

