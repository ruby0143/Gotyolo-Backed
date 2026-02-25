package com.goTyolo.repository;

import com.goTyolo.models.BookingEntity;
import com.goTyolo.enums.BookingState;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT b FROM BookingEntity b WHERE b.id = :id")
    Optional<BookingEntity> findByIdForUpdate(UUID id);

    @Query(value = "SELECT b FROM BookingEntity b WHERE b.state = :state AND b.expiresAt <= :currentTime")
    List<BookingEntity> findByStateAndExpiry(@Param("state") BookingState state, @Param("currentTime") LocalDateTime currentTime);
}
