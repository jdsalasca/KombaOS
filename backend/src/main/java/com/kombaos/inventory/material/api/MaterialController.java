package com.kombaos.inventory.material.api;

import com.kombaos.inventory.material.Material;
import com.kombaos.inventory.material.MaterialService;
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
@RequestMapping("/api/materials")
public class MaterialController {

    private final MaterialService service;

    public MaterialController(MaterialService service) {
        this.service = service;
    }

    @GetMapping
    public List<MaterialResponse> list() {
        return service.list().stream().map(MaterialController::toResponse).toList();
    }

    @GetMapping("/{id}")
    public MaterialResponse getById(@PathVariable String id) {
        return toResponse(service.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaterialResponse create(@Valid @RequestBody MaterialCreateRequest request) {
        return toResponse(service.create(request.name(), request.unit()));
    }

    @PutMapping("/{id}")
    public MaterialResponse update(@PathVariable String id, @Valid @RequestBody MaterialUpdateRequest request) {
        return toResponse(service.update(id, request.name(), request.unit()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    private static MaterialResponse toResponse(Material material) {
        return new MaterialResponse(material.id(), material.name(), material.unit(), material.createdAt());
    }
}
