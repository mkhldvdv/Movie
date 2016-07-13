package movie;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by mikhail.davydov on 2016/7/13.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class MovieTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetList() throws Exception {
        // positive check: correct results
        mvc
                .perform(MockMvcRequestBuilders.get("/movie")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("page").isNumber())
                .andExpect(jsonPath("page").value(1))
                .andExpect(jsonPath("results").exists())
                .andExpect(jsonPath("results").isArray())
                .andExpect(jsonPath("total_results").isNotEmpty())
                .andExpect(jsonPath("total_results").isNumber())
                .andExpect(jsonPath("total_pages").isNotEmpty())
                .andExpect(jsonPath("total_pages").isNumber());

        // positive check: correct results page 2
        mvc
                .perform(MockMvcRequestBuilders.get("/movie?page=2")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("page").isNumber())
                .andExpect(jsonPath("page").value(2))
                .andExpect(jsonPath("results").exists())
                .andExpect(jsonPath("results").isArray())
                .andExpect(jsonPath("total_results").isNotEmpty())
                .andExpect(jsonPath("total_results").isNumber())
                .andExpect(jsonPath("total_pages").isNotEmpty())
                .andExpect(jsonPath("total_pages").isNumber());

        // positive check: correct results page does not exist
        mvc
                .perform(MockMvcRequestBuilders.get("/movie?page=0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("page").isNumber())
                .andExpect(jsonPath("page").value(1))
                .andExpect(jsonPath("results").exists())
                .andExpect(jsonPath("results").isArray())
                .andExpect(jsonPath("total_results").isNotEmpty())
                .andExpect(jsonPath("total_results").isNumber())
                .andExpect(jsonPath("total_pages").isNotEmpty())
                .andExpect(jsonPath("total_pages").isNumber());

        // positive check: correct results null page
        mvc
                .perform(MockMvcRequestBuilders.get("/movie?page=")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("page").isNumber())
                .andExpect(jsonPath("page").value(1))
                .andExpect(jsonPath("results").exists())
                .andExpect(jsonPath("results").isArray())
                .andExpect(jsonPath("total_results").isNotEmpty())
                .andExpect(jsonPath("total_results").isNumber())
                .andExpect(jsonPath("total_pages").isNotEmpty())
                .andExpect(jsonPath("total_pages").isNumber());

        // negative check: page is a string
        mvc
                .perform(MockMvcRequestBuilders.get("/movie?page=somestring")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetOne() throws Exception {
        // positive check: correct results
        mvc
                .perform(MockMvcRequestBuilders.get("/movie/550")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("id").isNumber())
                .andExpect(jsonPath("id").value(550))
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("title").isString())
                .andExpect(jsonPath("title").value("Fight Club"))
                .andExpect(jsonPath("overview").isNotEmpty())
                .andExpect(jsonPath("overview").isString())
                .andExpect(jsonPath("vote_average").isNotEmpty())
                .andExpect(jsonPath("vote_average").isNumber());

        // negative check: id does not exist
        mvc
                .perform(MockMvcRequestBuilders.get("/movie/-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        // negative check: id is a string
        mvc
                .perform(MockMvcRequestBuilders.get("/movie/somestring")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Ignore("Not fully implemented yet")
    public void testGetRating() throws Exception {

    }
}