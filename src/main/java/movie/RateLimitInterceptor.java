package movie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by mikhail.davydov on 2016/7/14.
 */

@Component
public class RateLimitInterceptor extends HandlerInterceptorAdapter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${movie.requestRate}")
    private Integer requestRate;

    private final Map<String, Integer> rateCache = new HashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        int count = incrementLimit(request.getRemoteAddr());

        if (count > requestRate) {
            response.sendError(429, "Rate limit exceeded, only " + requestRate + " requests allowed per min");
            return false;
        }

        return true;
    }

    private int incrementLimit(String addr) {
        Integer currentRate = rateCache.get(addr);
        if (currentRate == null) {
            currentRate = 0;
        }

        Lock lock = new ReentrantLock();
        lock.lock();
        rateCache.put(addr, ++currentRate);
        lock.unlock();

        return currentRate;
    }

    @Scheduled(fixedRate = 60000)
    private void clearCache() {
        rateCache.clear();
    }
}
