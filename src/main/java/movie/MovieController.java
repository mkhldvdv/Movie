package movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by mikhail.davydov on 2016/7/12.
 */

@RestController
@EnableJms
public class MovieController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    Rate rate;

    @Value("${themoviedb.accessUri}")
    private String accessUri;

    @Value("${themoviedb.apiKey}")
    private String apiKey;

    @RequestMapping("/movie")
    public HttpEntity<Movies> getList(@RequestParam(name = "page", required = false) Integer page) {
        StringBuilder url = new StringBuilder()
                .append(accessUri)
                .append("/movie/popular?api_key=")
                .append(apiKey);
        if (page != null && page > 1) {
            url.append("&page=").append(page);
        }
        RestTemplate restTemplate = new RestTemplate();
        Movies movies = restTemplate.getForObject(url.toString(), Movies.class);
        return new HttpEntity<>(movies);
    }

    @RequestMapping("/movie/{id}")
    public HttpEntity<Movie> getOne(@PathVariable Long id) {
        if (id == null) {
            throw new RuntimeException();
        }
        RestTemplate restTemplate = new RestTemplate();

        Movie movie = restTemplate.getForObject(accessUri + "/movie/" + id + "?api_key=" + apiKey, Movie.class);
        return new HttpEntity<>(movie);
    }

    @RequestMapping("/rating/{id}")
    public ObjectNode getRating(@PathVariable Long id) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        // check if rate exists
        if (rate.getRatingProccessed().get(id) != null) {
            node.put("status", "request completed");
            node.put("message", "Average rating: " + rate.getRatingProccessed().get(id));
        } else if (rate.getRatingProccessing().get(id) != null) {
            node.put("status", "request in progress");
            node.put("message", "Request is in progress: " + rate.getRatingProccessing().get(id) + "% done");
        } else {
            // Send new request
            MessageCreator messageCreator = session -> session.createObjectMessage(id);
            log.info("Sending a new message: " + id);
            jmsTemplate.send("rating", messageCreator);
            node.put("status", "request accepted");
            node.put("message", "Proccess has been started");
        }
        return node;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ObjectNode errorInputHandler(Exception e) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("status", "error");
        node.put("message", e.getMessage());
        return node;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public void errorHttpClientHandler(HttpServletResponse response, HttpClientErrorException e) throws IOException {
        response.sendError(e.getStatusCode().value(), "Root cause received from original resource: " + e.getMessage());
    }
}
