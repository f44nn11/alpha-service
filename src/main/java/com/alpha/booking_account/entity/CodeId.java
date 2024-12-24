package com.alpha.booking_account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class CodeId implements Serializable {
    @Column(name = "TYPE", length = 10)
    private String type;

    @Column(name = "CODE", length = 10)
    private String code;
}
