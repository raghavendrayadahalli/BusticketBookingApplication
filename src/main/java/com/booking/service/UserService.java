package com.booking.service;

import com.booking.payload.UserDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;

public interface UserService {

    // for create user
    public UserDTO createUser(UserDTO userDTO);

    //for pagination
    public Page<UserDTO> getUsers(int page, int size, String sortBy, String sortOrder);

   // for  delete operation
   public void deleteUser(Long id);

    // for  update operation
    public UserDTO updateUser(Long id, UserDTO userDTO);

    // for  Generating Excel Report
    InputStreamResource getUserAsExcel();

    // for  Generating PDF Report
    InputStreamResource getUserAsPdf() throws Exception;

    // for  Generating CSV Report
    InputStreamResource getUserCsv();


}

