package com.streetsafe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * One row of the star-rating breakdown chart (e.g. "4 stars: 3 reviews, 60%").
 */
@Getter
@AllArgsConstructor
public class RatingBar {
    private final int star;
    private final int count;
    private final int percent;
}
