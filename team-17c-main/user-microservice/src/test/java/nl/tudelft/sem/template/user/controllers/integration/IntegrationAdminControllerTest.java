package nl.tudelft.sem.template.user.controllers.integration;

import java.util.Arrays;
import nl.tudelft.sem.template.user.database.AdminKeyRepository;
import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.database.UserRepository;
import nl.tudelft.sem.template.user.model.UserAnalytics;
import nl.tudelft.sem.template.user.model.UserProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({SpringExtension.class})
@ActiveProfiles({"test"})
@DirtiesContext(
        classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD
)
@AutoConfigureMockMvc
@ComponentScan("nl.tudelft.sem.template.user")
public class IntegrationAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient UserRepository userRepo;
    @Autowired
    private transient UserProfileRepository profileRepo;
    @Autowired
    private transient AdminKeyRepository adminKeyRepo;
    @Autowired
    private transient LogItemRepository logRepo;

    private UserProfile admin;
    private UserProfile user;

    public IntegrationAdminControllerTest(){
    }

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        UserProfile adminUnsaved = new UserProfile("adminTEST");
        adminUnsaved.setRole(UserProfile.RoleEnum.ADMIN);
        adminUnsaved.setState(UserProfile.StateEnum.ACTIVE);

        UserProfile userUnsaved = new UserProfile("userTEST");
        userUnsaved.setRole(UserProfile.RoleEnum.USER);
        userUnsaved.setState(UserProfile.StateEnum.ACTIVE);

        admin = profileRepo.save(adminUnsaved);
        user = profileRepo.save(userUnsaved);
    }

    @AfterEach
    void cleanUp(){
        profileRepo.deleteById(admin.getUserId());
        profileRepo.deleteById(user.getUserId());
    }

    @Test
    void testGenerateKey() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/admin/generateAdminKey")
                .header("user_id", admin.getUserId());
        MvcResult res =  mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        UUID key = JsonUtil.deserialize(res.getResponse().getContentAsString(), UUID.class);
        assertTrue(adminKeyRepo.existsById(key));
        adminKeyRepo.deleteById(key);

        requestBuilder = MockMvcRequestBuilders
                .get("/user/admin/generateAdminKey");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        requestBuilder = MockMvcRequestBuilders
                .get("/user/admin/generateAdminKey")
                .header("user_id", user.getUserId());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void testViewKeys() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/admin/generateAdminKey")
                .header("user_id", admin.getUserId());
        MvcResult res =  mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        UUID key = JsonUtil.deserialize(res.getResponse().getContentAsString(), UUID.class);

        requestBuilder = MockMvcRequestBuilders
                .get("/user/admin/viewAdminKeys")
                .header("user_id", admin.getUserId());
        res =  mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        List<String> keys = Arrays.asList(JsonUtil.deserialize(res.getResponse().getContentAsString(), String[].class));
        assertTrue(keys.contains(key.toString()));
        adminKeyRepo.deleteById(key);

        requestBuilder = MockMvcRequestBuilders
                .get("/user/admin/viewAdminKeys");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        requestBuilder = MockMvcRequestBuilders
                .get("/user/admin/viewAdminKeys")
                .header("user_id", user.getUserId());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void testDeactivateActivateBanUser() throws Exception {
        UUID nonExistent = UUID.randomUUID();
        while(profileRepo.existsById(nonExistent)){
            nonExistent = UUID.randomUUID();
        }

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/user/admin/deactivate/"+user.getUserId())
                .header("user_id", admin.getUserId());
        MvcResult res =  mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        UserProfile response = JsonUtil.deserialize(res.getResponse().getContentAsString(), UserProfile.class);
        assertEquals(UserProfile.StateEnum.INACTIVE, response.getState());
        assertEquals(UserProfile.StateEnum.INACTIVE, profileRepo.findById(response.getUserId()).get().getState());
        assertEquals(response.getUserId(), user.getUserId());

        requestBuilder = MockMvcRequestBuilders
                .put("/user/admin/deactivate/"+user.getUserId())
                .header("user_id", user.getUserId());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andReturn();

        requestBuilder = MockMvcRequestBuilders
                .put("/user/admin/deactivate/"+nonExistent.toString())
                .header("user_id", admin.getUserId());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        requestBuilder = MockMvcRequestBuilders
                .put("/user/admin/activate/"+user.getUserId())
                .header("user_id", admin.getUserId());
        res =  mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        response = JsonUtil.deserialize(res.getResponse().getContentAsString(), UserProfile.class);
        assertEquals(UserProfile.StateEnum.ACTIVE, response.getState());
        assertEquals(UserProfile.StateEnum.ACTIVE, profileRepo.findById(response.getUserId()).get().getState());
        assertEquals(response.getUserId(), user.getUserId());

        requestBuilder = MockMvcRequestBuilders
                .put("/user/admin/activate/"+user.getUserId())
                .header("user_id", user.getUserId());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andReturn();

        requestBuilder = MockMvcRequestBuilders
                .put("/user/admin/activate/"+nonExistent.toString())
                .header("user_id", admin.getUserId());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        requestBuilder = MockMvcRequestBuilders
                .put("/user/admin/ban/"+user.getUserId())
                .header("user_id", admin.getUserId());
        res =  mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        response = JsonUtil.deserialize(res.getResponse().getContentAsString(), UserProfile.class);
        assertEquals(UserProfile.StateEnum.BANNED, response.getState());
        assertEquals(UserProfile.StateEnum.BANNED, profileRepo.findById(response.getUserId()).get().getState());
        assertEquals(response.getUserId(), user.getUserId());

        requestBuilder = MockMvcRequestBuilders
                .put("/user/admin/ban/"+user.getUserId())
                .header("user_id", user.getUserId());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andReturn();

        requestBuilder = MockMvcRequestBuilders
                .put("/user/admin/ban/"+nonExistent.toString())
                .header("user_id", admin.getUserId());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void testAnalytics() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/admin/analytics")
                .header("user_id", admin.getUserId());
        MvcResult res =  mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        UserAnalytics analytics = JsonUtil.deserialize(res.getResponse().getContentAsString(), UserAnalytics.class);

        assertTrue(analytics.getUserCount() > 0);
        assertTrue(analytics.getActiveUsers() > 0);
        assertNotNull(analytics.getUserLog());
        assertNotNull(analytics.getMostFollowedUsers());
        assertFalse(analytics.getMostPopularBook().isEmpty());
        assertNotNull(analytics.getMostPopularGenres());

        requestBuilder = MockMvcRequestBuilders
                .get("/user/admin/analytics");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        requestBuilder = MockMvcRequestBuilders
                .get("/user/admin/analytics")
                .header("user_id", user.getUserId());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }


}
