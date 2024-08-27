package com.example.github_demo.app.response.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiRepo {
    private String name;
    private String owner;
    private List<ApiBranch> branches;
}
