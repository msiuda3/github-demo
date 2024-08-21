package com.example.github_demo;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiResponse {
    private List<ApiRepo> repos;
}
