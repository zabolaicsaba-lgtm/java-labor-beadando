package org.example.javalaborbeadando.torony;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ToronyAdatok(
        @NotNull @Positive Integer darab,
        @NotNull @Positive Integer teljesitmeny,
        @NotNull @Min(1) @Max(9999) Integer kezdev,
        @NotNull @Positive Integer helyszinId) {
}
