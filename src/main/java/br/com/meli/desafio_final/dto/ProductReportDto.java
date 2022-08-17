package br.com.meli.desafio_final.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ProductReportDto {
    @JsonProperty(value = "Nome do produto")
    private String name;

    @JsonProperty(value = "Quantidade inicial de produtos")
    private int initialQuantity;

    @JsonProperty(value = "Total produtos vendidos")
    private int soldQuantity;

    @JsonProperty(value = "Total produtos disponíveis em estoque")
    private int remainingQuantity;

    @JsonProperty(value = "Total produtos que venceram")
    private int expiredQuantity;

    public ProductReportDto(String name, int initial, int sold, int remaining, int expired) {
        this.name = name;
        this.initialQuantity = initial;
        this.soldQuantity = sold;
        this.remainingQuantity = remaining;
        this.expiredQuantity = expired;
    }
}
