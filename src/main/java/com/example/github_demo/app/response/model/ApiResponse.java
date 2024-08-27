package com.example.github_demo.app.response.model;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiResponse {
    private List<ApiRepo> repos;
}
