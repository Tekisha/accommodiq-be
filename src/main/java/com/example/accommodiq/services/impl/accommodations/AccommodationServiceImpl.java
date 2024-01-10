package com.example.accommodiq.services.impl.accommodations;

import com.example.accommodiq.domain.*;
import com.example.accommodiq.dtos.*;
import com.example.accommodiq.enums.*;
import com.example.accommodiq.repositories.AccommodationRepository;
import com.example.accommodiq.repositories.ReservationRepository;
import com.example.accommodiq.repositories.ReviewRepository;
import com.example.accommodiq.services.interfaces.accommodations.IAccommodationService;
import com.example.accommodiq.services.interfaces.notifications.INotificationService;
import com.example.accommodiq.services.interfaces.users.IAccountService;
import com.example.accommodiq.services.interfaces.users.IGuestService;
import com.example.accommodiq.specifications.AccommodationSpecification;
import com.example.accommodiq.utilities.ErrorUtils;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

import static com.example.accommodiq.utilities.ErrorUtils.generateNotFound;


@Service
public class AccommodationServiceImpl implements IAccommodationService {
    private final static int DEFAULT_CANCELLATION_DEADLINE_VALUE_DAYS = 1;
    AccommodationRepository accommodationRepository;
    ReservationRepository reservationRepository;
    IGuestService guestService;
    IAccountService accountService;
    ReviewRepository reviewRepository;
    INotificationService notificationService;

    @Autowired
    public AccommodationServiceImpl(AccommodationRepository accommodationRepository, ReservationRepository reservationRepository, IGuestService guestService, IAccountService accountService, ReviewRepository reviewRepository, INotificationService notificationService) {
        this.accommodationRepository = accommodationRepository;
        this.reservationRepository = reservationRepository;
        this.guestService = guestService;
        this.accountService = accountService;
        this.reviewRepository = reviewRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Accommodation insert(Host host, AccommodationModifyDto accommodationDto) {
        Accommodation accommodation = new Accommodation(accommodationDto);
        accommodation.setHost(host);
        accommodation.setCancellationDeadline(DEFAULT_CANCELLATION_DEADLINE_VALUE_DAYS);
        accommodation.setStatus(AccommodationStatus.PENDING);
        try {
            accommodationRepository.save(accommodation);
            accommodationRepository.flush();
            return accommodation;
        } catch (ConstraintViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation error: " + ex.getMessage());
        }
    }

    @Override
    public Accommodation update(Accommodation accommodation) {
        try {
            findAccommodation(accommodation.getId());
            accommodationRepository.save(accommodation);
            accommodationRepository.flush();
            return accommodation;
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data integrity violation");
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Accommodation not found");
        }
    }

    @Override
    @Transactional
    public Collection<AccommodationCardDto> findByFilter(String title, String location, Long availableFrom, Long availableTo, Integer priceFrom, Integer priceTo, Integer guests, String type, Set<String> benefits) {
        List<Accommodation> searchedAccommodations = accommodationRepository.findAll(AccommodationSpecification.searchAndFilter(title, location, guests, type, benefits)).stream().filter(accommodation -> !accommodation.getAvailable().isEmpty()).toList();
        boolean dateRangeSpecified = availableFrom != null && availableTo != null;
        boolean priceRangeSpecified = priceFrom != null && priceTo != null;

        if (dateRangeSpecified) {
            searchedAccommodations = searchedAccommodations.stream().filter(accommodation -> accommodation.isAvailable(availableFrom, availableTo)).toList();
        }

        List<AccommodationCardDto> accommodations;
        if (!dateRangeSpecified && priceRangeSpecified) {
            accommodations = searchedAccommodations.stream()
                    .map(AccommodationCardDto::new)
                    .filter(accommodationListDto -> accommodationListDto.getMinPrice() != 0 && accommodationListDto.getMinPrice() >= priceFrom && accommodationListDto.getMinPrice() <= priceTo)
                    .toList();
        } else if (dateRangeSpecified && priceRangeSpecified) {
            accommodations = searchedAccommodations.stream()
                    .map(accommodation -> new AccommodationCardDto(accommodation, availableFrom, availableTo, guests))
                    .filter(accommodationListDto -> accommodationListDto.getTotalPrice() >= priceFrom && accommodationListDto.getTotalPrice() <= priceTo)
                    .toList();
        } else if (dateRangeSpecified) {
            accommodations = searchedAccommodations.stream()
                    .map(accommodation -> new AccommodationCardDto(accommodation, availableFrom, availableTo, guests))
                    .toList();
        } else {
            accommodations = searchedAccommodations.stream()
                    .map(AccommodationCardDto::new)
                    .toList();
        }

        return accommodations;
    }

    @Override
    @Transactional
    public AccommodationCardWithStatusDto changeAccommodationStatus(Long accommodationId, AccommodationStatusDto statusDto) {
        Accommodation accommodation = findAccommodation(accommodationId);
        accommodation.setStatus(statusDto.getStatus());
        update(accommodation);
        return new AccommodationCardWithStatusDto(accommodation);
    }

    @Override
    @Transactional
    public AccommodationDetailsDto findById(Long accommodationId) {
        Long loggedInId = getLoggedInAccountId();
        Optional<Accommodation> accommodation = accommodationRepository.findById(accommodationId);

        if (accommodation.isEmpty()) {
            throw ErrorUtils.generateNotFound("accommodationNotFound");
        }

        return new AccommodationDetailsDto(accommodation.get(), loggedInId);
    }

    @Override
    public Accommodation findAccommodation(Long accommodationId) {
        Optional<Accommodation> found = accommodationRepository.findById(accommodationId);
        if (found.isEmpty()) {
            throw generateNotFound("accommodationNotFound");
        }
        return found.get();
    }

    @Override
    @Transactional
    public AccommodationCardDto updateAccommodation(AccommodationModifyDto updateDto) {
        Accommodation accommodation = findAccommodation(updateDto.getId());
        accommodation.applyChanges(updateDto);
        update(accommodation);
        return new AccommodationCardDto(accommodation);
    }

    @Override
    @Transactional
    public ResponseEntity<List<Availability>> addAccommodationAvailability(Long accommodationId, AvailabilityDto availabilityDto) {
        Accommodation accommodation = findAccommodation(accommodationId);
        Availability newAvailability = new Availability(availabilityDto);

        for (Availability existingAvailability : accommodation.getAvailable()) {
            if (isOverlapping(existingAvailability, newAvailability)) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .build();
            }
        }

        accommodation.getAvailable().add(newAvailability);
        Accommodation updatedAccommodation = update(accommodation);

        return ResponseEntity
                .ok(updatedAccommodation.getAvailable().stream().toList());
    }

