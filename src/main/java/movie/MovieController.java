package movie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

/**
 * Created by mikhail.davydov on 2016/7/12.
 */

@RestController
@EnableJms
public class MovieController {

    @Autowired
    ConfigurableApplicationContext context;

    @Value("${themoviedb.accessUri}")
    private String accessUri;

    @Value("${themoviedb.apiKey}")
    private String apiKey;

    @RequestMapping("/genre/{id}")
    public List<Movie> getList(@PathVariable Long id) {
        if (id == null) {
            throw new RuntimeException();
        }
        RestTemplate restTemplate = new RestTemplate();
        Movies movies = restTemplate.getForObject(accessUri + "/genre/" + id + "/movies?api_key=" + apiKey, Movies.class);
        return movies.getMovies();
    }

    @RequestMapping("/movie/{id}")
    public Movie getOne(@PathVariable Long id) {
        if (id == null) {
            throw new RuntimeException();
        }
        RestTemplate restTemplate = new RestTemplate();
        Movie movie = restTemplate.getForObject(accessUri + "/movie/" + id + "?api_key=" + apiKey, Movie.class);
        return movie;
    }

    @RequestMapping("/rating")
    public void getRating() {

        // Send a message
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("ping!");
            }
        };
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        System.out.println("Sending a new message.");
        jmsTemplate.send("rating-destination", messageCreator);
    }

    @RequestMapping("/url")
    public String getUrl() {
        return accessUri;
    }

    @ExceptionHandler(Exception.class)
    public String error() {
        return "No such movie found";
    }
}
