package com.streetsafe.controller;

import com.streetsafe.model.RatingBar;
import com.streetsafe.model.Review;
import com.streetsafe.model.Vendor;
import com.streetsafe.repository.ReviewRepository;
import com.streetsafe.repository.VendorRepository;
import com.streetsafe.service.LicenseService;
import com.streetsafe.service.QRCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StreetSafeController {

    private static final int MAX_NAME = 60;
    private static final int MAX_COMMENT = 500;

    private final VendorRepository vendorRepository;
    private final ReviewRepository reviewRepository;
    private final LicenseService licenseService;
    private final QRCodeService qrCodeService;

    /** Optional fixed base URL for the QR code, e.g. http://192.168.1.10:8080 */
    @Value("${streetsafe.public-base-url:}")
    private String publicBaseUrl;

    // ------------------------------------------------------------------ Home

    @GetMapping("/")
    public String homePage(@RequestParam(name = "sort", required = false) String sort, Model model) {
        List<Vendor> vendors = vendorRepository.findAll();

        // Sort using the existing getAverageRating() method from Vendor.java
        if ("best".equalsIgnoreCase(sort)) {
            vendors.sort((v1, v2) -> Double.compare(v2.getAverageRating(), v1.getAverageRating()));
        } else if ("worst".equalsIgnoreCase(sort)) {
            vendors.sort((v1, v2) -> Double.compare(v1.getAverageRating(), v2.getAverageRating()));
        }

        // Keep your existing stats counts
        long totalStalls = vendors.size();
        long verifiedStalls = vendors.stream().filter(Vendor::isFssaiVerified).count();
        long totalReviews = reviewRepository.count();

        model.addAttribute("vendors", vendors);
        model.addAttribute("currentSort", sort);
        model.addAttribute("totalStalls", totalStalls);
        model.addAttribute("verifiedStalls", verifiedStalls);
        model.addAttribute("totalReviews", totalReviews);

        return "index";
    }

    // -------------------------------------------------------------- Register

    @PostMapping("/vendor/register")
    public String registerVendor(@RequestParam String stallName,
                                 @RequestParam String ownerName,
                                 @RequestParam String location,
                                 @RequestParam String fssaiNumber,
                                 RedirectAttributes redirectAttributes) {

        stallName = stallName.trim();
        ownerName = ownerName.trim();
        location = location.trim();
        fssaiNumber = fssaiNumber.replaceAll("\\s+", "");

        if (stallName.isEmpty() || ownerName.isEmpty() || location.isEmpty() || fssaiNumber.isEmpty()) {
            redirectAttributes.addFlashAttribute("registerError", "Please fill in every field.");
            return "redirect:/";
        }
        if (stallName.length() > 120 || ownerName.length() > 120 || location.length() > 200 || fssaiNumber.length() > 20) {
            redirectAttributes.addFlashAttribute("registerError", "One of the fields is too long. Please shorten it.");
            return "redirect:/";
        }
        if (vendorRepository.existsByFssaiNumber(fssaiNumber)) {
            redirectAttributes.addFlashAttribute("registerError",
                    "A stall with FSSAI number " + fssaiNumber + " is already registered.");
            return "redirect:/";
        }

        Vendor vendor = new Vendor(stallName, ownerName, location, fssaiNumber);
        vendor.setFssaiVerified(licenseService.verifyFSSAI(fssaiNumber));

        try {
            vendor = vendorRepository.save(vendor);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("registerError",
                    "A stall with FSSAI number " + fssaiNumber + " is already registered.");
            return "redirect:/";
        }

        if (vendor.isFssaiVerified()) {
            redirectAttributes.addFlashAttribute("success",
                    "Stall registered. Your FSSAI number passed the format check and your stall now carries the FSSAI Verified badge.");
        } else {
            redirectAttributes.addFlashAttribute("warning",
                    "Stall registered, but the FSSAI number is not in a valid format (14 digits starting with 1 or 2), so it is not verified yet.");
        }
        return "redirect:/vendor/" + vendor.getId();
    }

    // ------------------------------------------------------- Vendor dashboard

    @GetMapping("/vendor/{id}")
    public String vendorDashboard(@PathVariable Long id, Model model) {
        Vendor vendor = findVendor(id);
        List<Review> reviews = reviewRepository.findByVendorIdOrderByCreatedAtDesc(id);
        String scanUrl = buildScanUrl(id);

        model.addAttribute("vendor", vendor);
        model.addAttribute("reviews", reviews);
        model.addAttribute("breakdown", buildBreakdown(reviews));
        model.addAttribute("scanUrl", scanUrl);
        model.addAttribute("qrCode", qrCodeService.generateQRCodeBase64(scanUrl, 360, 360));
        model.addAttribute("localhostWarning", scanUrl.contains("localhost") || scanUrl.contains("127.0.0.1"));
        return "vendor-dashboard";
    }

    // ------------------------------------------------------ Public scan page

    @GetMapping("/scan/{id}")
    public String customerScan(@PathVariable Long id, Model model) {
        Vendor vendor = findVendor(id);
        List<Review> reviews = reviewRepository.findByVendorIdOrderByCreatedAtDesc(id);

        model.addAttribute("vendor", vendor);
        model.addAttribute("reviews", reviews);
        model.addAttribute("breakdown", buildBreakdown(reviews));
        return "customer-scan";
    }

    @PostMapping("/scan/{id}/review")
    public String submitReview(@PathVariable Long id,
                               @RequestParam(required = false) String customerName,
                               @RequestParam(required = false) Integer rating,
                               @RequestParam(required = false) String comment,
                               RedirectAttributes redirectAttributes) {

        Vendor vendor = findVendor(id);

        if (rating == null || rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute("reviewError", "Please choose a star rating from 1 to 5.");
            return "redirect:/scan/" + id;
        }

        String name = customerName == null ? "" : customerName.trim();
        String text = comment == null ? "" : comment.trim();

        if (name.isEmpty()) {
            name = "Anonymous";
        }
        if (name.length() > MAX_NAME) {
            redirectAttributes.addFlashAttribute("reviewError", "Name can be at most " + MAX_NAME + " characters.");
            return "redirect:/scan/" + id;
        }
        if (text.length() > MAX_COMMENT) {
            redirectAttributes.addFlashAttribute("reviewError", "Comment can be at most " + MAX_COMMENT + " characters.");
            return "redirect:/scan/" + id;
        }

        Review review = new Review();
        review.setVendor(vendor);
        review.setCustomerName(name);
        review.setRating(rating);
        review.setComment(text.isEmpty() ? null : text);
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);

        redirectAttributes.addFlashAttribute("reviewSuccess", "Thanks! Your review is now live.");
        return "redirect:/scan/" + id;
    }

    // --------------------------------------------------------------- Helpers

    private Vendor findVendor(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stall not found"));
    }

    private String buildScanUrl(Long id) {
        String base;
        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            base = publicBaseUrl.trim().replaceAll("/+$", "");
        } else {
            base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        }
        return base + "/scan/" + id;
    }

    private List<RatingBar> buildBreakdown(List<Review> reviews) {
        int total = reviews.size();
        List<RatingBar> bars = new ArrayList<>();
        for (int star = 5; star >= 1; star--) {
            final int s = star;
            int count = (int) reviews.stream().filter(r -> r.getRating() == s).count();
            int percent = total == 0 ? 0 : (int) Math.round(count * 100.0 / total);
            bars.add(new RatingBar(star, count, percent));
        }
        return bars;
    }
}
