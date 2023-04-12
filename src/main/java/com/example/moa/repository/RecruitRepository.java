package com.example.moa.repository;

import com.example.moa.domain.Recruit;
import com.example.moa.domain.RecruitUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    Recruit findByRecruitId(Long recruitId);

}