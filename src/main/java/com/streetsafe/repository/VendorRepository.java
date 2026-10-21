package com.streetsafe.repository;

import com.streetsafe.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    boolean existsByFssaiNumber(String fssaiNumber);
}
