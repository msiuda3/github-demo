package com.example.github_demo;


import lombok.Data;

@Data
public class GithubBranch {
    private String name;
    private GithubCommit commit;
    private boolean isProtected;
}
