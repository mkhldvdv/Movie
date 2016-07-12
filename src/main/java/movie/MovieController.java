package movie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mikhail.davydov on 2016/7/12.
 */

@RestController
public class MovieController {

    @Value("${themoviedb.accessUri}")
    private String accessUri;

    @Value("${themoviedb.apiKey}")
    private String apiKey;

    @RequestMapping("/list")
    public List getList() {
        return null;
    }

    @RequestMapping("/film")
    public String getOne() {
        return null;
    }

    @RequestMapping("/rating")
    public Float getRating() {
        return null;
    }

    @RequestMapping("/url")
    public String getUrl() {
        return accessUri;
    }

}
