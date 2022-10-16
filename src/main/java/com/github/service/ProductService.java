package com.github.service;

import com.github.module.Product;

import java.util.Optional;

public interface ProductService {
    Optional<Product> findById(Integer id);

    Iterable<Product> findAll();

    Product save(Product product);

    boolean update(Product product);
}
