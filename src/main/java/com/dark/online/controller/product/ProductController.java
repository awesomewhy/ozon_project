package com.dark.online.controller.product;

import com.dark.online.dto.product.CreateProductForSellDto;
import com.dark.online.dto.product.SortDto;
import com.dark.online.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;

    @PostMapping(value = "/create")
    public ResponseEntity<?> addProduct(// @RequestParam(name = "image") MultipartFile file,
                                        @RequestBody CreateProductForSellDto createOrderForSellDto) {
        return productService.addProduct(createOrderForSellDto);
    }

    @GetMapping("/product")
    public ResponseEntity<?> searchProducts(@RequestParam(required = false, name = "text") String text) {
        return productService.searchProduct(text);
    }

    @GetMapping("/sort")
    public ResponseEntity<?> sortProducts(@RequestBody SortDto sortDto) {
        return productService.sortByJsonResponse(sortDto);
    }

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        return productService.getAllProducts();
    }
//    INSERT INTO service (name, price, description, orderTypeEnum, paymentTypeEnum, user_id)
//    VALUES ('услуга 1', 100, 'description service', 'SOME', 'VISA', 'f725741d-18a4-4818-b09a-ad792c214c66');

}
