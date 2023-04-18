package com.example.backend.Category;

import com.example.backend.Image.Image;
import com.example.backend.Image.ImageUtility;
import com.example.backend.ImageOfCategory.ImageOfCategory;
import com.example.backend.ImageOfCategory.ImageOfCategoryRepository;
import com.example.backend.Products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ImageOfCategoryRepository imageOfCategoryRepository;

   @PostMapping("/createcategory")
   public void createCategory(@RequestBody Category category){
       Long image = imageOfCategoryRepository.findTopByOrderByIdDesc().get().getId();
       category.setImageOfCategory(imageOfCategoryRepository.findById(image).get());
       categoryRepository.save(category);
   }

   @GetMapping("/getallcategories")
    public List<Category> getAllCategories(){
        List<Category> categoryList = (List<Category>) categoryRepository.findAll();

       ArrayList<Category> categories = new ArrayList<>();

       for(Category category : categoryList){
           category.setImageOfCategory(
                   ImageOfCategory.builder()
                           .id(category.getImageOfCategory().getId())
                           .type(category.getImageOfCategory().getType())
                           .image(ImageUtility.decompressImage(category.getImageOfCategory().getImage())).build());

           categories.add(category);
       }

       return categories;
    }

}
