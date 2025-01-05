package com.book_social_network.book_network.role;

import com.book_social_network.book_network.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "roles")
//@EntityListeners(AuditingEntityListener.class)
public class Role {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

//    @ManyToMany(mappedBy = "roles")
//    @ManyToMany
    @JsonIgnore
    private List<User> users;

    @CreatedDate
    @NotNull
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
