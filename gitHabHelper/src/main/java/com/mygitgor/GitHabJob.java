package com.mygitgor;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GitHabJob {
    private final GitHub gitHub;
    private final Gui gui = new Gui();
    private final Set<Long> allPullReqId = new HashSet<>();

    public GitHabJob(){
        try {
            gitHub = new GitHubBuilder()
                    .withAppInstallationToken(System.getenv("GITHUB_TOKEN"))
                    .build();
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException {
        GHMyself myself = gitHub.getMyself();
        String login = myself.getLogin();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    myself.getAllRepositories()
                            .values()
                            .stream()
                            .map(repository ->{
                                try {
                                    List<GHPullRequest> pulReq = repository.queryPullRequests()
                                            .list()
                                            .toList();
                                    Set<Long> pullReqId = pulReq.stream()
                                            .map(GHPullRequest::getId)
                                            .collect(Collectors.toSet());
                                    pullReqId.removeAll(allPullReqId);
                                    allPullReqId.addAll(pullReqId);
                                    //...................//
                                    return new RepositoryDescription(
                                            repository.getFullName(),
                                            repository, pulReq);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }).collect(Collectors.toList());
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        }, 1000, 1000);
    }

}
