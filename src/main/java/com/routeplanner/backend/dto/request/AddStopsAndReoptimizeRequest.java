package com.routeplanner.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * "Mevcut rotaya yolda durak ekle ve hemen yeniden optimize et" tek-çağrı isteği.
 *
 * Mobil uygulama bunu kullanırken backend tek transaction'da:
 *   1) yeni durakları (CreateRouteStopRequest) ekler — koordinat varsa geocoding YAPMAZ,
 *   2) hemen reoptimize çalıştırır,
 *   3) yeni stop listesini döner.
 *
 * Bu, frontend'in 2 ayrı API çağrısı yapmasına gerek bırakmaz → daha az istek, daha az race.
 */
@Data
public class AddStopsAndReoptimizeRequest {

    @NotEmpty
    @Valid
    private List<CreateRouteStopRequest> stops;

    /** İsteğe bağlı reoptimize parametreleri; null bırakılırsa varsayılan: tüm aktif duraklar. */
    @Valid
    private ReoptimizeRouteRequest reoptimize;

    /** İsteğe bağlı tetikleyen kullanıcı kim — geçmiş kaydı için. */
    private java.util.UUID triggeredByUserId;
}
