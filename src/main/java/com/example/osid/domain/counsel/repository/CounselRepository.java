package com.example.osid.domain.counsel.repository;

import com.example.osid.domain.counsel.entity.Counsel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounselRepository extends JpaRepository<Counsel, Long> {

    Page<Counsel> findAllByUserId(Long userId, Pageable pageable);

    Page<Counsel> findAllByDealerIdOrDealerIsNull(Long dealerId, Pageable pageable);
}
