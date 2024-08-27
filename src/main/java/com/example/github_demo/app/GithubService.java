package com.example.github_demo.app;

import com.example.github_demo.app.response.model.ApiResponse;
import com.example.github_demo.app.response.model.ApiBranch;
import com.example.github_demo.app.response.model.ApiRepo;
import com.example.github_demo.github.GithubApiService;
import com.example.github_demo.github.GithubRepo;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GithubService {

    @Setter(onMethod_ = {@Value("${error.message.user_not_found}")})
    private String ERROR_MESSAGE_USER_NOT_FOUND;

    @Setter(onMethod_ = {@Value("${error.message.user_not_found}")})
    private String ERROR_MESSAGE_REPOS_NOT_FOUND;


    private final GithubApiService githubApiService;

    public ApiResponse getRepos(@NonNull String username){
        List<GithubRepo> repos;
        try {
            repos = githubApiService.getReposForUser(username);
        }
        catch (ResponseStatusException notFound){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(ERROR_MESSAGE_USER_NOT_FOUND, username));
        }
        if(repos.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(ERROR_MESSAGE_REPOS_NOT_FOUND, username));
        }

        ApiResponse result = ApiResponse.builder().repos(
                repos.stream()
                        .filter(repo -> !repo.isFork())
                        .map(repo ->
                                ApiRepo.builder()
                                        .name(repo.getName())
                                        .owner(repo.getOwner().getLogin())
                                        .branches(
                                                githubApiService.getBranchesForRepo(username, repo.getName())
                                                        .stream()
                                                        .map(
                                                                branch -> ApiBranch.builder()
                                                                        .name(branch.getName())
                                                                        .lastCommit(branch.getCommit().getSha())
                                                                        .build()
                                                        ).collect(Collectors.toCollection(ArrayList::new))
                                        ).build()
                        ).collect(Collectors.toCollection(ArrayList::new))

        ).build();
        if(result.getRepos().isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(ERROR_MESSAGE_REPOS_NOT_FOUND, username));
        }
        return result;
    }
}
