package com.paulstna.inventory.product.mapper;

import com.paulstna.inventory.product.dto.ProductRequestDTO;
import com.paulstna.inventory.product.dto.ProductResponseDTO;
import com.paulstna.inventory.product.model.Product;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductMapper {

    public ProductResponseDTO toProductResponse(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public Product toEntity(ProductRequestDTO dto, Product product) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setImageUrl(dto.getImageUrl());
        return product;
    }
}
