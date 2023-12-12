package com.example.accommodiq.services.interfaces;

import com.example.accommodiq.domain.Accommodation;
import com.example.accommodiq.domain.Host;
import com.example.accommodiq.domain.Review;
import com.example.accommodiq.dtos.*;

import java.util.Collection;

public interface IAccommodationService {
    Collection<AccommodationListDto> findAll();

    Accommodation insert(Host host, AccommodationCreateDto accommodationDto);

    Accommodation update(Accommodation accommodation);

    Accommodation changeAccommodationStatus(Long accommodationId, AccommodationStatusDto statusDto);

    AccommodationDetailsDto findById(Long accommodationId);

    Accommodation findAccommodation(Long accommodationId);

    Accommodation updateAccommodation(AccommodationUpdateDto updateDto);

    Accommodation updateAccommodationAvailability(Long accommodationId, AvailabilityPricingDto availabilityDto);

    MessageDto removeAccommodationAvailability(Long accommodationId, Long availabilityId);

    AccommodationReportDto getAccommodationReport(Long accommodationId);

    Collection<Review> getAccommodationReviews(Long accommodationId);

    Accommodation addReview(Long accommodationId, ReviewRequestDto reviewDto);
}
