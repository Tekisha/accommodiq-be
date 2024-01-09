package com.example.accommodiq.controllers;

import com.example.accommodiq.domain.Availability;
import com.example.accommodiq.dtos.*;
import com.example.accommodiq.services.interfaces.accommodations.IAccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/accommodations")
@CrossOrigin
public class AccommodationController {
    final private IAccommodationService accommodationService;

    @Autowired
    public AccommodationController(IAccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    @GetMapping()
    @Operation(summary = "Get all accommodations")
    public Collection<AccommodationCardDto> getAllAccommodations(@RequestParam(required = false) String title, @RequestParam(required = false) String location, @RequestParam(required = false) Long availableFrom, @RequestParam(required = false) Long availableTo,
                                                                 @RequestParam(required = false) Integer priceFrom, @RequestParam(required = false) Integer priceTo, @RequestParam(required = false) Integer guests, @RequestParam(required = false) String type, @RequestParam(required = false) Set<String> benefits) {
        return accommodationService.findByFilter(title, location, availableFrom, availableTo, priceFrom, priceTo, guests, type, benefits);
    }

    @PutMapping("/{accommodationId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Change accommodation status")
    public AccommodationCardWithStatusDto changeAccommodationStatus(@Parameter(description = "Id of accommodation to change status") @PathVariable Long accommodationId, @RequestBody AccommodationStatusDto body) {
        return accommodationService.changeAccommodationStatus(accommodationId, body);
    }

    @GetMapping("/{accommodationId}")
    @Operation(summary = "Get accommodation by id")
    public AccommodationDetailsDto getAccommodationDetails(@Parameter(description = "Id of accommodation to get data") @PathVariable Long accommodationId) {
        return accommodationService.findById(accommodationId);
    }

    @PutMapping()
    @PreAuthorize("hasAuthority('HOST')")
    @Operation(summary = "Update accommodation")
    public AccommodationCardDto updateAccommodation(@RequestBody AccommodationModifyDto body) {
        return accommodationService.updateAccommodation(body);
    }

    @GetMapping("/{accommodationId}/booking-details")
    @PreAuthorize("hasAuthority('HOST')")
    @Operation(summary = "Get accommodation booking details")
    public ResponseEntity<AccommodationBookingDetailsWithAvailabilityDto> getAccommodationBookingDetails(@Parameter(description = "Id of accommodation to get booking details") @PathVariable Long accommodationId) {
        return accommodationService.getAccommodationBookingDetails(accommodationId);
    }

    @PutMapping("/{accommodationId}/booking-details")
    @PreAuthorize("hasAuthority('HOST')")
    @Operation(summary = "Update accommodation booking details")
    public ResponseEntity<AccommodationBookingDetailsDto> updateAccommodationBookingDetails(@Parameter(description = "Id of accommodation to update booking details") @PathVariable Long accommodationId, @RequestBody AccommodationBookingDetailsDto body) {
        return accommodationService.updateAccommodationBookingDetails(accommodationId, body);
    }

    @PostMapping("/{accommodationId}/availabilities")
    @PreAuthorize("hasAuthority('HOST')")
    @Operation(summary = "Add accommodation availability")
    public ResponseEntity<List<Availability>> addAccommodationAvailability(@Parameter(description = "Id of accommodation to add availabilities") @PathVariable Long accommodationId, @RequestBody AvailabilityDto body) {
        return accommodationService.addAccommodationAvailability(accommodationId, body);
    }

    @DeleteMapping("/{accommodationId}/availabilities/{availabilityId}")
    @PreAuthorize("hasAuthority('HOST')")
    @Operation(summary = "Remove accommodation availability")
    public MessageDto removeAccommodationAvailability(@Parameter(description = "Id of accommodation to remove availability") @PathVariable Long accommodationId, @PathVariable Long availabilityId) {
        return accommodationService.removeAccommodationAvailability(accommodationId, availabilityId);
    }

    @GetMapping("/{accommodationId}/financial-report")
    @PreAuthorize("hasAuthority('HOST')")
    @Operation(summary = "Get accommodation financial report")
    public AccommodationReportDto getAccommodationReport(@Parameter(description = "Id of accommodation to get financial report") @PathVariable Long accommodationId) {
        return accommodationService.getAccommodationReport(accommodationId);
    }

    @PostMapping("{accommodationId}/reviews")
    @PreAuthorize("hasAuthority('GUEST')")
    @Operation(summary = "Add review")
    public ReviewDto addReview(@Parameter(description = "Id of accommodation to add review") @PathVariable Long accommodationId, @RequestBody ReviewRequestDto reviewDto) {
        return accommodationService.addReview(accommodationId, reviewDto);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all pending accommodations")
    public Collection<AccommodationCardWithStatusDto> getPendingAccommodations() {
        return accommodationService.getPendingAccommodations();
    }

    @GetMapping("/{accommodationId}/total-price")
    @Operation(summary = "Get total price")
    public AccommodationPriceDto getTotalPrice(@Parameter(description = "Id of accommodation to get total price") @PathVariable Long accommodationId, @RequestParam long dateFrom, @RequestParam long dateTo, @RequestParam int guests) {
        return accommodationService.getTotalPrice(accommodationId, dateFrom, dateTo, guests);
    }

    @GetMapping("/{accommodationId}/is-available")
    @Operation(summary = "Get is available")
    public AccommodationAvailabilityDto getIsAvailable(@Parameter(description = "Id of accommodation to get is available") @PathVariable Long accommodationId, @RequestParam long dateFrom, @RequestParam long dateTo) {
        return accommodationService.getIsAvailable(accommodationId, dateFrom, dateTo);
    }

    @GetMapping("{accommodationId}/advanced")
    @PreAuthorize("hasAuthority('HOST')")
    @Operation(summary = "Get accommodation advanced details")
    public AccommodationModifyDto getAdvancedDetails(@Parameter(description = "Id of accommodation to get advanced details") @PathVariable Long accommodationId) {
        return accommodationService.getAdvancedDetails(accommodationId);
    }

    @GetMapping("/reviews/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all pending reviews")
    public Collection<PendingReviewDto> getPendingReviews() {
        return accommodationService.getPendingReviews();
    }
}
