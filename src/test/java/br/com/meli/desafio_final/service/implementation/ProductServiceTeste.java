package br.com.meli.desafio_final.service.implementation;

import br.com.meli.desafio_final.dto.BatchesByProductDto;
import br.com.meli.desafio_final.dto.ProductDto;
import br.com.meli.desafio_final.dto.ProductReportDto;
import br.com.meli.desafio_final.exception.BadRequest;
import br.com.meli.desafio_final.exception.NotFound;
import br.com.meli.desafio_final.model.entity.Product;
import br.com.meli.desafio_final.model.enums.Category;
import br.com.meli.desafio_final.repository.ProductRepository;
import br.com.meli.desafio_final.util.*;
import org.junit.jupiter.api.Assertions;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
// TODO: RENOMEAR ARQUIVO E CLASSE (ÚLTIMA PALAVRA), PARA MANTER O PADRÃO - REMOVER O "E" DA PALAVRA "TEST"
public class ProductServiceTeste {

    @InjectMocks
    ProductService productService;

    @Mock
    ProductRepository productRepository;

    @Mock
    SectionService sectionService;

    @Mock
    AdsenseService adsenseService;

    @Mock
    BatchService batchService;

    // TODO: REMOVER A PALAVRA "TEST" DOS NOMES DOS MÉTODOS, POIS A MAIORIA NÃO POSSUI
    // TODO: ADICIONAR @DisplayName() AOS TESTES QUE NÃO O POSSUI

    @Test
    @DisplayName("Salva um novo produto")
    public void save() {
        ProductDto productDto = ProductUtils.newProductDto();
        BDDMockito.when(productRepository.findByName(ArgumentMatchers.anyString()))
                .thenAnswer(invocationOnMock -> {
                    throw new NotFound("Produto não existente");
                });

        BDDMockito.when(productRepository.save(ArgumentMatchers.any()))
                .thenReturn(ProductUtils.newProduct4ToSave());

        BDDMockito.when(productRepository.findAll()).thenReturn(ProductUtils.productList());

        Product newProduct = productService.save(productDto);

        assertThat(newProduct).isNotNull();
        assertThat(newProduct.getId()).isEqualTo(ProductUtils.productList().size() + 1);
        assertThat(newProduct.getCategory()).isEqualTo(productDto.getCategory());
        assertThat(newProduct.getVolumen()).isEqualTo(productDto.getVolumen());
        assertThat(newProduct.getName()).isEqualTo(productDto.getName());
    }

    @Test
    @DisplayName("Lanza um erro quando o produto já existe")
    public void saveThrowException() {
        Exception exceptionResponse = null;
        Product newProduct = null;
        BDDMockito.when(productRepository.findByName(ArgumentMatchers.anyString()))
                .thenReturn(ProductUtils.newProduct4ToSave());

        try {
            newProduct = productService.save(ProductUtils.newProductDto());
        } catch (BadRequest e) {
            exceptionResponse = e;
        }

        assertThat(newProduct).isNull();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Produto já cadastrado");
    }

    @Test
    public void testGetAllProducts() {

        BDDMockito.when(productRepository.findAll())
                .thenReturn(ProductUtils.productList());

        List<Product> productListResponse = productService.findAllProducts();

        assertThat(productListResponse.size()).isEqualTo(4);
        assertThat(productListResponse).isNotNull();
    }

    @Test
    public void testIfGetAllProductsThrowsException() {
        Exception exceptionResponse = null;
        BDDMockito.when(productRepository.findAll())
                .thenReturn(new ArrayList<>());
        try {
            productService.findAllProducts();
        } catch (Exception exception) {
            exceptionResponse = exception;
        }

        assertThat(exceptionResponse.getMessage()).isEqualTo("Lista de produtos não encontrada");
    }

    @Test
    public void testGetProductByCategory() {
        BDDMockito.when(productRepository.findByCategory(Category.FRESH))
                .thenReturn(ProductUtils.productListFresh());

        List<Product> productListResponse = productService.findByCategory(Category.FRESH);

        assertThat(productListResponse.size()).isEqualTo(2);
        assertThat(productListResponse.get(0).getCategory()).isEqualTo(Category.FRESH);
        assertThat(productListResponse.get(1).getCategory()).isEqualTo(Category.FRESH);
    }

