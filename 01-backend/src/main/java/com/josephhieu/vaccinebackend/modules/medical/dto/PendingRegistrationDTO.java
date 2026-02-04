package com.josephhieu.vaccinebackend.modules.medical.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PendingRegistrationDTO {

    private UUID id;
    private String tenVacXin;
    private String soLo;
    private String ngayHen;
}
