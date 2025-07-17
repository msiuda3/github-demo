package com.example.github_demo;

import com.example.github_demo.github.model.GithubBranch;
import com.example.github_demo.github.model.GithubCommit;
import com.example.github_demo.github.model.GithubOwner;
import com.example.github_demo.github.model.GithubRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTest {

    @LocalServerPort
    private int port;

    private RestClient client;
    private static MockWebServer mockGitHubServer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterAll
    static void shutdown() throws IOException {
        mockGitHubServer.shutdown();
    }

    @DynamicPropertySource
    static void overrideGitHubApi(DynamicPropertyRegistry registry) {
        try {
            mockGitHubServer = new MockWebServer();
            mockGitHubServer.start();
            registry.add("api.github.host", () -> mockGitHubServer.url("/").toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to start MockWebServer", e);
        }
    }

    @BeforeEach
    void setUp() {
        this.client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }


    @Test
    void shouldReturnOnlyNonForkRepos() throws Exception {
        String user = "testuser";
        GithubOwner owner = new GithubOwner(user);

        GithubRepo nonForkRepo = new GithubRepo("demo-repo", false, owner);
        GithubRepo forkedRepo = new GithubRepo("forked-repo", true, owner);

        GithubCommit commit = new GithubCommit("abc123", "http://mock.com");
        GithubBranch branch = new GithubBranch("main", commit, false);

        mockGitHubServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String path = request.getPath();
                try {
                    if (path.startsWith(String.format("/users/%s/repos", user))) {
                        return new MockResponse()
                                .setBody(objectMapper.writeValueAsString(List.of(nonForkRepo, forkedRepo)))
                                .addHeader("Content-Type", "application/json");
                    }

                    if (path.startsWith(String.format("/repos/%s/demo-repo/branches", user))) {
                        return new MockResponse()
                                .setBody(objectMapper.writeValueAsString(List.of(branch)))
                                .addHeader("Content-Type", "application/json");
                    }

                    if (path.startsWith(String.format("/repos/%s/forked-repo/branches", user))) {
                        return new MockResponse()
                                .setBody("[]")
                                .addHeader("Content-Type", "application/json");
                    }

                } catch (Exception e) {
                    return new MockResponse().setResponseCode(500);
                }

                return new MockResponse().setResponseCode(404);
            }
        });

        String body = client.get()
                .uri(String.format("/api/v1/repo/%s", user))
                .retrieve()
                .body(String.class);

        assertThat(body)
                .contains("demo-repo")
                .contains("testuser")
                .contains("main")
                .contains("abc123")
                .doesNotContain("forked-repo");
    }

}