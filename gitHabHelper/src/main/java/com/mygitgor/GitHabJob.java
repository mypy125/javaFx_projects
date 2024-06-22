package com.mygitgor;


import io.github.cdimascio.dotenv.Dotenv;
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

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("GITHUB_TOKEN");

    public GitHabJob(){
        try {
            gitHub = new GitHubBuilder()
                    .withAppInstallationToken(System.getenv(token))
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
                try {
                    boolean notifyForNewPrs = !allPullReqId.isEmpty();
                    HashSet<GHPullRequest> newPrs = new HashSet<>();

                    List<RepositoryDescription> repos = myself.getAllRepositories()
                            .values()
                            .stream()
                            .map(repository -> {
                                try {
                                    List<GHPullRequest> pulReq = repository.queryPullRequests()
                                            .list()
                                            .toList();
                                    Set<Long> pullReqIds = pulReq.stream()
                                            .map(GHPullRequest::getId)
                                            .collect(Collectors.toSet());
                                    pullReqIds.removeAll(allPullReqId);
                                    allPullReqId.addAll(pullReqIds);
                                    pulReq.forEach(pr -> {
                                        if (pullReqIds.contains(pr.getId())) {
                                            newPrs.add(pr);
                                        }
                                    });

                                    return new RepositoryDescription(
                                            repository.getFullName(),
                                            repository,
                                            pulReq
                                    );
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList());

                    if (notifyForNewPrs) {
                        newPrs.forEach(pr -> {
                            gui.showNotification(
                                    "New PR in " + pr.getRepository().getFullName(),
                                    pr.getTitle()
                            );
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1000, 1000);
    }


}
