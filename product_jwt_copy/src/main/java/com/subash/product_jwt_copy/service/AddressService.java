package com.subash.product_jwt_copy.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.subash.product_jwt_copy.dto.AddressDTO;
import com.subash.product_jwt_copy.model.Address;
import com.subash.product_jwt_copy.model.User;
import com.subash.product_jwt_copy.repository.AddressRepository;
import com.subash.product_jwt_copy.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressDTO addAddress(AddressDTO addressDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = new Address();
        address.setName(addressDTO.getName());
        address.setMobileNumber(addressDTO.getMobileNumber());
        address.setPincode(addressDTO.getPincode());
        address.setLocality(addressDTO.getLocality());
        address.setAddress(addressDTO.getAddress());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setAlternatePhone(addressDTO.getAlternatePhone());
        address.setLandmark(addressDTO.getLandmark());
        address.setAddressType(addressDTO.getAddressType());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return mapToAddressDTO(savedAddress);
    }

    public List<AddressDTO> getUserAddresses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Address> addresses = addressRepository.findByUserEmail(email);
        return addresses.stream()
            .map(this::mapToAddressDTO)
            .collect(Collectors.toList());
    }

    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to update this address");
        }

        address.setName(addressDTO.getName());
        address.setMobileNumber(addressDTO.getMobileNumber());
        address.setPincode(addressDTO.getPincode());
        address.setLocality(addressDTO.getLocality());
        address.setAddress(addressDTO.getAddress());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setAlternatePhone(addressDTO.getAlternatePhone());
        address.setLandmark(addressDTO.getLandmark());
        address.setAddressType(addressDTO.getAddressType());

        Address updatedAddress = addressRepository.save(address);
        return mapToAddressDTO(updatedAddress);
    }

    public void deleteAddress(Long addressId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to delete this address");
        }

        addressRepository.delete(address);
    }

    private AddressDTO mapToAddressDTO(Address address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setName(address.getName());
        addressDTO.setMobileNumber(address.getMobileNumber());
        addressDTO.setPincode(address.getPincode());
        addressDTO.setLocality(address.getLocality());
        addressDTO.setAddress(address.getAddress());
        addressDTO.setCity(address.getCity());
        addressDTO.setState(address.getState());
        addressDTO.setAlternatePhone(address.getAlternatePhone());
        addressDTO.setLandmark(address.getLandmark());
        addressDTO.setAddressType(address.getAddressType());
        return addressDTO;
    }
}