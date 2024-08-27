package com.example.github_demo.github;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GithubCommit {
    private String sha;
    private String url;
}
