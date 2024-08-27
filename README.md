# github-demo
Demo spring boot app working with github API.
# functionality overview
The app provides one endpoint:
```
/api/v1/repo/{username}
```
which for given username returns all of their repos that aren't fork in the following format:
```
{
    "repos": [
        {
            "name": "repo1",
            "owner": "owner of the repo",
            "branches": [
                {
                    "name": "branch1",
                    "lastCommit": "sha of the last commit"
                }
            ]
        },
        {
            "name": "repo2",
            "owner": "owner of the repo",
            "branches": [
                {
                    "name": "branch1",
                    "lastCommit": "sha of the last commit"
                },
                {
                    "name": "branch2",
                    "lastCommit": "sha of the last commit"
                }
            ]
        }
        ]
}
```
In case of a non-existing user and in case if the user has no non-fork repos the endpoint returns error 404.

# Building the project
To build the project run
```
mvn clean install
```
 in the project's root directory

# Running the application
After building the project you can run the application by running the command:
```
mvn spring-boot:run
```
This will run the application on the default port 8080. The endpoint should be available under the address: 
```
localhost:8080/api/v1/repo/{username}
```
