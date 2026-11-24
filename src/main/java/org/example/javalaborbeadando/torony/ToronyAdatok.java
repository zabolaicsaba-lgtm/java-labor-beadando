package org.example.javalaborbeadando.torony;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ToronyAdatok(
        @NotNull(message = "Add meg a tornyok számát.")
        @Positive(message = "A tornyok száma pozitív egész szám legyen.") Integer darab,
        @NotNull(message = "Add meg a teljesítményt.")
        @Positive(message = "A teljesítmény pozitív egész szám legyen.") Integer teljesitmeny,
        @NotNull(message = "Add meg az üzembe helyezés évét.")
        @Min(value = 1, message = "Az év 1 és 9999 között legyen.")
        @Max(value = 9999, message = "Az év 1 és 9999 között legyen.") Integer kezdev,
        @NotNull(message = "Válassz helyszínt.")
        @Positive(message = "Válassz érvényes helyszínt.") Integer helyszinId) {
}
