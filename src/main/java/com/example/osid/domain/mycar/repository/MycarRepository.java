package com.example.osid.domain.mycar.repository;

import com.example.osid.domain.mycar.entity.Mycar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MycarRepository extends JpaRepository<Mycar, Long> {

}
