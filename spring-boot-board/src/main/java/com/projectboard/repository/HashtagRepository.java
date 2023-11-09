package com.projectboard.repository;

import com.projectboard.domain.Hashtag;
import com.projectboard.repository.querydsl.HashtagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.*;
import java.util.Optional;

@RepositoryRestResource
public interface HashtagRepository extends JpaRepository<Hashtag,Long>,
        HashtagRepositoryCustom,
        QuerydslPredicateExecutor<Hashtag> {
    Optional<Hashtag> findByHashtagName(String hashtagName);

    List<Hashtag> findByHashtagNameIn(Set<String> hashtagNames);
}

