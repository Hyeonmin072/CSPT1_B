package com.myong.backend.repository;

import com.myong.backend.domain.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdvertisementRepository extends JpaRepository<Advertisement , UUID> {

    List<Advertisement> findAll();

}
