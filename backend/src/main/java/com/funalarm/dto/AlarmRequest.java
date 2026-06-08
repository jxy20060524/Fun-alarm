package com.funalarm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record AlarmRequest(
        @NotNull Integer userId,
        @NotNull LocalTime alarmTime,
        @NotBlank @Pattern(regexp = "[01]{7}") String repeatDays,
        @NotBlank @Size(max = 100) String ringtone,
        @NotNull Boolean active,
        @Size(max = 50) String label
) {}
