package com.booking.repository;

import com.booking.entities.UserOffer;
import com.booking.utils.UserOfferId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOfferRepository extends JpaRepository<UserOffer, UserOfferId> {

}
