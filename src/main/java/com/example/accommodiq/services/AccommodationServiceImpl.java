package com.example.accommodiq.services;

import com.example.accommodiq.domain.Accommodation;
import com.example.accommodiq.domain.Availability;
import com.example.accommodiq.domain.Review;
import com.example.accommodiq.dtos.*;
import com.example.accommodiq.enums.PricingType;
import com.example.accommodiq.repositories.AccommodationRepository;
import com.example.accommodiq.services.interfaces.IAccommodationService;
import com.example.accommodiq.utilities.ReportUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccommodationServiceImpl implements IAccommodationService {
    AccommodationRepository accommodationRepository;
    ResourceBundle bundle = ResourceBundle.getBundle("ValidationMessages", LocaleContextHolder.getLocale());

    @Autowired
    public AccommodationServiceImpl(AccommodationRepository accommodationRepository) {
        this.accommodationRepository = accommodationRepository;
    }

    @Override
    public Collection<AccommodationListDto> findAll() {
        return new ArrayList<AccommodationListDto>() {
            {
                add(new AccommodationListDto(1L, "City Center Apartment", "https://example.image.com", 4.92,
                        202, "Novi Sad", 540, 2, 5));
            }

            {
                add(new AccommodationListDto(2L, "City Center Apartment", "https://example.image.com", 4.92,
                        202, "Novi Sad", 540, 2, 5));
            }
        };
    }

    public Accommodation changeAccommodationStatus(Long accommodationId, AccommodationStatusDto statusDto) {
        if (accommodationId == 4L) {
            ReportUtils.throwNotFound("accommodationNotFound");
        }

        return new Accommodation(1L,
                "Cozy Cottage",
                "A charming place to relax",
                "Green Valley",
                "cottage_image.jpg",
                2,
                4,
                "Cottage",
                true,
                PricingType.PER_GUEST,
                true,
                7,
                null
        );
    }

    @Override
    public AccommodationDetailsDto findById(Long accommodationId) {
        if (accommodationId == 4L) {
            ReportUtils.throwNotFound("accommodationNotFound");
        }

        AccommodationDetailsHostDto detailsHostDto = new AccommodationDetailsHostDto(1L, "John Doe", 4.92, 202);
        ArrayList<Availability> availabilities = new ArrayList<>() {
            {
                add(new Availability(1L, new Date(), new Date(), 150.0));

            }

            {
                add(new Availability(2L, new Date(), new Date(), 120.0));
            }
        };
        ArrayList<AccommodationDetailsReviewDto> detailsReviewDtos = new ArrayList<>() {
            {
                add(new AccommodationDetailsReviewDto("John Doe", "Great place!", 4.5, new Date()));
            }

            {
                add(new AccommodationDetailsReviewDto("Jane Smith", "Excellent stay!", 5.0, new Date()));
            }
        };

        return new AccommodationDetailsDto(
                1L,
                "Cozy Cottage",
                4.8,
                25,
                "123 Main St, Cityville",
                detailsHostDto,
                "cottage_image.jpg",
                2,
                4,
                availabilities,
                "A charming cottage with a beautiful garden.",
                detailsReviewDtos
        );
    }

    @Override
    public Accommodation findAccommodation(Long accommodationId) {
        return null;
    }

    @Override
    public Accommodation updateAccommodation(AccommodationUpdateDto updateDto) {
        if (updateDto.getId() == 4L) {
            ReportUtils.throwNotFound("accommodationNotFound");
        }

        return new Accommodation(1L,
                "Cozy Cottage",
                "A charming place to relax",
                "Green Valley",
                "cottage_image.jpg",
                2,
                4,
                "Cottage",
                true,
                PricingType.PER_GUEST,
                true,
                7,
                null
        );
    }

    @Override
    public Accommodation addAccommodationAvailability(Long accommodationId, AvailabilityDto availabilityDto) {
        if (accommodationId == 4L) {
            ReportUtils.throwNotFound("accommodationNotFound");
        }

        return new Accommodation(1L,
                "Cozy Cottage",
                "A charming place to relax",
                "Green Valley",
                "cottage_image.jpg",
                2,
                4,
                "Cottage",
                true,
                PricingType.PER_GUEST,
                true,
                7,
                null
        );
    }

    @Override
    public Accommodation removeAccommodationAvailability(Long accommodationId, Long availabilityId) {
        if (accommodationId == 4L) {
            ReportUtils.throwNotFound("accommodationNotFound");
        }

        if (availabilityId == 4L) {
            ReportUtils.throwNotFound("availabilityNotFound");
        }

        return new Accommodation(1L,
                "Cozy Cottage",
                "A charming place to relax",
                "Green Valley",
                "cottage_image.jpg",
                2,
                4,
                "Cottage",
                true,
                PricingType.PER_GUEST,
                true,
                7,
                null
        );
    }

    @Override
    public AccommodationReportDto getAccommodationReport(Long accommodationId) {
        ArrayList<AccommodationReportRevenueDto> revenueDtos = new ArrayList<>() {
            {
                add(new AccommodationReportRevenueDto("January", 8000));
            }

            {
                add(new AccommodationReportRevenueDto("February", 8000));
            }
        };

        return new AccommodationReportDto(30, revenueDtos);
    }

    @Override
    public Collection<Review> getAccommodationReviews(Long accommodationId) {
        return new ArrayList<Review>() {
            {
                add(new Review(1L, 5, "Great place!", new Date(), null));
            }

            {
                add(new Review(2L, 5, "Excellent stay!", new Date(), null));
            }
        };

        //Accommodation accommodation = accommodationRepository.findById(accommodationId);
        //return accommodation.getReviews();
    }

    @Override
    public Accommodation addReview(Long accommodationId, ReviewRequestDto reviewDto) {
        if (accommodationId == 4L) {
            ReportUtils.throwNotFound("accommodationNotFound");
        }

        return new Accommodation(1L,
                "Cozy Cottage",
                "A charming place to relax",
                "Green Valley",
                "cottage_image.jpg",
                2,
                4,
                "Cottage",
                true,
                PricingType.PER_GUEST,
                true,
                7,
                null
        );
    }
}
