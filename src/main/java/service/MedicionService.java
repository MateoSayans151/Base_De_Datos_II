package service;

import entity.Medicion;
import org.springframework.stereotype.Service;
import repository.cassandra.MedicionRepository;

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

    // OBTENER MEDICIÓN POR ID
    public Medicion getById(String id) {
        return repo.getMeasurement(id);
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

    public List<Medicion> getByFechaAndCountryBetween(LocalDateTime from, LocalDateTime until, String country,String city, String state) {
        List<Medicion> measurements = new ArrayList<>();
        List<Medicion> measurementsRange = repo.getMeasurementsBetwenDates(from, until);
        for (Medicion m : measurementsRange) {
            if (m.getSensor().getCiudad().equalsIgnoreCase(country)) {
                measurements.add(m);
            }
        }
        return measurements;
    }

    public Double getAverageHumidityBetweenDates(String city, LocalDateTime from, LocalDateTime until) {
        List<Medicion> measurements = new ArrayList<>();
        List<Medicion> measurementsRange = repo.getMeasurementsBetwenDates(from, until);
        for (Medicion m : measurementsRange) {
            if (m.getSensor().getCiudad().equalsIgnoreCase(city)) {
                measurements.add(m);
            }


        }
        var avgHum = measurements.stream()
                .mapToDouble(mes -> mes.getHumedad())
                .average()
                .orElse(0.0);


        return avgHum;
    }
    public Double getAverageTemperatureBetweenDates(String city, LocalDateTime from, LocalDateTime until) {
        List<Medicion> measurements = new ArrayList<>();
        List<Medicion> measurementsRange = repo.getMeasurementsBetwenDates(from, until);
        for (Medicion m : measurementsRange) {
            if (m.getSensor().getCiudad().equalsIgnoreCase(city)) {
                measurements.add(m);
            }
        }
        var avgTemp = measurements.stream()
                .mapToDouble(mes -> mes.getTemperatura())
                .average()
                .orElse(0.0);
        return avgTemp;
    }
}

