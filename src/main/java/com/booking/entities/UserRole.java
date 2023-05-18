package com.booking.entities;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

import com.booking.utils.UserRoleId;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserRoleId.class)
@Table(name = "user_roles")
public class UserRole implements Serializable{

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id",referencedColumnName = "id")
    private Role role;
}
