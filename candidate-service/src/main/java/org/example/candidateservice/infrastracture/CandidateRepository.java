package org.example.candidateservice.infrastracture;

import org.example.candidateservice.domain.CandidateApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateApplication, Long> {
}