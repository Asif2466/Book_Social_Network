package com.book_social_network.book_network.common;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
//@MappedSuperClass
public abstract class BaseEntity {

    @Id
    private String id;

    @CreatedDate
    @NotNull
    private LocalDate createdDate;

    @LastModifiedDate
    private LocalDate lastModifiedDate;

    @CreatedBy
    @NotNull
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
