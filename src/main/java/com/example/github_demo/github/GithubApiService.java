package com.example.github_demo.github;

import com.example.github_demo.github.model.GithubBranch;
import com.example.github_demo.github.model.GithubRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubApiService {

    private final RestClient githubRestClient;

    private static final String GITHUB_API_ENDPOINT = "/users/{username}/repos";
    private static final String BRANCHES_API_ENDPOINT = "/repos/{username}/{repo}/branches";

    public List<GithubRepo> getReposForUser(String username) {
        try {
            GithubRepo[] repos = githubRestClient.get()
                    .uri(GITHUB_API_ENDPOINT, uriBuilder -> uriBuilder.build(username))
                    .retrieve()
                    .body(GithubRepo[].class);

            return repos != null ? List.of(repos) : List.of();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    public List<GithubBranch> getBranchesForRepo(String username, String repoName) {
        GithubBranch[] branches = githubRestClient.get()
                .uri(BRANCHES_API_ENDPOINT, uriBuilder -> uriBuilder.build(username, repoName))
                .retrieve()
                .body(GithubBranch[].class);

        return branches != null ? List.of(branches) : List.of();
    }
}