    @Override
    @Transactional
    public ResponseEntity<AccommodationBookingDetailsDto> updateAccommodationBookingDetails(Long accommodationId, AccommodationBookingDetailsDto accommodationBookingDetailsDto) {
        Accommodation accommodation = findAccommodation(accommodationId);
        accommodation.setCancellationDeadline(accommodationBookingDetailsDto.getCancellationDeadline());
        accommodation.setPricingType(accommodationBookingDetailsDto.getPricingType());
        Accommodation updatedAccommodation = update(accommodation);
        return ResponseEntity.ok(new AccommodationBookingDetailsDto(updatedAccommodation));
    }

    @Override
    @Transactional
    public MessageDto removeAccommodationAvailability(Long accommodationId, Long availabilityId) {
        Accommodation accommodation = findAccommodation(accommodationId);
        Optional<Availability> availability = accommodation.getAvailable().stream().filter(a -> a.getId().equals(availabilityId)).findFirst();
        if (availability.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Availability not found");
        }

        Availability availabilityToRemove = availability.get();

        if (hasActiveReservations(accommodation, availabilityToRemove)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove availability as there are active reservations in this period.");
        }

        accommodation.getAvailable().remove(availability.get());
        update(accommodation);

        return new MessageDto("Availability removed");
    }

    @Override
    public AccommodationReportDto getAccommodationReport(Long accommodationId) {
        ArrayList<AccommodationReportRevenueDto> revenues = new ArrayList<>() {
            {
                add(new AccommodationReportRevenueDto("January", 8000));
            }

            {
                add(new AccommodationReportRevenueDto("February", 8000));
            }
        };

        return new AccommodationReportDto(30, revenues);
    }

    @Override
    public Collection<Review> getAccommodationReviews(Long accommodationId) { // mocked
        return new ArrayList<>() {
            {
                add(new Review(1L, 5, "Great place!", Instant.now().toEpochMilli(), null));
            }

            {
                add(new Review(2L, 5, "Excellent stay!", Instant.now().toEpochMilli(), null));
            }
        };
    }

    @Override
    public ReviewDto addReview(Long accommodationId, ReviewRequestDto reviewDto) {
        Long guestId = getGuestId();
        canGuestCommentAndRateAccommodation(guestId, accommodationId); // this will throw ResponseStatusException if guest cannot comment and rate accommodation
        Accommodation accommodation = findAccommodation(accommodationId);
        Guest guest = guestService.findGuest(guestId);
        Review review = new Review(reviewDto, guest, ReviewStatus.PENDING);
        accommodation.getReviews().add(review);
        update(accommodation);
        return new ReviewDto(review, guestId);
    }

