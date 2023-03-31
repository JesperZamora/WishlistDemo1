package com.example.wishlistdemo1.controller;

import com.example.wishlistdemo1.model.User;
import com.example.wishlistdemo1.model.Wish;
import com.example.wishlistdemo1.repository.WishlistRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/wishlist")
@Controller
public class WishlistController {
    private WishlistRepository wishlistRepository;
    public WishlistController(WishlistRepository wishlistRepository){
        this.wishlistRepository = wishlistRepository;
    }
    @GetMapping()
    public String landingPage(){
        return "index";
    }

    @GetMapping("/create")
    public String createUser(Model model){
        User newUser = new User();
        model.addAttribute("newUser", newUser);
        return "create-user";
    }

    @PostMapping("/adduser")
    public String addUser(@ModelAttribute User newUser, Model model){
        int userId = wishlistRepository.createUser(newUser);
        model.addAttribute("firstName",newUser.getFirstName());
        model.addAttribute("lastName",newUser.getLastName());
        model.addAttribute("email",newUser.getEmail());
        model.addAttribute("password",newUser.getPassword());
        model.addAttribute("userId", userId);
        return "created-user";
    }

    @GetMapping("/mainpage/{id}")
    public String mainPage(@PathVariable int id, Model model){
        User user = wishlistRepository.getUser(id);
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());

        List<Wish> wishList = wishlistRepository.getWishList(id);
        model.addAttribute("wishlist", wishList);
        return "main-page";
    }

    @GetMapping("/createwish/{id}")
    public String createWish(@PathVariable int id, Model model){
        Wish newWish = new Wish();
        newWish.setUserId(id);
        model.addAttribute("newWish", newWish);
        return "create-wish";
    }

    @PostMapping("/addwish")
    public String addWish(@ModelAttribute Wish newWish){
        wishlistRepository.createWish(newWish);
        return "redirect:/wishlist/mainpage/" + newWish.getUserId();
    }

    @GetMapping("/updatewish")
    public String updateWish(){
        return "";
    }

    @GetMapping("/updateuser")
    public String updateUser(){
        return "";
    }

    @PostMapping("/updateuser")
    public String updateUser(@ModelAttribute User user){
        return "";
    }

    @GetMapping("/deletewish/{uid}/{wid}")
    public String deleteWish(@PathVariable int uid, @PathVariable int wid){
        wishlistRepository.deleteWish(wid);
        return "redirect:/wishlist/mainpage/" + uid;
    }

    @GetMapping("/deleteuser")
    public String deleteuser(){
        return "";
    }
}
