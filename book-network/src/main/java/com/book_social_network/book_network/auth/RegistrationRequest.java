package com.book_social_network.book_network.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(

        @NotEmpty(message = "firstName is required")
                @NotBlank(message = "firstName should not be blank")
        String firstName,

        @NotEmpty(message =  "lastName is required")
        @NotBlank(message = "lastName should not be blank")
        String lastName,

        @Email(message = "Email should be valid")
        @NotEmpty(message = "Email is required")
        @NotBlank(message = "Email should not be blank")
        String email,

        @NotEmpty(message = "password is required")
        @NotBlank(message = "password should not be blank")
                @Size(min = 8, message = "Password should be 8 characters long")
        String password

) {
}