    @Override
    @Transactional
    public Collection<AccommodationCardWithStatusDto> getPendingAccommodations() {
        return accommodationRepository.findAllByStatus(AccommodationStatus.PENDING).stream().map(AccommodationCardWithStatusDto::new).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<AccommodationBookingDetailsWithAvailabilityDto> getAccommodationBookingDetails(Long accommodationId) {
        Accommodation accommodation = findAccommodation(accommodationId);
        AccommodationBookingDetailsWithAvailabilityDto accommodationDetails = new AccommodationBookingDetailsWithAvailabilityDto(accommodation);
        return ResponseEntity.ok(accommodationDetails);
    }

    @Override
    public AccommodationPriceDto getTotalPrice(long accommodationId, long dateFrom, long dateTo, int guests) {
        return new AccommodationPriceDto(findAccommodation(accommodationId).getTotalPrice(dateFrom, dateTo, guests));
    }

    @Override
    public Collection<Accommodation> findAccommodationsByHostId(Long hostId) {
        return accommodationRepository.findByHostId(hostId);
    }

    public AccommodationAvailabilityDto getIsAvailable(long accommodationId, long dateFrom, long dateTo) {
        return new AccommodationAvailabilityDto(findAccommodation(accommodationId).isAvailable(dateFrom, dateTo));
    }

    @Override
    @Transactional
    public AccommodationModifyDto getAdvancedDetails(Long accommodationId) {
        Accommodation accommodation = findAccommodation(accommodationId);
        return new AccommodationModifyDto(accommodation);
    }

    @Override
    public AccommodationCardDto deleteAccommodation(Long accommodationId) {
        Accommodation accommodation = findAccommodation(accommodationId);
        reservationRepository.deleteByAccommodationId(accommodationId);
        accommodationRepository.delete(accommodation);
        accommodationRepository.flush();
        return new AccommodationCardDto(accommodation);
    }

    @Override
    public Collection<PendingReviewDto> getReviewsByStatus(ReviewStatus status) {
        Collection<Review> reviews = reviewRepository.findByStatus(status);
        Collection<Accommodation> accommodationsWithPendingReviews = accommodationRepository.findAccommodationsContainingReview(reviews.stream().map(Review::getId).toList());
        Collection<PendingReviewDto> pendingReviews = new ArrayList<>();
        for (Accommodation accommodation : accommodationsWithPendingReviews) {
            List<Review> accommodationReviews = accommodation.getReviews().stream().filter(reviews::contains).toList();
            for (Review review : accommodationReviews)
                pendingReviews.add(new PendingReviewDto(review, accommodation));
        }

        return pendingReviews;
    }

    @Override
    public MessageDto changeReviewStatus(Long reviewId, ReviewStatusDto body) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        ReviewStatus oldStatus = review.getStatus();
        review.setStatus(body.getStatus());
        reviewRepository.save(review);
        reviewRepository.flush();
        if (oldStatus == ReviewStatus.PENDING && body.getStatus() == ReviewStatus.ACCEPTED) {
            Accommodation accommodation = accommodationRepository.findAccommodationByReviewsContaining(review);
            Notification notification = new Notification("Your accommodation has a new review", NotificationType.ACCOMMODATION_RATING, accommodation.getHost());
            notificationService.createAndSendNotification(notification);
        }
        return new MessageDto("Review status updated successfully");
    }

    private boolean hasActiveReservations(Accommodation accommodation, Availability availability) {

        Long count = reservationRepository.countOverlappingReservations(
                accommodation.getId(),
                availability.getFromDate(),
                availability.getToDate()
        );
        return count != null && count > 0;
    }

    private boolean isOverlapping(Availability existing, Availability newAvailability) {
        long existingStart = existing.getFromDate();
        long existingEnd = existing.getToDate();
        long newStart = newAvailability.getFromDate();
        long newEnd = newAvailability.getToDate();

        return newStart < existingEnd && newEnd > existingStart;
    }

    private void canGuestCommentAndRateAccommodation(Long guestId, Long accommodationId) {
        long currentTime = System.currentTimeMillis() / 1000; //TODO: change everything to milliseconds in database
        long sevenDaysAgo = currentTime - (7 * 24 * 60 * 60);

        // Check if guest has stayed in this accommodation or the 7-day period post-reservation has expired
        Collection<Reservation> reservations = reservationRepository
                .findByGuestIdAndAccommodationIdAndStatusNotInAndEndDateGreaterThanAndEndDateLessThan(
                        guestId, accommodationId, Arrays.asList(ReservationStatus.PENDING, ReservationStatus.CANCELLED), sevenDaysAgo, currentTime);

        if (reservations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Guest cannot comment and rate this accommodation, because he has not stayed here or the 7-day period post-reservation has expired");
        }

        // Check if guest has already commented and rated this accommodation
        Set<Review> reviewsForAccommodationByGuest = reviewRepository.findReviewsByGuestIdAndAccommodationId(guestId, accommodationId);
        Collection<Reservation> reservationsForAccommodationByGuest = reservationRepository.findByGuestIdAndAccommodationIdAndStatusNotInAndEndDateLessThan(guestId, accommodationId, Arrays.asList(ReservationStatus.PENDING, ReservationStatus.CANCELLED), currentTime);

        if (reviewsForAccommodationByGuest.size() >= reservationsForAccommodationByGuest.size()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Guest cannot comment and rate this accommodation, as they have already left reviews for all their reservations.");
        }
    }

    private Long getGuestId() {
        Account account = getAccount();
        if (account.getRole() != AccountRole.GUEST) throw new RuntimeException("User is not a guest");
        return account.getId();
    }

    private Long getLoggedInAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return -1L;
        }

        String email = authentication.getName();
        Account account = (Account) accountService.loadUserByUsername(email);
        return account.getId();
    }

    private Account getAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return (Account) accountService.loadUserByUsername(email);
    }
}
