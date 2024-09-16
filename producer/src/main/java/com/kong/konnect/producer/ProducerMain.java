package com.kong.konnect.producer;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ProducerMain {

    private static final Logger logger = LoggerFactory.getLogger(ProducerMain.class);


    public static void main(String[] args) {
        IProducer producerService = new ProducerService();
        String activeProfile = System.getProperty("env", "dev");
        String propertiesFile = String.format("application-%s.properties", activeProfile);

        try {

            Properties props = new Properties();

            try (InputStream input = ProducerMain.class.getClassLoader().getResourceAsStream(propertiesFile)) {
                if (input == null) {
                    logger.error("Sorry, unable to find {}", propertiesFile);
                    return;
                }
                props.load(input);
            }
            catch (Exception e){
                e.printStackTrace();
            }


            String topic = props.getProperty("kafka.topic");
            String jsonlFilePath = props.getProperty("kafka.jsonl.file");
            try (InputStream fileInputStream = ProducerMain.class.getClassLoader().getResourceAsStream(jsonlFilePath)) {
                if (fileInputStream == null) {
                    logger.error("Unable to find jsonl file to publish: {}", jsonlFilePath);
                    return;
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String key = "cdc-key";
                        producerService.sendMessage(topic, key, line);
                    }
                }
            }

            producerService.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


