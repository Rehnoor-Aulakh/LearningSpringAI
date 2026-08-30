package com.telusko.SpringEcom.service;

import com.telusko.SpringEcom.model.Product;
import com.telusko.SpringEcom.repo.ProductRepo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ChatClient chatClient;

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(int id) {
        return productRepo.findById(id).orElse(new Product(-1));
    }

    public Product addOrUpdateProduct(Product product, MultipartFile image) throws IOException {

        product.setImageName(image.getOriginalFilename());
        product.setImageType(image.getContentType());
        product.setProductImage(image.getBytes());

        return productRepo.save(product);

    }


    public void deleteProduct(int id) {
        productRepo.deleteById(id);
    }

    @Transactional
    public List<Product> searchProducts(String keyword) {
        return productRepo.searchProducts(keyword);
    }


    public String generateDescription(String name, String category) {
         // use the chatClient object to generate the product description
         String descriptionPrompt = String.format("""
                    Write a concise and professional product for an e-commerce listing.
                 
                    Product Name: %s
                    Category: %s
                 
                    Keep it simple, engaging and highlight its primary features or benefits.
                    Avoid technical jargon and keep it customer-friendly.
                    Limit the description to 250 characters maximum.
                 
                 """, name, category);
         String description = chatClient.prompt(descriptionPrompt)
                 .advisors(a -> a.param("chat_memory_conversation_id", "user1"))
                 .call()
                 .chatResponse()
                 .getResult()
                 .getOutput()
                 .getText();
         return description;
    }
}
