package org.aadi.bookstore.catalog.web.controllers;

import lombok.RequiredArgsConstructor;
import org.aadi.bookstore.catalog.domain.PagedResult;
import org.aadi.bookstore.catalog.domain.Product;
import org.aadi.bookstore.catalog.domain.ProductNotFoundException;
import org.aadi.bookstore.catalog.domain.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    PagedResult<Product> getProducts(@RequestParam (name = "page", defaultValue = "1") int pageNo){
        return productService.getProducts(pageNo);

    }

    @GetMapping("/{code}")
    ResponseEntity<Product> getProductByCode(@PathVariable String code) {
        return productService
                .getProductByCode(code)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ProductNotFoundException.forCode(code));
    }

}