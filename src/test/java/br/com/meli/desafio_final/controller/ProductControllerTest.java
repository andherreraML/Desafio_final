package br.com.meli.desafio_final.controller;

import br.com.meli.desafio_final.dto.BatchesByProductDto;
import br.com.meli.desafio_final.dto.ProductReportDto;
import br.com.meli.desafio_final.model.entity.Product;
import br.com.meli.desafio_final.model.enums.Category;
import br.com.meli.desafio_final.service.implementation.ProductService;
import br.com.meli.desafio_final.util.ProductReportDtoUtils;
import br.com.meli.desafio_final.util.ProductUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;


    // TODO: ADICIONAR @DisplayName() AOS TESTES QUE NÃO O POSSUI

    @Test
    @DisplayName("Retorna o produto cadastrado com status 201")
    public void save() {
        BDDMockito.when(productService.save(ArgumentMatchers.any()))
                .thenReturn(ProductUtils.newProduct4ToSave());

        ResponseEntity<Product> response = productController.save(ProductUtils.newProductDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(ProductUtils.newProduct4ToSave().getName());
    }

    @Test
    @DisplayName("Retorna o produto encontrado pelo nome com status 200")
    public void findByName() {
        BDDMockito.when(productService.findByName(ArgumentMatchers.anyString()))
                .thenReturn(ProductUtils.newProduct1ToSave());

        ResponseEntity<Product> response = productController.findByName("Alface");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Alface");
    }

    @Test
    @DisplayName("Faz o update do produto com status 200")
    public void update() {
        BDDMockito.when(productService.update(ArgumentMatchers.any()))
                .thenReturn("Update do produto feito com sucesso");

        ResponseEntity<String> response = productController.update(ProductUtils.newProductDto2());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("Update do produto feito com sucesso");
    }

    @Test
    @DisplayName("Deleta um produto com status 200")
    public void delete() {
        BDDMockito.when(productService.delete(ArgumentMatchers.anyLong()))
                .thenReturn("Produto deletado com sucesso");

        ResponseEntity<String> response = productController.delete(5L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("Produto deletado com sucesso");
    }

    @Test
    public void testGetAllProducts() {
        BDDMockito.when(productService.findAllProducts())
                .thenReturn(ProductUtils.productList());

        ResponseEntity<List<Product>> productResponse = productController.getAll();
        assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(productResponse.getBody()).isNotNull();
        assertThat(productResponse.getBody().size()).isNotNull().isPositive().isEqualTo(4);
    }

    @Test
    public void testProductsByCategory() {
        BDDMockito.when(productService.findByCategory(Category.FRESH))
                .thenReturn(ProductUtils.productListFresh());

        ResponseEntity<List<Product>> productResponse = productController.getByCategory(Category.FRESH);
        assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(productResponse.getBody()).isNotNull();
        assertThat(productResponse.getBody().size()).isNotNull().isPositive().isEqualTo(2);
    }

    @Test
    public void testFindBatchByProduct() {
        BatchesByProductDto batchesByProductDto = ProductUtils.bachesByProduct();
        BDDMockito.when(productService.findBatchByProduct(1L, null))
                .thenReturn(batchesByProductDto);

        ResponseEntity<BatchesByProductDto> productResponse = productController.findBatchByProduct(1L, null);

        assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(productResponse.getBody()).isNotNull();
        assertThat(productResponse.getBody()).isEqualTo(batchesByProductDto);
    }

    @Test
    @DisplayName("Retorna o relatorio do produto com status 200")
    public void doRepoByName() {
        ProductReportDto productReportDto = ProductReportDtoUtils.newRepoToSave();
        BDDMockito.when(productService.doRepoByName(ArgumentMatchers.anyString()))
                .thenReturn(productReportDto);

        ResponseEntity<ProductReportDto> response = productController.doRepoByName("Alface");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(productReportDto);
    }
}
