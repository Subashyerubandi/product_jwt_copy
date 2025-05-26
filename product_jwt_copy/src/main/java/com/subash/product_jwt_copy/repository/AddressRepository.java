package com.subash.product_jwt_copy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.subash.product_jwt_copy.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
	List<Address> findByUserEmail(String email);
}