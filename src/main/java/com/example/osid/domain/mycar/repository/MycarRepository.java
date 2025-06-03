package com.example.osid.domain.mycar.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.osid.domain.mycar.entity.Mycar;

@Repository
public interface MycarRepository extends JpaRepository<Mycar, Long> {

	Optional<Mycar> findByIdAndDeletedAtIsNull(Long myCarId);

	Page<Mycar> findAllByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

	boolean existsByOrdersId(Long ordersId);

}
