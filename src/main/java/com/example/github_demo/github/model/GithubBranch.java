package com.example.github_demo.github.model;

public record GithubBranch(
        String name,
        GithubCommit commit,
        boolean isProtected
) {}
