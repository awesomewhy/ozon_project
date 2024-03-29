package com.ozon.online.service.impl.product;

import com.ozon.online.dto.product.*;
import com.ozon.online.entity.Product;
import com.ozon.online.entity.User;
import com.ozon.online.exception.ErrorResponse;
import com.ozon.online.exception.UserNotAuthException;
import com.ozon.online.mapper.ProductMapper;
import com.ozon.online.repository.ProductRepository;
import com.ozon.online.service.ProductService;
import com.ozon.online.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final UserService userService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ResponseEntity<?> addProduct(@RequestParam(name = "image") MultipartFile multipartFile,
                                        @RequestBody CreateProductForSellDto createOrderForSellDto) throws IOException, ExecutionException, InterruptedException, UserNotAuthException {

        User user = userService.getAuthenticationPrincipalUserByNickname().orElseThrow(
                () -> new UserNotAuthException(HttpStatus.NOT_FOUND.value(), "user not auth")
        );

        Product product = productMapper.mapCreateOrderForSellDtoToProductEntity(multipartFile, createOrderForSellDto, user);
        productRepository.save(product);

        return ResponseEntity.ok().body(new ErrorResponse(HttpStatus.OK.value(), "product added"));
    }

    public ResponseEntity<?> sort(@RequestBody SortDto sortDto) {
        List<Product> products = productRepository.findAll();
        ExecutorService executorService = Executors.newFixedThreadPool(16);

        List<Callable<Void>> tasks = new ArrayList<>();

        if (!sortDto.getCategories().isEmpty()) {
            List<Integer> categoryIndexes = sortDto.getCategories().stream()
                    .map(category -> category.getOrderTypeEnum().ordinal())
                    .toList();

            tasks.add(() -> {
                products.removeIf(product -> !categoryIndexes.contains(product.getOrderType().ordinal()));
                return null;
            });
        }

        if (sortDto.getPaymentTypeEnum() != null) {
            tasks.add(() -> {
                products.removeIf(product -> product.getPaymentType() != sortDto.getPaymentTypeEnum());
                return null;
            });
        }

        if (sortDto.getStartPrice() != null && sortDto.getEndPrice() != null) {
            tasks.add(() -> {
                products.removeIf(product -> product.getPrice().compareTo(sortDto.getStartPrice()) < 0 ||
                        product.getPrice().compareTo(sortDto.getEndPrice()) > 0);
                return null;
            });
        }

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.ok().body(new ErrorResponse(HttpStatus.I_AM_A_TEAPOT.value(), "some problem with sort"));
        } finally {
            executorService.shutdown();
        }

        List<ProductForShowDto> productForShowDto = products.stream()
                .map(productMapper::mapProductToProductForShowDto)
                .toList();

        return ResponseEntity.ok(productForShowDto);
    }


    @Override
    public ResponseEntity<?> searchProduct(@RequestParam("text") String text) {
        List<Product> products = productRepository.findProductByName(text);
        List<ProductForShowDto> productForShowDto = products.stream()
                .map(productMapper::mapProductToProductForShowDto)
                .toList();
        return ResponseEntity.ok(productForShowDto);
    }

    @Override
    public ResponseEntity<?> getAllProducts(PageRequest pageRequest) {
        List<Product> products = productRepository.findAll(pageRequest).getContent();
        List<ProductForShowDto> productForShowDto = products.stream()
                .map(productMapper::mapProductToProductForShowDto)
                .toList();
        return ResponseEntity.ok(productForShowDto);
    }

    @Override
    public ResponseEntity<?> getCorrectProduct(@RequestParam("id") Long id) {
        Product productOptional = productRepository.findById(id).orElseThrow();
        return ResponseEntity.ok().body(productMapper.mapProductCorrectProductDto(productOptional));
    }

    @Override
    public ResponseEntity<?> getMyProducts() {
        User userOptional = userService.getAuthenticationPrincipalUserByNickname().orElseThrow();

        return ResponseEntity.ok().body(userOptional.getProducts().stream().map(
                productMapper::mapProductToMyProductDto
        ));
    }

}
