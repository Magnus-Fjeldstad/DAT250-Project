package com.example.pollapp.repo;

import com.example.pollapp.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByVoterIdAndVotedOnId(Long userId, Long optionId);

    List<Vote> findAllByVoterId(Long userId);

    List<Vote> findAllByVotedOn_Poll_Id(Long pollId);

    @Query("""
           select v.votedOn.id as optionId,
                  sum(case when v.value = 1 then 1 else 0 end) as upVotes,
                  sum(case when v.value = -1 then 1 else 0 end) as downVotes
           from Vote v
           where v.votedOn.poll.id = :pollId
           group by v.votedOn.id
           """)
    List<Object[]> aggregateUpDownByPoll(Long pollId);
}
