package com.booking.controller;
import com.booking.payload.UserDTO;
import com.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private String uploadDirectory="src/main/resources/user_profile_image/";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // http://localhost:8080/api/users/create

    // for create user
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestParam("firstName") String firstName,
                                              @RequestParam("lastName") String lastName,
                                              @RequestParam("email") String email,
                                              @RequestParam("password") String password,
                                              @RequestParam("phoneNumber") String phoneNumber,
                                              @RequestParam(value = "profileImage", required = false)
                                               MultipartFile profileImage    ) {
        UserDTO userDTO = new UserDTO();

        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        userDTO.setPhoneNumber(phoneNumber);
        userDTO.setProfileImage(profileImage);

        UserDTO createdUser = userService.createUser(userDTO);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //Add this below method and above uploadDirectory path to access the image thought API by this class

    //http://localhost:8080/api/users/profile-images/{fileName}

    @GetMapping("/profile-images/{fileName}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable String fileName) {

        Path filePath = Paths.get(uploadDirectory, fileName);

        try {
            byte[] imageBytes = Files.readAllBytes(filePath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read profile image", e);
        }
    }

    //Pagination Method
    //   http://localhost:8080/api/users/users?page=0&size=3&sortOrder=asc&sortBy=id
         //this time available everything asc desc sort by id ,name etc.

    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String  sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String  sortOrder ) {
        Page<UserDTO> users = userService.getUsers(page, size, sortBy, sortOrder);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

     // for  delete operation
    //http://localhost:8080/api/users/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("User Deleted Successfully",HttpStatus.OK);
    }

    // for  update operation
    //http://localhost:8080/api/users/1
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id,
                                          @RequestParam("firstName") String firstName,
                                          @RequestParam("lastName") String lastName,
                                          @RequestParam("email") String email,
                                          @RequestParam("password") String password,
                                          @RequestParam("phoneNumber") String phoneNumber,
                                          @RequestParam(value = "profileImage", required = false)
                                          MultipartFile profileImage) {
        UserDTO userDTO = new UserDTO();

        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        userDTO.setPhoneNumber(phoneNumber);
        userDTO.setProfileImage(profileImage);
        UserDTO createdUser = userService.updateUser(id,userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // for  Generating Excel Report

        //  http://localhost:8080/api/users/download/excel

    @GetMapping("/download/excel")
    public ResponseEntity<InputStreamResource> downloadUsersAsExcel() {
        try {
            InputStreamResource stream = userService.getUserAsExcel();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename=users.xlsx");
            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(stream);
        } catch (Exception e) {
            return   ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

     // for  Generating PDF Report
    // http://localhost:8080/api/users/download/pdf

    @GetMapping(value = "/download/pdf", produces =MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getUserAsPdf() {
        try {
            InputStreamResource pdf = userService.getUserAsPdf();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=users.pdf");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdf);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //    for  Generating CSV Report
    //     http://localhost:8080/api/users/download/csv

    @GetMapping(value = "/download/csv",produces = "text/csv")
    public ResponseEntity<InputStreamResource> getUserCsv() {
        InputStreamResource csvStream = userService.getUserCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.csv");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("text/csv")).body(csvStream);
    }
}


