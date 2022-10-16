package com.github.controller;

import com.github.module.Product;
import com.github.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Integer id) {
        return productService.findById(id).map(product -> {
            try {
                return ResponseEntity.ok().eTag(Integer.toString(product.getVersion())).location(new URI("/products/" + id))
                        .body(product);
            } catch (URISyntaxException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Iterable<Product> getProducts() {
        return productService.findAll();
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        logger.info("Creating new product with name: {}, quantity: {} ", product.getName(), product.getQuantity());
        Product newProduct = productService.save(product);

        try {
            return ResponseEntity.created(new URI("/products/" + newProduct.getId()))
                    .eTag(Integer.toString(newProduct.getVersion()))
                    .body(newProduct);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity updateProduct(@RequestBody Product product,
                                        @PathVariable Integer id,
                                        @RequestHeader("If-Match") Integer ifMatch) {
        logger.info("Updating product with id: {}, name: {}, quantity: {}", product.getId(), product.getName(), product.getQuantity());
        Optional<Product> existingProduct = productService.findById(id);
        return existingProduct.map(p -> {
            logger.info("Product with id: {} has a version of {}. Update is for If-Match: {}", p.getId(), p.getVersion(), ifMatch);
            if (!p.getVersion().equals(ifMatch)) return ResponseEntity.status(HttpStatus.CONFLICT).build();
            p.setName(product.getName());
            p.setQuantity(product.getQuantity());
            p.setVersion(p.getVersion() + 1);

            logger.info("Updating product with id: {}, name: {}, quantity: {}, version: {}", p.getId(), p.getName(), p.getQuantity(), p.getVersion());

            try {
                if (productService.update(p)) {
                    return ResponseEntity.ok().eTag(Integer.toString(p.getVersion()))
                            .location(new URI("/products/" + p.getId()))
                            .body(p);
                } else {
                    return ResponseEntity.noContent().build();
                }
            } catch (URISyntaxException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        }).orElse(ResponseEntity.notFound().build());
    }
}
