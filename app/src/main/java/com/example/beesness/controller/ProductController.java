package com.example.beesness.controller;

import com.example.beesness.database.repositories.ProductRepository;

public class ProductController {

    private final ProductRepository repository;

    public ProductController() {
        this.repository = ProductRepository.getInstance();
    }

}