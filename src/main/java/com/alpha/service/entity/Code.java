package com.alpha.service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_CODE") // Mengaitkan entity dengan tabel yang sudah ada
@Data
public class Code {

    @EmbeddedId
    private CodeId id;

    @Column(name = "DESCRIPTION", length = 50)
    private String description;

    @Column(name = "CREATEBY", length = 15)
    private String createBy;

    @Column(name = "CREATEDT")
    private LocalDateTime createDt;

    @Column(name = "EDITBY", length = 15)
    private String editBy;

    @Column(name = "EDITDT")
    private LocalDateTime editDt;
}
