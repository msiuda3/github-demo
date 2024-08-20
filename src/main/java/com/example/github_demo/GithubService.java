package com.example.github_demo;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GithubService {

    private final ApiService apiService;

    public ApiResponse getRepos(String username){
        List<GithubRepo> repos = apiService.getReposForUser(username);

        repos.stream()
                .filter(repo -> !repo.isFork())
                .map(repo -> {
                    List<>
                })
    }
}
