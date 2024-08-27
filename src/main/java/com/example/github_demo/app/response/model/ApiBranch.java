package com.example.github_demo.app.response.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiBranch {
    private String name;
    private String lastCommit;
}
