package com.example.github_demo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiBranch {
    private String name;
    private String lastCommit;
}
