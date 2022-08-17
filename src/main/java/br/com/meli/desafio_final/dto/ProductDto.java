package br.com.meli.desafio_final.dto;

import br.com.meli.desafio_final.model.enums.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter @Setter
@Builder
@NoArgsConstructor
public class ProductDto {
    private String name;
    private Double volumen;

    @Enumerated(EnumType.STRING)
    private Category category;

    public ProductDto(String name, Double volumen, Category category) {
        this.name = name;
        this.volumen = volumen;
        this.category = category;
    }
}
