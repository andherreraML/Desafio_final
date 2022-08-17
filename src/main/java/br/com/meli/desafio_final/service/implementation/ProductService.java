package br.com.meli.desafio_final.service.implementation;

import br.com.meli.desafio_final.dto.*;
import br.com.meli.desafio_final.exception.BadRequest;
import br.com.meli.desafio_final.exception.NotFound;
import br.com.meli.desafio_final.model.entity.Batch;
import br.com.meli.desafio_final.model.entity.Product;
import br.com.meli.desafio_final.model.entity.Section;
import br.com.meli.desafio_final.model.enums.Category;
import br.com.meli.desafio_final.repository.ProductRepository;
import br.com.meli.desafio_final.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private AdsenseService adsenseService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private SectionService sectionService;

    /**
     * Nesse método estamos salvando um novo produto
     * @param productDto
     */
    @Override
    public Product save(ProductDto productDto) {
        Product product = new Product();
        try {
            findByName(productDto.getName());
        } catch (NotFound e) {
            product.setName(productDto.getName());
            product.setVolumen(productDto.getVolumen());
            product.setCategory(productDto.getCategory());
            repository.save(product);
            return product;
        }
        throw new BadRequest("Produto já cadastrado");
    }

    /**
     * Nesse método estamos fazendo um update de um produto
     * @param productDto
     */
    @Override
    public String update(ProductDto productDto) {
        Product foundProduct = findByName(productDto.getName());
        foundProduct.setName(productDto.getName());
        foundProduct.setVolumen(productDto.getVolumen());
        foundProduct.setCategory(productDto.getCategory());
        repository.save(foundProduct);
        return "Update do produto feito com sucesso";
    }

    /**
     * Nesse método estamos deletando um produto pelo id
     * @param id
     */
    @Override
    public String delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new NotFound("Produto não existente");
        }
        return "Produto deletado com sucesso";
    }

    /**
     * Nesse método estamos retornando um produto achado pelo nome
     * @param name
     */
    @Override
    public Product findByName(String name) {
        Product product = repository.findByName(name);
        if (product == null) throw new NotFound("Produto não existente");
        return product;
    }

    /**
     * Nesse método estamos retornado uma lista de produtos
     * @return
     */
    @Override
    public List<Product> findAllProducts() {
        List<Product> products = repository.findAll();

        if (products.size() == 0) throw new NotFound("Lista de produtos não encontrada");

        return products;
    }

    /**
     * Nesse método estamos retornando uma lista de produtos por categoria
     * @param category
     * @return
     */
    @Override
    public List<Product> findByCategory(Category category) {
        List<Product> response = repository.findByCategory(category);
        if (response.size() == 0) {
            throw new RuntimeException("Nenhum produto com essa categoria foi encontrado");
        }
        return response;
    }

    /**
     * Nesse método estamos localizando um produto por Id
     * @param id
     * @return
     */
    @Override
    public Product findById(Long id) {
        return repository.findById(id).orElseThrow(() -> {
            throw new NotFound("Produto inexistente");
        });
    }

    /**
     * Nesse método estamos retornado um lote de produto
     * @param id
     * @param s
     * @return
     */
    @Override
    public BatchesByProductDto findBatchByProduct(Long id, String s) {
        Product product = findById(id);
        List<Section> sections = sectionService.findByCategory(product.getCategory());
        List<AdsenseIdDto> adsenseList = adsenseService.findByProductId(product.getId());
        List<BatchDto> batchStock = batchService.returnBatchStock(adsenseList, s);
        return new BatchesByProductDto(sections.get(0), product.getId(), batchStock);
    }

    /**
     * Esse método retorna um Dto que é um relatório sobre um produto em específico,
     * para o representante ou vendedor ver como foram as vendas
     * @param name
     */
    @Override
    public ProductReportDto doRepoByName(String name) {
        Product product = repository.findByName(name);
        List<AdsenseIdDto> adsenseList = adsenseService.findByProductId(product.getId());
        List<Batch> batchList = batchService.allStocksByAdsenseList(adsenseList);
        int totalInitialQuantity = batchList.stream().mapToInt(Batch::getInitialQuantity).sum();
        int totalCurrentQuantity = batchList.stream().mapToInt(Batch::getCurrentQuantity).sum();
        int total = 0;
        if (totalInitialQuantity > totalCurrentQuantity) total = totalInitialQuantity - totalCurrentQuantity;
        List<BatchDto> batchStock = batchService.returnBatchStock(adsenseList, null);
        int CurrentQuantity = batchStock.stream().mapToInt(BatchDto::getCurrentQuantity).sum();
        int totalExpired = batchService.totalExpired(batchList);
        return new ProductReportDto(name, totalInitialQuantity, total, CurrentQuantity, totalExpired);
    }
}
