package com.example.github_demo;

import com.example.github_demo.GithubService;
import com.example.github_demo.github.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GithubServiceTest {

    private final String ERROR_MESSAGE_USER_NOT_FOUND = "User %s not found";
    private final String ERROR_MESSAGE_REPOS_NOT_FOUND = "No non-fork repos found for user: %s";

    @Mock
    private GithubApiService githubApiService;

    private GithubService githubService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        githubService = new GithubService(githubApiService);
        githubService.setERROR_MESSAGE_USER_NOT_FOUND(ERROR_MESSAGE_USER_NOT_FOUND);
        githubService.setERROR_MESSAGE_REPOS_NOT_FOUND(ERROR_MESSAGE_REPOS_NOT_FOUND);
    }

    @Test
    public void getRepos_UserHasNoRepos_ThrowsNotFoundException() {
        String username = "someUser";
        when(githubApiService.getReposForUser(username)).thenReturn(new ArrayList<>());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            githubService.getRepos(username);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(String.format(ERROR_MESSAGE_REPOS_NOT_FOUND, username), exception.getReason());
    }

    @Test
    public void getRepos_UserHasRepos_ReturnsFilteredRepos() {
        String username = "someUser";
        GithubOwner owner = new GithubOwner();
        owner.setLogin("someUser");
        GithubRepo repo1 = GithubRepo.builder()
                .name("Repo1")
                .owner(owner)
                .fork(false)
                .build();
        GithubRepo repo2 = GithubRepo.builder()
                .name("Repo1")
                .owner(owner)
                .fork(true)
                .build();
        List<GithubRepo> repos = List.of(repo1, repo2);

        GithubBranch branch1 = GithubBranch.builder().name("master").commit(GithubCommit.builder().sha("sha123").build()).build();
        List<GithubBranch> branches = List.of(branch1);

        when(githubApiService.getReposForUser(username)).thenReturn(repos);
        when(githubApiService.getBranchesForRepo(username, "Repo1")).thenReturn(branches);

        ApiResponse response = githubService.getRepos(username);

        assertNotNull(response);
        assertEquals(1, response.getRepos().size());

        ApiRepo apiRepo = response.getRepos().get(0);
        assertEquals("Repo1", apiRepo.getName());
        assertEquals("someUser", apiRepo.getOwner());
        assertEquals(1, apiRepo.getBranches().size());

        ApiBranch apiBranch = apiRepo.getBranches().get(0);
        assertEquals("master", apiBranch.getName());
        assertEquals("sha123", apiBranch.getLastCommit());
    }

    @Test
    public void getRepos_UserHasOnlyForkRepos_ThrowsNotFoundException() {
        String username = "someUser";
        GithubOwner owner = new GithubOwner();
        owner.setLogin("someUser");
        GithubRepo repo1 = GithubRepo.builder()
                .name("Repo1")
                .owner(owner)
                .fork(true)
                .build();
        List<GithubRepo> repos = List.of(repo1);

        when(githubApiService.getReposForUser(username)).thenReturn(repos);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            githubService.getRepos(username);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(String.format(ERROR_MESSAGE_REPOS_NOT_FOUND, username), exception.getReason());
    }

    @Test
    public void getRepos_ApiThrowsNotFound_ThrowsNotFoundException() {
        String username = "someUser";
        GithubOwner owner = new GithubOwner();
        owner.setLogin("someUser");
        GithubRepo repo1 = GithubRepo.builder()
                .name("Repo1")
                .owner(owner)
                .fork(true)
                .build();
        List<GithubRepo> repos = List.of(repo1);

        when(githubApiService.getReposForUser(username)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            githubService.getRepos(username);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(String.format(ERROR_MESSAGE_USER_NOT_FOUND, username), exception.getReason());
    }

    @Test
    public void getRepos_UserWithMultipleBranches_ReturnsCorrectBranches() {
        String username = "someUser";
        GithubOwner owner = new GithubOwner();
        owner.setLogin("someUser");
        GithubRepo repo1 = GithubRepo.builder()
                .name("Repo1")
                .owner(owner)
                .fork(false)
                .build();
        List<GithubRepo> repos = List.of(repo1);

        GithubBranch branch1 = GithubBranch.builder().name("master").commit(GithubCommit.builder().sha("sha123").build()).build();
        GithubBranch branch2 = GithubBranch.builder().name("develop").commit(GithubCommit.builder().sha("sha456").build()).build();
        List<GithubBranch> branches = List.of(branch1, branch2);

        when(githubApiService.getReposForUser(username)).thenReturn(repos);
        when(githubApiService.getBranchesForRepo(username, "Repo1")).thenReturn(branches);

        ApiResponse response = githubService.getRepos(username);

        assertNotNull(response);
        assertEquals(1, response.getRepos().size());

        ApiRepo apiRepo = response.getRepos().get(0);
        assertEquals("Repo1", apiRepo.getName());
        assertEquals("someUser", apiRepo.getOwner());
        assertEquals(2, apiRepo.getBranches().size());

        ApiBranch apiBranch1 = apiRepo.getBranches().get(0);
        assertEquals("master", apiBranch1.getName());
        assertEquals("sha123", apiBranch1.getLastCommit());

        ApiBranch apiBranch2 = apiRepo.getBranches().get(1);
        assertEquals("develop", apiBranch2.getName());
        assertEquals("sha456", apiBranch2.getLastCommit());
    }

}
