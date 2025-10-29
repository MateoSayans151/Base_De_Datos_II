package utilities;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Config instance;
    private static Properties properties;

    private Config() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("application.properties no encontrado");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public String getProperty (String key) {
        return properties.getProperty(key);
    }


    }