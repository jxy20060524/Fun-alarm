package com.funalarm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SubmitAnswerRequest(
        @NotNull Integer sessionId,
        @NotNull Integer questionId,
        @NotBlank @Pattern(regexp = "[A-Da-d]") String userAnswer
) {}
