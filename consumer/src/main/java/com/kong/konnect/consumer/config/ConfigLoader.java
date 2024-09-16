package com.kong.konnect.consumer.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * generic properties configuration loader class
 */
public class ConfigLoader {

    private static final Properties properties = new Properties();
    private static final String activeProfile = System.getProperty("env", "dev");  // Default to 'dev' if not set
    private static final String propertiesFile = String.format("application-%s.properties", activeProfile);

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(propertiesFile)) {
            if (input == null) {
                throw new RuntimeException("Unable to find application-dev.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading application-dev.properties", ex);
        }
    }

    /**
     * returns the property string
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
