package movie;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mikhail.davydov on 2016/7/14.
 */

@Component
public class RatingImpl implements Rating {

    private final Map<Long, Integer> ratingProccessing = new HashMap<>();
    private final Map<Long, Float> ratingProccessed = new HashMap<>();

    @Override
    public Map<Long, Integer> getRatingProccessing() {
        return ratingProccessing;
    }

    @Override
    public Map<Long, Float> getRatingProccessed() {
        return ratingProccessed;
    }
}
