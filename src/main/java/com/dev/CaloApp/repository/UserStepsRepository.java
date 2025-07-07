package com.dev.CaloApp.repository;

import com.dev.CaloApp.entity.UserSteps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserStepsRepository extends JpaRepository<UserSteps, Long> {
    List<UserSteps> findByUserId(long userId);

    UserSteps findByUserIdAndDate(long userId, LocalDate date);
    List<UserSteps> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

}
