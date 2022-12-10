package com.inqoo.tripservice.repository;

import com.inqoo.tripservice.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TripJpaRepository extends JpaRepository<Trip,Integer>,
        JpaSpecificationExecutor<Trip> {
    List<Trip> findAllByPriceEurBetween(double from, double to);
}