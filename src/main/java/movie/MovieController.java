package movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

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

    @RequestMapping("/movie")
    public Movies getList(@RequestParam(name = "page", required = false) Integer page) {
        StringBuilder url = new StringBuilder()
                .append(accessUri)
                .append("/movie/popular?api_key=")
                .append(apiKey);
        if (page != null && page > 1) {
            url.append("&page=").append(page);
        }
        RestTemplate restTemplate = new RestTemplate();
        Movies movies = restTemplate.getForObject(url.toString(), Movies.class);
        return movies;
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

    @RequestMapping("/rating/{genre}")
    public ObjectNode getRating(@PathVariable Long genre) {

        // Send a message
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(genre);
            }
        };
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        System.out.println("Sending a new message.");
        jmsTemplate.send("rating-genre", messageCreator);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("status", "Proccess has been started");
        return node;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ObjectNode error() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("error", "No such element found");
        return node;
    }
}
