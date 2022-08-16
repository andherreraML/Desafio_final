package br.com.meli.desafio_final.service.implementation;

import br.com.meli.desafio_final.dto.AdsenseIdDto;
import br.com.meli.desafio_final.dto.BatchDto;
import br.com.meli.desafio_final.dto.BatchesByProductDto;
import br.com.meli.desafio_final.dto.ProductReportDto;
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
        List<Batch> batchList = batchService.allStocksByProduct(adsenseList);
        List<BatchDto> batchDtoList = BatchDto.convertDto(batchList);
        int totalInitialQuantity = batchDtoList.stream().mapToInt(BatchDto::getInitialQuantity).sum();
        int totalCurrentQuantity = batchDtoList.stream().mapToInt(BatchDto::getCurrentQuantity).sum();
        int total = totalInitialQuantity - totalCurrentQuantity;
        int totalExpired = batchService.totalExpired(batchList);
        return new ProductReportDto(name, total, totalCurrentQuantity, totalExpired);
    }
}
