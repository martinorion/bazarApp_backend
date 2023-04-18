package com.example.backend.Favourite;

import com.example.backend.Image.Image;
import com.example.backend.Image.ImageUtility;
import com.example.backend.Products.Product;
import com.example.backend.proSecurity.user.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FavouriteController {

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private FavouriteServiceImpl favouriteService;

    @PostMapping("/favourite")
    public void addFavourite(@RequestBody Favourite favourite, @AuthenticationPrincipal CurrentUser currentUser) {
        favouriteService.addFavourite(favourite, currentUser);
    }

    @GetMapping("/favourite")
    public ArrayList<Product> getUsersFavourite(@AuthenticationPrincipal CurrentUser currentUser) {
        List<Product> productList = favouriteService.getMeFavourite(currentUser);

        ArrayList<Product> products = new ArrayList<>();

        for(Product product : productList){
            product.setImage(
                    Image.builder()
                            .id(product.getImage().getId())
                            .type(product.getImage().getType())
                            .image(ImageUtility.decompressImage(product.getImage().getImage())).build());

            products.add(product);
        }

        return products;
    }

    @PostMapping("/removefavourite")
    public void removeFavourite(@RequestBody Product product, @AuthenticationPrincipal CurrentUser currentUser) {
        favouriteService.removeFavourite(product, currentUser);
    }
}
