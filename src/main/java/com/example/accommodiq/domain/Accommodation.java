package com.example.accommodiq.domain;

import com.example.accommodiq.enums.PricingType;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String location;
    private String image;
    private int minGuests;
    private int maxGuests;
    private String type;
    private boolean accepted;
    private PricingType pricingType;
    private int cancellationDeadline;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Availability> available = new HashSet<>();

    public Accommodation(Long id, String title, String description, String location, String image, int minGuests,
                         int maxGuests, String type, boolean accepted, PricingType pricingType, int cancellationDeadline) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.image = image;
        this.minGuests = minGuests;
        this.maxGuests = maxGuests;
        this.type = type;
        this.accepted = accepted;
        this.pricingType = pricingType;
        this.cancellationDeadline = cancellationDeadline;
    }

    public Accommodation() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMinGuests() {
        return minGuests;
    }

    public void setMinGuests(int minGuests) {
        this.minGuests = minGuests;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingType pricingType) {
        this.pricingType = pricingType;
    }

    public int getCancellationDeadline() {
        return cancellationDeadline;
    }

    public void setCancellationDeadline(int cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
    }

    public Set<Availability> getAvailable() {
        return available;
    }

    public void setAvailable(Set<Availability> available) {
        this.available = available;
    }

    public void addAvailability(Availability availability) {
        available.add(availability);
    }

    public void removeAvailability(Availability availability) {
        available.remove(availability);
    }
}

