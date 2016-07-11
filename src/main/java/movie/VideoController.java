package movie;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mikhail.davydov on 2016/7/12.
 */

@RestController
public class VideoController {

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
}
