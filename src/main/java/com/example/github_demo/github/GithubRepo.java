package com.example.github_demo.github;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GithubRepo {
    private String name;
    private boolean fork;
    private GithubOwner owner;
}
