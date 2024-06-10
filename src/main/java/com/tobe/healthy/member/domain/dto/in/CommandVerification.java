package com.tobe.healthy.member.domain.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CommandVerification {
    @NotEmpty
    private String email;

    @NotEmpty
    private String emailKey;
}
