package com.example.github_demo;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ApiService {
    private static final String HOST_URL = "https://api.github.com";

    private static final String GITHUB_API_ENDPOINT = "/users/{user}/repos";

    private static final String BRANCHES_API_ENDPOINT = "/repos/{user}/{repo}/branches";

    public List<GithubRepo> getReposForUser (String username){
        RestTemplate restTemplate = new RestTemplate();
        return Arrays.stream(restTemplate.getForObject(HOST_URL + GITHUB_API_ENDPOINT, GithubRepo[].class, username)).toList();
    }

    public List<GithubBranch> getBranchesForRepo (String username, String repoName){
        RestTemplate restTemplate = new RestTemplate();
        return Arrays.stream(restTemplate.getForObject(HOST_URL + BRANCHES_API_ENDPOINT, GithubBranch[].class, username, repoName)).toList();
    }
}
