package com.github.service.impl;

import com.github.module.Product;
import com.github.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {


    @Override
    public Optional<Product> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Iterable<Product> findAll() {
        return null;
    }

    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public boolean update(Product product) {
        return false;
    }
}
