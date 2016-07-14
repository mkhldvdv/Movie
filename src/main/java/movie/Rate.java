package movie;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mikhail.davydov on 2016/7/14.
 */

@Component
public class Rate {

    private final Map<Long, Integer> ratingProccessing = new HashMap<>();
    private final Map<Long, Float> ratingProccessed = new HashMap<>();
    private final Map<Long, String> ratingErrors = new HashMap<>();

    public Map<Long, Integer> getRatingProccessing() {
        return ratingProccessing;
    }

    public Map<Long, Float> getRatingProccessed() {
        return ratingProccessed;
    }

    public Map<Long, String> getRatingErrors() {
        return ratingErrors;
    }
}
