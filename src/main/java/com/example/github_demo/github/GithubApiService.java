package com.example.github_demo.github;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubApiService {

    @Value("${api.github.host}")
    private String HOST_URL;

    @Value("${api.github.endpoint.repos}")
    private String GITHUB_API_ENDPOINT;

    @Value("${api.github.endpoint.branches}")
    private String BRANCHES_API_ENDPOINT;

    public List<GithubRepo> getReposForUser (String username) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return Arrays.stream(restTemplate.getForObject(HOST_URL + GITHUB_API_ENDPOINT, GithubRepo[].class, username)).toList();
        }
        catch (HttpClientErrorException.NotFound notFoundException){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundException.getMessage());
        }
    }

    public List<GithubBranch> getBranchesForRepo (String username, String repoName){
        RestTemplate restTemplate = new RestTemplate();
        return Arrays.stream(restTemplate.getForObject(HOST_URL + BRANCHES_API_ENDPOINT, GithubBranch[].class, username, repoName)).toList();
    }
}
