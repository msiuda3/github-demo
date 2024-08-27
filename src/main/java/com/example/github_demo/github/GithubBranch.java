package com.example.github_demo.github;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GithubBranch {
    private String name;
    private GithubCommit commit;
    private boolean isProtected;
}
