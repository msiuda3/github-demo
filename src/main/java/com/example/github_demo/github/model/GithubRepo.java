package com.example.github_demo.github.model;

public record GithubRepo(
        String name,
        boolean fork,
        GithubOwner owner
) {}
