package com.example.jwt_token.service;

import com.example.jwt_token.dto.ProductRequest;
import com.example.jwt_token.model.Category;
import com.example.jwt_token.model.Product;
import com.example.jwt_token.repository.CategoryRepository;
import com.example.jwt_token.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // Get all Products without pagination
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get all Products with pagination, sorting, and searching
    public Page<Product> getListProducts(String keyword, Long categoryId, Integer page, Integer limit, String sort, String order) {
        Sort sortBy = "DESC".equalsIgnoreCase(order)
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, limit, sortBy);

        if ((keyword != null && !keyword.isEmpty()) || categoryId != null) {
            return productRepository.search(keyword, categoryId, pageable);
        }

        return productRepository.findAll(pageable);
    }

    // Get product by ID
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // Save/New a new product
    public Product saveProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setSlug(productRequest.getSlug());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(productRequest.getCategory());
        return productRepository.save(product);
    }

    // Update an existing product
    public Product updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.setName(productRequest.getName());
            existingProduct.setSlug(productRequest.getSlug());
            existingProduct.setPrice(productRequest.getPrice());
            existingProduct.setQuantity(productRequest.getQuantity());
            existingProduct.setCategory(productRequest.getCategory());
            return productRepository.save(existingProduct);
        }).orElse(null);
    }

    // Delete a product by ID
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id).map(product -> {
            productRepository.delete(product);
            return true;
        }).orElse(false);
    }

    // Export
    public ByteArrayInputStream exportProductsExcel() throws IOException {
        List<Product> products = productRepository.findAll();

        // Create workbook excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        // Header
        String[] columns = {"ID", "Name", "Slug", "Price", "Quantity", "Category"};
        Row headerRow = sheet.createRow(0);

        CellStyle headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Isi data
        int rowIdx = 1;
        for (Product product : products) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getSlug());
            row.createCell(3).setCellValue(product.getPrice().doubleValue());
            row.createCell(4).setCellValue(product.getQuantity());
            row.createCell(5).setCellValue(product.getCategory() != null ? product.getCategory().getName() : "");
        }

        // Auto resize kolom
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Simpan ke stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    // Import Excel
    public void importProductsExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (!rows.hasNext()) {
                throw new IOException("File Excel kosong");
            }

            // Lewati header
            rows.next();

            List<Product> products = new ArrayList<>();

            while(rows.hasNext()) {
                Row row = rows.next();
                Product product = new Product();

                // Asumsi kolom sesuai urutan: ID, Name, Slug, Price, Quantity, Category
                if (row.getCell(1) == null || row.getCell(1).getStringCellValue().trim().isEmpty()) {
                    continue; // Lewati baris jika nama kosong
                }

                product.setName(row.getCell(1).getStringCellValue());
                product.setSlug(row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null);
                if (row.getCell(3) != null) {
                    product.setPrice(BigDecimal.valueOf(row.getCell(3).getNumericCellValue()));
                }

                if (row.getCell(4) != null) {
                    product.setQuantity((int) row.getCell(4).getNumericCellValue());
                }

                if (row.getCell(5) != null) {
                    String categoryName = row.getCell(5).getStringCellValue();
                    Category category = categoryRepository.findByName(categoryName).orElse(null);
                    product.setCategory(category);
                }

                products.add(product);
            }

            productRepository.saveAll(products);
        }
    }
}
