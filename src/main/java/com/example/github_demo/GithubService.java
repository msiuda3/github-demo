package com.example.github_demo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class GithubService {

    private final ApiService apiService;

    public ApiResponse getRepos(String username){
        List<GithubRepo> repos = apiService.getReposForUser(username);
        return ApiResponse.builder().repos(
                repos.stream()
                        .filter(repo -> !repo.isFork())
                        .map(repo ->
                                ApiRepo.builder().branches(
                                        apiService.getBranchesForRepo(username, repo.getName())
                                                .stream()
                                                .map(branch -> ApiBranch.builder().name(branch.getName()).lastCommit(branch.getCommit().getSha()).build()
                                                ).collect(Collectors.toCollection(ArrayList::new))
                                ).build()
                        ).collect(Collectors.toCollection(ArrayList::new))

        ).build();
    }
}
