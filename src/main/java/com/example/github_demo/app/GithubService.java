package com.example.github_demo.app;

import com.example.github_demo.app.response.model.ApiBranch;
import com.example.github_demo.app.response.model.ApiRepo;
import com.example.github_demo.app.response.model.ApiResponse;
import com.example.github_demo.github.GithubApiService;
import com.example.github_demo.github.model.GithubRepo;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GithubService {

    @Setter(onMethod_ = {@Value("${error.message.user_not_found}")})
    private String ERROR_MESSAGE_USER_NOT_FOUND;

    @Setter(onMethod_ = {@Value("${error.message.repos_not_found}")})
    private String ERROR_MESSAGE_REPOS_NOT_FOUND;

    private final GithubApiService githubApiService;

    public ApiResponse getRepos(@NonNull String username) {
        List<GithubRepo> repos;
        try {
            repos = githubApiService.getReposForUser(username);
        } catch (ResponseStatusException notFound) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format(ERROR_MESSAGE_USER_NOT_FOUND, username)
            );
        }

        List<ApiRepo> apiRepos = repos.stream()
                .filter(repo -> !repo.fork())
                .map(repo -> new ApiRepo(
                        repo.name(),
                        repo.owner().login(),
                        getBranches(username, repo.name())
                ))
                .collect(Collectors.toList());
        if (apiRepos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format(ERROR_MESSAGE_REPOS_NOT_FOUND, username)
            );
        }

        return new ApiResponse(apiRepos);
    }

    private List<ApiBranch> getBranches(String username, String repoName) {
        return githubApiService.getBranchesForRepo(username, repoName)
                .stream()
                .map(branch ->
                        new ApiBranch(
                                branch.name(),
                                branch.commit().sha()
                        ))
                .collect(Collectors.toList());
    }

}
