package com.example.github_demo.app.response.model;

import java.util.List;

public record ApiRepo(String name,
                      String owner,
                      List<ApiBranch> branches
) {}
