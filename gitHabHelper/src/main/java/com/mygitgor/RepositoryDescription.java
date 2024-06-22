package com.mygitgor;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.util.List;

public class RepositoryDescription {
    private String name;
    private GHRepository repository;
    private List<GHPullRequest> pullReq;

    public RepositoryDescription(String name, GHRepository repository, List<GHPullRequest> pullReq) {
        this.name = name;
        this.repository = repository;
        this.pullReq = pullReq;
    }

    public String getName() {
        return name;
    }

    public GHRepository getRepository() {
        return repository;
    }

    public List<GHPullRequest> getPullReq() {
        return pullReq;
    }
}
