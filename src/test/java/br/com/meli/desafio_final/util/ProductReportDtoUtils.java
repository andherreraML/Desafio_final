package br.com.meli.desafio_final.util;

import br.com.meli.desafio_final.dto.ProductReportDto;

public class ProductReportDtoUtils {
    public static ProductReportDto newRepoToSave() {
        return ProductReportDto.builder()
                .name("Alface")
                .initialQuantity(210)
                .soldQuantity(8)
                .remainingQuantity(200)
                .expiredQuantity(2)
                .build();
    }
}
