package lu.lusis.demo;

import com.vaadin.flow.spring.annotation.SpringComponent;
import lu.lusis.demo.backend.data.Message;
import lu.lusis.demo.backend.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * Génére les données pour la démo
 */
@SpringComponent
public class DataGenerator {

    private Logger logger = LoggerFactory.getLogger(DataGenerator.class);

    private final MessageRepository messageRepository;

    private String[] fromStrings = {"Dupont Antoine","Doe John","Clinton Shirley"};


    private String[] subjectStrings = {"Hello","New Request","Close request", "Reopen request"};


    @Value( "${custom.messageSize}" )
    private int messageSize;

    public DataGenerator(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    @PostConstruct
    private void init(){
        logger.info("DataGenerator init()");
        if (messageRepository.count() == 0) {
            logger.warn("Message empty - Generate messages");
            // add some data in db
            for (int i = 0; i < messageSize; i++) {
                messageRepository.save(new Message(fromStrings[i%3],subjectStrings[i%4],(i%7==0),(i%5==0),(i%2==0)));
            }
        }
    }
}