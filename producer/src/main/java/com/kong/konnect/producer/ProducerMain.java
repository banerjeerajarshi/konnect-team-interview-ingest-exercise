package com.kong.konnect.producer;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * producer main class
 */
public class ProducerMain {

    private static final Logger logger = LoggerFactory.getLogger(ProducerMain.class);


    public static void main(String[] args) {
        IProducer producerService = new ProducerService();
        String activeProfile = System.getProperty("env", "dev");
        String propertiesFile = String.format("application-%s.properties", activeProfile);

        try {

            Properties props = new Properties();

            // load the props information from the properties file
            try (InputStream input = ProducerMain.class.getClassLoader().getResourceAsStream(propertiesFile)) {
                if (input == null) {
                    logger.error("Sorry, unable to find {}", propertiesFile);
                    return;
                }
                props.load(input);
            }
            catch (Exception e){
                logger.error("error occurred while processing properties file ", e);
            }


            String topic = props.getProperty("kafka.topic");
            String jsonlFilePath = props.getProperty("kafka.jsonl.file");
            // read the jsonl file as a fileinput stream
            try (InputStream fileInputStream = ProducerMain.class.getClassLoader().getResourceAsStream(jsonlFilePath)) {
                if (fileInputStream == null) {
                    logger.error("Unable to find jsonl file to publish: {}", jsonlFilePath);
                    return;
                }
                // use buffered reader to handle large files in an efficient way
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
                    String line;
                    // while the line we read from the file is not null
                    while ((line = reader.readLine()) != null) {
                        // use a message key if consumer wants to aggregate by key.
                        String key = "cdc-key";
                        // send the line (record) to kafka
                        producerService.sendMessage(topic, key, line);
                    }
                }
            }

            // close the producer once the file has been read and its contents dispatched
            producerService.close();
        } catch (Exception e) {
            logger.error("error occurred while producing records to kafka ", e);
        }
    }

}


