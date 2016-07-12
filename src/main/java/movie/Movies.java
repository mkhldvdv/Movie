package movie;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by mikhail.davydov on 2016/7/12.
 */
public class Movies {

    @JsonProperty("results")
    private List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "Movies{" +
                "movies=" + movies +
                '}';
    }
}
