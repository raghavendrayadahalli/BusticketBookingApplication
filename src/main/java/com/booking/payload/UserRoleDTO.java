package com.booking.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDTO {

    private Long userId;
    private Long roleId;
}