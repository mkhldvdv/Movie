package movie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by mikhail.davydov on 2016/7/12.
 */

@RestController
public class MovieController {

    public static final int SUCCESS = 1;

    @Value("${themoviedb.accessUri}")
    private String accessUri;

    @Value("${themoviedb.apiKey}")
    private String apiKey;

    @RequestMapping("/movie")
    public List<Movie> getList() {
        RestTemplate restTemplate = new RestTemplate();
        Movies movies = restTemplate.getForObject(accessUri + "/movie/popular?api_key=" + apiKey, Movies.class);
        return movies.getMovies();
    }

    @RequestMapping("/movie/{id}")
    public Movie getOne(@PathVariable Long id) {
        RestTemplate restTemplate = new RestTemplate();
        Movie movie = restTemplate.getForObject(accessUri + "/movie/" + id + "?api_key=" + apiKey, Movie.class);
        if (movie.getStatusCode() != SUCCESS) {
            throw new NotFoundException();
        }
        return movie;
    }

    @RequestMapping("/rating")
    public Float getRating() {
        return null;
    }

    @RequestMapping("/url")
    public String getUrl() {
        return accessUri;
    }

    @ExceptionHandler(NotFoundException.class)
    public String error(NotFoundException e) {
        return e.getMessage();
    }
}
