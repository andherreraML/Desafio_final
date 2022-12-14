package br.com.meli.desafio_final.service;

import br.com.meli.desafio_final.dto.BatchesByProductDto;
import br.com.meli.desafio_final.dto.ProductDto;
import br.com.meli.desafio_final.dto.ProductReportDto;
import br.com.meli.desafio_final.model.entity.Product;
import br.com.meli.desafio_final.model.enums.Category;

import java.util.List;

public interface IProductService {

    Product save(ProductDto productDto);
    String update(ProductDto productDto);
    String delete(Long id);
    Product findByName(String name);
    List<Product> findAllProducts();
    List<Product> findByCategory(Category category);
    Product findById(Long id);
    BatchesByProductDto findBatchByProduct(Long id, String sort);
    ProductReportDto doRepoByName(String name);
}
