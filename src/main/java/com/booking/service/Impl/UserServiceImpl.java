package com.booking.service.Impl;
import com.booking.entities.User;
import com.booking.payload.UserDTO;
import com.booking.service.UserService;
import com.booking.repository.UserRepository;

import com.booking.utils.CsvExporter;
import com.booking.utils.ExcelExporter;
import com.booking.utils.PdfExplorer;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    private final String uploadDirectory = "D:\\Bus Booking Project\\booking\\src\\main\\resources\\user_profile_image\\";

    // for create user
    @Override
    public UserDTO createUser(UserDTO userDTO) {

        User user = dtoToUser(userDTO);

        user.setCreatedAt(new Date()); //it will save that time when you create
        user.setUpdatedAt(new Date()); //it will save that time when you update

        // If a password is provided, encode and set it
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty() )  {
            //if password is null then its empty in password column
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }

        //this will get image
        MultipartFile profileImage = userDTO.getProfileImage();

        //check profile image not null & not empty
        if (profileImage != null && !profileImage.isEmpty()) {//if  YES Enter Inside If Condition

            // Save the uploaded file to a desired location
            String fileName = saveProfileImage(profileImage);
            user.setProfilePicture(fileName);
        }
        User savedUser = userRepository.save(user);
        return userToDto(savedUser);
    }

    //for pagination

    @Override
    public Page<UserDTO> getUsers (int page, int size, String sortBy, String  sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        Page<User> userPage = userRepository.findAll(pageRequest);
        return userPage.map(user -> userToDto(user));
    }

    // for  delete operation
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new   RuntimeException("User not found"));
        userRepository.delete(user);
            // userRepository.deleteById(user.getId());
            // userRepository.deleteById(id);
    }


    // for  update operation

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new  RuntimeException("User not found")
        );

        // Update the fields of the user object
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        // If a new password is provided, encode and set it
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {

            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }

        // If a new profile image is provided, save it and set the filename

        MultipartFile profileImage = userDTO.getProfileImage();

        if (profileImage != null && !profileImage.isEmpty()) {
            String fileName = saveProfileImage(profileImage);
            user.setProfilePicture(fileName);
        }
        // Set the update time

        user.setUpdatedAt(new Date());
        User updatedUser = userRepository.save(user);
        return userToDto(updatedUser);
    }

    // for  Generating Excel Report
    @Override
    public InputStreamResource getUserAsExcel() {
        List<User> users = userRepository.findAll();
        try {
            return ExcelExporter.exportUsersToExcel(users);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }

    // for  Generating PDF Report

    @Override
    public InputStreamResource getUserAsPdf() throws Exception{
        List<UserDTO> userDTOS = userRepository.findAll().stream().map(e ->userToDto(e)).collect(Collectors.toList());
        ByteArrayInputStream pdfInputStream= PdfExplorer.exportUsersToPdf(userDTOS);
        return new InputStreamResource(pdfInputStream);
    }

    // for  Generating CSV Report

    @Override
    public InputStreamResource getUserCsv() {
        List<UserDTO> userDTOS =
                userRepository.findAll().stream().map(e ->userToDto(e)).collect(Collectors.toList());
        ByteArrayInputStream pdfInputStream= CsvExporter.exportUsersToCsv(userDTOS);
        return new InputStreamResource(pdfInputStream);
    }




    private String saveProfileImage(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String originalFileName = file.getOriginalFilename();
          //Just Add this for image name different for same photo
            String fileExtension =originalFileName.substring(originalFileName.lastIndexOf('.'));
            String baseFileName = originalFileName.substring(0,originalFileName.lastIndexOf('.'));
            String uniqueFileName = baseFileName + "_" +System.currentTimeMillis() + fileExtension;
            Path path = Paths.get(uploadDirectory + uniqueFileName);
            Files.write(path, bytes);

            return uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile image", e);
        }
    }

    //DTO to entity
    private User dtoToUser(UserDTO userDTO) {

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPasswordHash(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        return user;
    }
    //Entity to DTO
    private UserDTO userToDto(User user) {

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setProfilePicture(user.getProfilePicture());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        return userDTO;
    }
}
