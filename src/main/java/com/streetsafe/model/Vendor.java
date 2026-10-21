package com.streetsafe.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A registered street food stall.
 */
@Entity
@Table(name = "vendor")
@Getter
@Setter
@NoArgsConstructor
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stall_name", nullable = false, length = 120)
    private String stallName;

    @Column(name = "owner_name", nullable = false, length = 120)
    private String ownerName;

    @Column(name = "location", nullable = false, length = 200)
    private String location;

    @Column(name = "fssai_number", nullable = false, unique = true, length = 20)
    private String fssaiNumber;

    @Column(name = "fssai_verified", nullable = false)
    private boolean fssaiVerified;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Review> reviews = new ArrayList<>();

    public Vendor(String stallName, String ownerName, String location, String fssaiNumber) {
        this.stallName = stallName;
        this.ownerName = ownerName;
        this.location = location;
        this.fssaiNumber = fssaiNumber;
    }

    /**
     * Average star rating rounded to one decimal place, or 0.0 when there are no reviews.
     */
    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        return Math.round(avg * 10.0) / 10.0;
    }

    /**
     * Average rating rounded to the nearest whole star (used to draw star icons).
     */
    public int getRoundedRating() {
        return (int) Math.round(getAverageRating());
    }

    public int getReviewCount() {
        return reviews == null ? 0 : reviews.size();
    }
}
