package com.kombaos.catalog.product.controller;

import com.kombaos.catalog.product.domain.Product;
import com.kombaos.catalog.product.dto.ProductCreateRequest;
import com.kombaos.catalog.product.dto.ProductResponse;
import com.kombaos.catalog.product.dto.ProductUpdateRequest;
import com.kombaos.catalog.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> list() {
        return service.list().stream().map(ProductController::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable String id) {
        return toResponse(service.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductCreateRequest request) {
        return toResponse(service.create(
                request.name(),
                request.description(),
                request.priceCents(),
                request.currency(),
                request.active()
        ));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable String id, @Valid @RequestBody ProductUpdateRequest request) {
        return toResponse(service.update(
                id,
                request.name(),
                request.description(),
                request.priceCents(),
                request.currency(),
                request.active()
        ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    private static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.id(),
                product.name(),
                product.description(),
                product.priceCents(),
                product.currency(),
                product.active(),
                product.createdAt()
        );
    }
}
