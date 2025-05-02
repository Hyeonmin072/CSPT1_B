package com.myong.backend.repository;

import com.myong.backend.domain.entity.user.MemberShip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberShipRepository extends JpaRepository<MemberShip, UUID> {

}
