package com.routeplanner.backend.dto.request;

import com.routeplanner.backend.dto.StopDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RouteRequest {

    /**
     * Başlangıç noktası — depo, garaj ya da kurye'nin bulunduğu yer.
     * Rota buradan başlar ve buraya döner.
     */
    @NotNull(message = "Başlangıç konumu gerekli")
    @Valid
    private StopDto depot;

    /**
     * Optimize edilecek duraklar.
     * Minimum 1, maksimum 500 (ücretsiz plan: 15 ile kısıtlanır — servis katmanında kontrol edilir).
     */
    @NotEmpty(message = "En az bir durak girilmeli")
    @Size(max = 500, message = "Tek seferde en fazla 500 durak optimize edilebilir")
    @Valid
    private List<StopDto> stops;
}

