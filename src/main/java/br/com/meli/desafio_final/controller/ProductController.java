package br.com.meli.desafio_final.controller;

import br.com.meli.desafio_final.dto.BatchesByProductDto;
import br.com.meli.desafio_final.dto.ProductDto;
import br.com.meli.desafio_final.dto.ProductReportDto;
import br.com.meli.desafio_final.model.entity.Product;
import br.com.meli.desafio_final.model.enums.Category;
import br.com.meli.desafio_final.service.implementation.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v2/fresh-products")
public class ProductController {


    @Autowired
    private ProductService service;

    /**
     * Nesse método criamos um novo produto
     * @param productDto
     */
    @PostMapping
    public ResponseEntity<Product> save(@RequestBody @Valid ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(productDto));
    }

    /**
     * Nesse método retornamos um produto pesquisado pelo nome
     * @param name
     */
    @GetMapping("/{name}")
    public ResponseEntity<Product> findByName(@PathVariable String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    /**
     * Nesse método fazemos um update do produto mandado
     * @param product
     */
    @PutMapping
    public ResponseEntity<String> update(@RequestBody ProductDto product) {
        return ResponseEntity.ok(service.update(product));
    }

    /**
     * Nesse método deletamos o produto com o id mandado
     * @param id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }

    /**
     * Nesse método retornamos uma lista com todos os produtos
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(service.findAllProducts());
    }

    /**
     * Nesse método retornamos uma lista de produtos por categoria
     * @param querytype
     * @return
     */

    @GetMapping("/list")
    public ResponseEntity<List<Product>> getByCategory(@RequestParam Category querytype) {
        return ResponseEntity.ok(service.findByCategory(querytype));
    }

    /**
     * Nesse método retornamos produto por lote (batch product )
     * @param productId
     * @param s
     * @return
     */
    @GetMapping("/sortlist")
    public ResponseEntity<BatchesByProductDto> findBatchByProduct(@RequestParam Long productId, String s) {
        return ResponseEntity.ok(service.findBatchByProduct(productId, s));
    }

    /**
     * Nesse método retornamos a quantidade vendida do produto especifico
     * @param name
     * @return um relatorio da quantidade total de vendas do produto
     */
    @GetMapping("/product")
    public ResponseEntity<ProductReportDto> doRepoByName(@RequestParam String name) {
        return ResponseEntity.ok(service.doRepoByName(name));
    }
}
