package com.mefi.backend.db.repository;

import com.mefi.backend.db.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
