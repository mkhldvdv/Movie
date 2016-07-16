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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    @JmsListener(destination = "rating")
    public void getRating(Long id) throws InterruptedException {
        log.info("Message received: " + id);

        // check if it's already called for defined jenre
        Lock lock = new ReentrantLock();
        lock.lock();
        if (rate.getRatingProccessing().get(id) == null) {
            rate.getRatingProccessing().put(id, 0);
        } else {
            lock.unlock();
            return;
        }
        lock.unlock();

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
            Thread.sleep(10000);
            movies = restTemplate.getForObject(url.toString(), Movies.class);
        }
        int page = movies.getPage();
        int totalPages = movies.getTotalPages();
        int totalResults = movies.getTotalResults();
        float rating = INIT_VALUE;
        // proccessing
        while (page < totalPages) {
            rate.getRatingProccessing().put(id, page * 100 / totalPages);
            rating += movies.getMovies().stream().mapToDouble(Movie::getVoteAverage).sum();
            page++;
            url.append("&page=").append(page);
            try {
                movies = restTemplate.getForObject(url.toString(), Movies.class);
            } catch (RestClientException e) {
                Thread.sleep(10000);
                movies = restTemplate.getForObject(url.toString(), Movies.class);
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
