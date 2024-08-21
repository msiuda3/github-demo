package com.example.github_demo;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ApiController {

    private final GithubService githubService;

    @GetMapping("/repo/{username}")
    public ApiResponse getRepos(@PathVariable String username){
        return githubService.getRepos(username);
    }
    


}
