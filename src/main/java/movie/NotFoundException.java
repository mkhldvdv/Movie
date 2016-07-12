package movie;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mikhail.davydov on 2016/7/12.
 */

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such movie found", code = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException() {
    }
}
