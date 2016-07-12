package movie;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Created by mikhail.davydov on 2016/7/12.
 */

@Component
public class MessageReceiver {

    /**
     * When you receive a message, print it out, then shut down the application.
     * Finally, clean up any ActiveMQ server stuff.
     */
    @JmsListener(destination = "rating-destination")
    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
//        FileSystemUtils.deleteRecursively(new File("activemq-data"));
    }
}
