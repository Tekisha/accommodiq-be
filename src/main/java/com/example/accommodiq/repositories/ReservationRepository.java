package com.example.accommodiq.repositories;

import com.example.accommodiq.domain.Reservation;
import com.example.accommodiq.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Collection<Reservation> findByUserId(Long userId);

    Collection<Reservation> findByAccommodationId(Long accommodationId);

    @Transactional
    void deleteByAccommodationId(Long accommodationId);

    @Transactional
    void deleteByUserId(Long userId);

    List<Reservation> findByStatusAndUserIdAndEndDateGreaterThanOrderByStartDateDesc(ReservationStatus status, Long userId, Long endDate);

    List<Reservation> findByStatusAndAccommodation_HostIdAndEndDateGreaterThanOrderByStartDateDesc(ReservationStatus status, Long hostId, Long endDate);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.accommodation.id = :accommodationId " +
            "AND r.startDate < :availabilityEnd AND r.endDate > :availabilityStart")
    Long countOverlappingReservations(@Param("accommodationId") Long accommodationId,
                                      @Param("availabilityStart") Long availabilityStart,
                                      @Param("availabilityEnd") Long availabilityEnd);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.accommodation.id = :accommodationId AND r.status NOT IN :excludedStatuses AND r.endDate > :sevenDaysAgo AND r.endDate < :currentTimestamp ")
    Collection<Reservation> findPastReservationsByGuestAndAccommodationExcludingStatuses(
            @Param("userId") Long userId,
            @Param("accommodationId") Long accommodationId,
            @Param("excludedStatuses") Collection<ReservationStatus> excludedStatuses,
            @Param("sevenDaysAgo") Long sevenDaysAgo,
            @Param("currentTimestamp") Long currentTimestamp);

    Collection<Reservation> findByUserIdAndAccommodationIdInAndStatusNotAndEndDateLessThan(Long userId, Collection<Long> accommodationIds, ReservationStatus status, Long endDate);
}
