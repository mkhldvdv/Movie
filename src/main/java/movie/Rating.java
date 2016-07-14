package movie;

import java.util.Map;

/**
 * Created by mikhail.davydov on 2016/7/14.
 */

public interface Rating {
    public Map<Long, Integer> getRatingProccessing();
    public Map<Long, Float> getRatingProccessed();
}
