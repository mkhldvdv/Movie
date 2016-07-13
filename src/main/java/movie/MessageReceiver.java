package movie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * Created by mikhail.davydov on 2016/7/12.
 */

@Component
public class MessageReceiver {

    @Value("${themoviedb.accessUri}")
    private String accessUri;

    @Value("${themoviedb.apiKey}")
    private String apiKey;

    /**
     * When you receive a message, print it out, then shut down the application.
     * Finally, clean up any ActiveMQ server stuff.
     */
    @JmsListener(destination = "rating-genre")
    public Movies receiveMessage(Long id) {

        RestTemplate restTemplate = new RestTemplate();
        Movies movies = restTemplate.getForObject(accessUri + "/genre/" + id + "/movies?api_key=" + apiKey, Movies.class);
        FileSystemUtils.deleteRecursively(new File("activemq-data"));

        return movies;
    }
}
