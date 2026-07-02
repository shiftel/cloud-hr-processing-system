package org.example.candidateservice.application.query;

public class DownloadCandidateQuery {

    private final Long id;

    public DownloadCandidateQuery(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
