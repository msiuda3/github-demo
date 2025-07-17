package com.example.github_demo.app.response.model;


import java.util.List;

public record ApiResponse(
        List<ApiRepo> repos
) {}
