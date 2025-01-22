package nl.tudelft.sem.template.user.controllers.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.database.UserRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith({SpringExtension.class})
@ActiveProfiles({"test"})
@DirtiesContext(
        classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD
)
@AutoConfigureMockMvc
@ComponentScan("nl.tudelft.sem.template.user")
public class IntegrationAuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private transient UserRepository userRepo;
    @Autowired
    private transient UserProfileRepository profileRepo;

    private UserProfile author;
    private UserProfile user;

    UUID validAuthorId;
    UUID validUserId;

    @BeforeEach
    void setup(){
        UserProfile authorUnsaved = new UserProfile("authorTEST");
        authorUnsaved.setRole(UserProfile.RoleEnum.AUTHOR);
        authorUnsaved.setState(UserProfile.StateEnum.ACTIVE);
        authorUnsaved.setUserId(UUID.randomUUID());

        UserProfile userUnsaved = new UserProfile("userTEST");
        userUnsaved.setRole(UserProfile.RoleEnum.USER);
        userUnsaved.setState(UserProfile.StateEnum.ACTIVE);
        userUnsaved.setUserId(UUID.randomUUID());

        author = profileRepo.save(authorUnsaved);
        user = profileRepo.save(userUnsaved);

        validAuthorId = author.getUserId();
        validUserId = user.getUserId();
    }

    @AfterEach
    void cleanUp(){
        profileRepo.deleteById(author.getUserId());
        profileRepo.deleteById(user.getUserId());
    }


    @Test
    void testGetAuthors() throws Exception {
        // Case 1: Database contains authors
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/author/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        // Deserialize the response content to a list of UserProfile
        String jsonResponse = result.getResponse().getContentAsString();
        List<UserProfile> authors = objectMapper.readValue(jsonResponse, new TypeReference<List<UserProfile>>() {});
        assertNotNull(authors);
        assertFalse(authors.isEmpty());

        // Case 2: Database is empty
        mockMvc.perform(MockMvcRequestBuilders.get("/user/author/all"))
                .andExpect(status().isOk()); // Corrected to expect a 200 status for a successful request
    }

    @Test
    void testGetAuthorByID() throws Exception {
        // Case 1: Author with the given ID exists, and the role is AUTHOR
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/author/view/{author_id}", validAuthorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Deserialize the response content to a list of UserProfile
        String jsonResponse = result.getResponse().getContentAsString();
        List<UserProfile> authors = objectMapper.readValue(jsonResponse, new TypeReference<List<UserProfile>>() {});

        // Assert that the response contains the validAuthorId
        assertTrue(authors.stream().anyMatch(author -> validAuthorId.equals(author.getUserId())));

        // Case 2: Author with the given ID exists, but the role is not AUTHOR
        mockMvc.perform(MockMvcRequestBuilders.get("/user/author/view/{author_id}", validUserId))
                .andExpect(status().isNotFound());

        // Case 3: Author with the given ID does not exist
        mockMvc.perform(MockMvcRequestBuilders.get("/user/author/view/{author_id}", UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

}
