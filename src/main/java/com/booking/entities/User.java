package com.booking.entities;
import lombok.*;
import javax.persistence.*;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,name = "first_name")
    private String firstName;

    @Column(nullable = false,name = "last_name")
    private String lastName;

    @Column(nullable = false, unique = true,name = "email")
    private String email;

    @Column(nullable = false,name = "password_hash")
    private String passwordHash;

    @Column(nullable = false,name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "create_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private Set<UserPaymentMethod> userPaymentMethods;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private Set<Booking> bookings;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private Set<Feedback> feedbacks;

    @ManyToMany
    @JoinTable(name = "user_offers",joinColumns = @JoinColumn(name = "user_id"),
                                  inverseJoinColumns = @JoinColumn(name = "offer_id")  )
    private Set<Offer> offers;
}


