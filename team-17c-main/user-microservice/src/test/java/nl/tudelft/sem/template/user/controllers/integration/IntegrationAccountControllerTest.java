package nl.tudelft.sem.template.user.controllers.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.util.UUID;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.database.UserRepository;
import nl.tudelft.sem.template.user.model.User;
import nl.tudelft.sem.template.user.model.UserAuth;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.model.UserWithoutPassword;
import nl.tudelft.sem.template.user.services.UserProfileService;
import nl.tudelft.sem.template.user.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@SpringBootTest
@ExtendWith({SpringExtension.class})
@ActiveProfiles({"test"})
@DirtiesContext(
    classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD
)
@AutoConfigureMockMvc
@ComponentScan("nl.tudelft.sem.template.user")
@TestPropertySource(locations = "classpath:application-test.properties")
public class IntegrationAccountControllerTest {
    @RegisterExtension
    static WireMockExtension wm1 = WireMockExtension.newInstance()
        .options(wireMockConfig().port(8080))
        .build();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient UserService userService;
    @Autowired
    private transient UserProfileService profileService;
    @Autowired
    private transient UserRepository userRepository;
    @Autowired
    private transient UserProfileRepository profileRepository;

    @Test
    public void createAccountSuccess() throws Exception {
        UserAuth userAuth = new UserAuth("user1", "test1@example.com", "test123");
        String json = JsonUtil.serialize(userAuth);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post("/user/account/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        UserProfile returnedProfile = JsonUtil.deserialize(result.getResponse().getContentAsString(), UserProfile.class);

        User savedUser = userRepository.findById(returnedProfile.getUserId()).orElseThrow();
        UserProfile savedProfile = profileRepository.findById(returnedProfile.getUserId()).orElseThrow();

        //assertEquals(returnedProfile, savedProfile);
        assertEquals(savedUser.getUserId(), savedProfile.getUserId());
        assertEquals(userAuth.getUsername(), savedProfile.getUsername());
        assertEquals(userAuth.getEmail(), savedUser.getEmail());
        assertEquals(userAuth.getPassword(), savedUser.getPassword());
    }

    @Test
    public void createAccountInvalidData() throws Exception {
        UserAuth userAuth = new UserAuth("**", "test1@example.com", "test123");
        String json = JsonUtil.serialize(userAuth);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post("/user/account/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());

        assertNull(userRepository.findUserByEmail("test1@example.com"));
    }

    @Test
    public void createAccountEmailExists() throws Exception {
        UserAuth userAuth1 = new UserAuth("user1", "test1@example.com", "test123");
        UserAuth userAuth2 = new UserAuth("user2", "test1@example.com", "test456");
        String json1 = JsonUtil.serialize(userAuth1);
        String json2 = JsonUtil.serialize(userAuth2);

        MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
            .post("/user/account/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json1);
        MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
            .post("/user/account/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json2);

        MvcResult result = mockMvc.perform(requestBuilder1).andExpect(status().isOk()).andReturn();
        UserProfile returnedProfile = JsonUtil.deserialize(result.getResponse().getContentAsString(), UserProfile.class);
        User savedUser = userRepository.findById(returnedProfile.getUserId()).orElseThrow();

        mockMvc.perform(requestBuilder2).andExpect(status().isForbidden());

        assertEquals(savedUser.getEmail(), userAuth1.getEmail());
        assertNull(userRepository.findUserByEmail("test2@example.com"));
    }

    @Test
    public void deleteAccountSuccess() throws Exception {
        wm1.stubFor(delete(urlPathTemplate("/bookshelves/bookshelf/{userId}/removeAll/requestBy/{requesterId}")).willReturn(ok()));

        UserAuth userAuth = new UserAuth("user1", "test1@example.com", "test123");
        String json = JsonUtil.serialize(userAuth);

        MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
            .post("/user/account/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);
        MvcResult result = mockMvc.perform(requestBuilder1).andExpect(status().isOk()).andReturn();
        UserProfile returnedProfile = JsonUtil.deserialize(result.getResponse().getContentAsString(), UserProfile.class);

        MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
            .delete("/user/account/delete")
            .header("user_id", returnedProfile.getUserId());
        mockMvc.perform(requestBuilder2).andExpect(status().isOk());

        verify(exactly(1), deleteRequestedFor(
                urlPathTemplate("/bookshelves/bookshelf/{userId}/removeAll/requestBy/{requesterId}")));

        assertTrue(userRepository.findById(returnedProfile.getUserId()).isEmpty());
        assertNull(userRepository.findUserByEmail("test1@example.com"));
    }

    @Test
    public void deleteAccountNotFound() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .delete("/user/account/delete")
            .header("user_id", UUID.randomUUID());
        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());

        verify(exactly(0), deleteRequestedFor(
                urlPathTemplate("/bookshelves/bookshelf/{userId}/removeAll/requestBy/{requesterId}")));
        assertNull(userRepository.findUserByEmail("test1@example.com"));
    }

    @Test
    public void modifyAccountSuccess() throws Exception {
        UserAuth userAuth = new UserAuth("user1", "test1@example.com", "test123");
        User userUpdate = new User("test2@example.com", "test456");

        String json1 = JsonUtil.serialize(userAuth);

        MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
            .post("/user/account/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json1);
        MvcResult result1 = mockMvc.perform(requestBuilder1).andExpect(status().isOk()).andReturn();
        UserProfile returnedProfile = JsonUtil.deserialize(result1.getResponse().getContentAsString(), UserProfile.class);

        UserWithoutPassword expectedResult = new UserWithoutPassword(userUpdate.getEmail());
        expectedResult.setUserId(returnedProfile.getUserId());
        userUpdate.setUserId(returnedProfile.getUserId());

        String json2 = JsonUtil.serialize(userUpdate);
        MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
            .put("/user/account/modify")
            .header("user_id", userUpdate.getUserId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json2);
        MvcResult result2 = mockMvc.perform(requestBuilder2).andExpect(status().isOk()).andReturn();

        UserWithoutPassword actualResult =
            JsonUtil.deserialize(result2.getResponse().getContentAsString(), UserWithoutPassword.class);
        User savedUser = userRepository.findById(userUpdate.getUserId()).orElseThrow();

        assertEquals(expectedResult, actualResult);
        assertEquals(userUpdate.getUserId(), savedUser.getUserId());
        assertEquals(userUpdate.getEmail(), savedUser.getEmail());
        assertEquals(userUpdate.getPassword(), savedUser.getPassword());
    }
}
