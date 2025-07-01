package com.example.osid.domain.counsel.repository;

import com.example.osid.domain.counsel.entity.Counsel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CounselRepository extends JpaRepository<Counsel, Long> {

    Page<Counsel> findAllByUserId(Long userId, Pageable pageable);

    Page<Counsel> findAllByDealerIdOrDealerIsNull(Long dealerId, Pageable pageable);

    @Query("SELECT c FROM Counsel c JOIN FETCH c.dealer JOIN FETCH c.user WHERE c.id = :id")
    Optional<Counsel> findWithDealerAndUserById(@Param("id") Long id);
}
