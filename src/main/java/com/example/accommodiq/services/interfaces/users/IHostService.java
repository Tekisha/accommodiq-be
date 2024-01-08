package com.example.accommodiq.services.interfaces.users;

import com.example.accommodiq.domain.Host;
import com.example.accommodiq.domain.Review;
import com.example.accommodiq.domain.User;
import com.example.accommodiq.dtos.*;

import java.util.ArrayList;
import java.util.Collection;

public interface IHostService {

    Collection<Host> getAll();

    Host findHost(Long hostId);

    Host insert(Host host);

    Host update(Host host);

    Host delete(Long hostId);

    void deleteAll();

    Collection<AccommodationCardWithStatusDto> getHostAccommodations();

    ArrayList<HostReservationDto> getHostAccommodationReservations(Long hostId);

    ArrayList<FinancialReportEntryDto> getFinancialReport(Long hostId, long fromDate, long toDate);

    Collection<ReviewDto> getHostReviews(Long hostId);

    AccommodationDetailsDto createAccommodation(AccommodationModifyDto accommodationDto);

    ReviewDto addReview(Long hostId, ReviewRequestDto reviewDto);

    AccommodationCardDto deleteAccommodation(Long accommodationId);
}