    @Test
    public void testIfGetProductByCategoryThrowsException() {
        Exception exceptionResponse = null;

        BDDMockito.when(productRepository.findByCategory(Category.FRESH))
                .thenReturn(new ArrayList<>());

        try {
            productService.findByCategory(Category.FRESH);
        }catch (Exception exception) {
            exceptionResponse = exception;
        }
        assertThat(exceptionResponse.getMessage()).isEqualTo("Nenhum produto com essa categoria foi encontrado");
    }

    @Test
    public void testFindById() {
        Product product =  ProductUtils.newProduct1ToSave();
        BDDMockito.when(productRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(ProductUtils.newProduct1ToSave()));

        Product productResponse = productService.findById(1L);

        assertThat(productResponse).isNotNull();
        Assertions.assertEquals(productResponse.getId(), product.getId());
    }

    @Test
    public void testFindByIdThrowsException() {
        Exception exceptionResponse = null;
        BDDMockito.when(productRepository.findById(999L))
                .thenAnswer(invocationOnMock -> Optional.empty());
        try {
            productService.findById(999L);
        }catch (Exception exception) {
            exceptionResponse = exception;
        }
        assertThat(exceptionResponse.getMessage()).isEqualTo("Produto inexistente");
    }

    @Test
    public void testFindBatchByProduct() {
        BDDMockito.when(productRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(ProductUtils.newProduct3ToSave()));

        BDDMockito.when(sectionService.findByCategory(ArgumentMatchers.any(Category.class)))
                .thenReturn(List.of(SectionUtils.newSectionFrozen()));

        BDDMockito.when(adsenseService.findByProductId(ArgumentMatchers.anyLong()))
                .thenReturn(AdsenseUtilsDto.generateAdsenseIdDtoList());

        BDDMockito.when(batchService.returnBatchStock(AdsenseUtilsDto.generateAdsenseIdDtoList(), null))
                .thenReturn(BatchDtoUtils.generateBatchDtoList());

        BatchesByProductDto batchesByProductDto = ProductUtils.bachesByProduct();
        BatchesByProductDto response = productService.findBatchByProduct(3L, null);

        assertThat(response).isNotNull();
        Assertions.assertEquals(response.getSectionId(), batchesByProductDto.getSectionId());
        Assertions.assertEquals(response.getWarehouseId(), batchesByProductDto.getWarehouseId());
        Assertions.assertEquals(response.getProductId(), batchesByProductDto.getProductId());
    }

    @Test
    @DisplayName("Retorna um relatorio do produto mandado.")
    public void doRepoByName() {
        BDDMockito.when(productRepository.findByName(ArgumentMatchers.anyString()))
                .thenReturn(ProductUtils.newProduct1ToSave());

        BDDMockito.when(adsenseService.findByProductId(ArgumentMatchers.anyLong()))
                .thenReturn(AdsenseUtilsDto.generateAdsenseIdDtoList());

        BDDMockito.when(batchService.allStocksByAdsenseList(ArgumentMatchers.anyList()))
                .thenReturn(BatchUtils.generateBatchList());

        BDDMockito.when(batchService.returnBatchStock(ArgumentMatchers.anyList(), ArgumentMatchers.any()))
                .thenReturn(BatchDtoUtils.generateBatchDtoList2());

        BDDMockito.when(batchService.totalExpired(ArgumentMatchers.anyList()))
                .thenReturn(BatchUtils.newBatch4ToSave().getCurrentQuantity());

        ProductReportDto reportDto = productService.doRepoByName("Alface");

        Assertions.assertEquals(reportDto.getName(), "Alface");
        Assertions.assertEquals(reportDto.getInitialQuantity(), 210);
        Assertions.assertEquals(reportDto.getSoldQuantity(), 8);
        Assertions.assertEquals(reportDto.getRemainingQuantity(), 200);
        Assertions.assertEquals(reportDto.getExpiredQuantity(), 2);
    }
}
