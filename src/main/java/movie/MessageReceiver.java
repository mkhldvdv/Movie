package movie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;

/**
 * Created by mikhail.davydov on 2016/7/12.
 */

@Component
public class MessageReceiver {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final int INIT_VALUE = 0;
    @Autowired
    private Rate rate;

    @Value("${themoviedb.accessUri}")
    private String accessUri;

    @Value("${themoviedb.apiKey}")
    private String apiKey;

    @Value("${themoviedb.pageLimit}")
    private Integer pageLimit;

    @JmsListener(destination = "rating")
    public synchronized void getRating(Long id) throws RestClientException {
        // check if it was already called for the defined jenre
        if (rate.getRatingProccessed().get(id) != null) {
            return;
        }

        log.info("Message received: " + id);

        StringBuilder url = new StringBuilder()
                .append(accessUri)
                .append("/genre/")
                .append(id)
                .append("/movies?api_key=").append(apiKey);
        RestTemplate restTemplate = new RestTemplate();
        Movies movies;
        try {
            movies = restTemplate.getForObject(url.toString(), Movies.class);
        } catch (RestClientException e) {
            rate.getRatingErrors().put(id, e.getMessage());
            return;
        }
        int page = movies.getPage();
        int totalPages = pageLimit;
//        int totalResults = movies.getTotalResults();
        int totalResults = INIT_VALUE;
        float rating = INIT_VALUE;
        // proccessing
        while (page < totalPages) {
            rate.getRatingProccessing().put(id, page * 100 / totalPages);
            rating += movies.getMovies().stream().mapToDouble(Movie::getVoteAverage).sum();
            // because of the request limit per second
            totalResults += movies.getMovies().stream().count();
            //
            page++;
            url.append("&page=").append(page);
            try {
                movies = restTemplate.getForObject(url.toString(), Movies.class);
            } catch (RestClientException e) {
                rate.getRatingErrors().put(id, e.getMessage());
                return;
            }
        }
        // add processed results
        rate.getRatingProccessed().put(id, Float.valueOf(new DecimalFormat("##,##").format(rating / totalResults)));
        // remove from proccessing
        rate.getRatingProccessing().remove(id);
    }

    @Scheduled(cron = "0 0 * * * *")
    private void clearCache() {
        rate.getRatingProccessed().clear();
    }
}
