package com.example.beesness.factories;

import com.example.beesness.models.ProductCategory;

public class ProductCategoryFactory {
    public static ProductCategory create(String name, String code){
        return new ProductCategory(null, name, code);
    }
    public static ProductCategory create(String id, String name, String code){
        return new ProductCategory(id, name, code);
    }
}
