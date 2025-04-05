package ru.datastorage.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "phones")
public class Phone {
    @Id
    @NotNull
    @Column(name = "model", updatable = false, nullable = false)
    private String model;

    @NotNull
    @Column(name = "release_year")
    private Integer releaseYear;

    @NotNull
    @Column(name = "type")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> type;

    @NotNull
    @Column(name = "memory")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> memory;
}
