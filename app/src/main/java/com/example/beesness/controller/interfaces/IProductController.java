package com.example.beesness.controller.interfaces;

public interface IProductController {
    void add(String name, String price);
    void getAll();
    void getById(String id);
    void update(String id, String name, String price);
    void delete(String id);
}
