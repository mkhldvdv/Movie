package movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mikhail.davydov on 2016/7/12.
 */

@Component
public class MessageReceiver {

    public static final int LIMIT = 5;
    public static final int INIT_VALUE = 0;
    @Autowired
    private Rating rate;

    @Value("${themoviedb.accessUri}")
    private String accessUri;

    @Value("${themoviedb.apiKey}")
    private String apiKey;

    @JmsListener(destination = "rating.in")
    public void getRating(Long id) {
        System.out.println("Message received: " + id);

        StringBuilder url = new StringBuilder()
                .append(accessUri)
                .append("/genre/")
                .append(id)
                .append("/movies?api_key=").append(apiKey);
        RestTemplate restTemplate = new RestTemplate();
        Movies movies = restTemplate.getForObject(url.toString(), Movies.class);
        int page = movies.getPage();
        int totalPages = LIMIT;
//        int totalResults = movies.getTotalResults();
        int totalResults = INIT_VALUE;
        float rating = INIT_VALUE;
        // proccessing
        while (page < totalPages) {
            rate.getRatingProccessing().put(id, page * 100 / totalPages);
            rating += movies.getMovies().stream().mapToDouble(Movie::getVoteAverage).sum();
            // beacuse of the request limit per second
            totalResults += movies.getMovies().stream().count();
            page++;
            url.append("&page=").append(page);
            movies = restTemplate.getForObject(url.toString(), Movies.class);
        }
        // add processed results
        rate.getRatingProccessed().put(id, Float.valueOf(new DecimalFormat("##,##").format(rating / totalResults)));
        // remove from proccessing
        rate.getRatingProccessing().remove(id);
    }
}
