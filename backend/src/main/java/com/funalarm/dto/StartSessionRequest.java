package com.funalarm.dto;

import jakarta.validation.constraints.NotNull;

public record StartSessionRequest(@NotNull Integer alarmId) {}
