package com.example.osid.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.osid.event.entity.FailedEvent;

@Repository
public interface FailedEventRepository extends JpaRepository<FailedEvent, Long> {

}
