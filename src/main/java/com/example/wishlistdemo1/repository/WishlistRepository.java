package com.example.wishlistdemo1.repository;

import com.example.wishlistdemo1.dto.UserWishListDTO;
import com.example.wishlistdemo1.model.User;
import com.example.wishlistdemo1.model.Wish;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WishlistRepository {
    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String user_id;
    @Value("${spring.datasource.password}")
    String user_pwd;

    public int createUser(User newUser) {
        int userId = 0;
        try (Connection con = DriverManager.getConnection(url, user_id, user_pwd)) {
            String SQL = "INSERT INTO users (first_name, last_name, user_email, user_password) values (?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, newUser.getFirstName());
            pstmt.setString(2, newUser.getLastName());
            pstmt.setString(3, newUser.getEmail());
            pstmt.setString(4, newUser.getPassword());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if(rs.next()){
                userId = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userId;
    }

    public User getUser(int id){
        User user = new User();
        try (Connection con = DriverManager.getConnection(url, user_id, user_pwd)) {
            String SQL = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setInt(1,id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                user.setUserId(rs.getInt("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createWish(Wish newWish){
        try(Connection con = DriverManager.getConnection(url,user_id,user_pwd)) {
            String SQL = "INSERT INTO wishlist(wish_title, wish_description, wish_url, user_id) VALUES(?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1,newWish.getTitle());
            pstmt.setString(2,newWish.getDescription());
            pstmt.setString(3,newWish.getUrl());;
            pstmt.setInt(4,newWish.getUserId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Wish> getWishList(int usersId){
        List<Wish> wishList = new ArrayList<>();
        try(Connection con = DriverManager.getConnection(url,user_id,user_pwd)) {
            String SQL = "SELECT * FROM wishlist WHERE user_id = ?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setInt(1, usersId);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int wishId = rs.getInt("wish_id");
                String title = rs.getString("wish_title");
                String description = rs.getString("wish_description");
                String url = rs.getString("wish_url");
                int id = rs.getInt("user_id");

                wishList.add(new Wish(wishId,title,description,url,id));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return wishList;
    }

    public Wish getSpecificWish(int wishId){
        Wish wishFound = null;
        try(Connection con = DriverManager.getConnection(url,user_id,user_pwd)){
            String SQL = "SELECT * FROM wishlist WHERE wish_id = ?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setInt(1,wishId);

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                wishFound = new Wish(
                        rs.getInt("wish_id"),
                        rs.getString("wish_title"),
                        rs.getString("wish_description"),
                        rs.getString("wish_url"),
                        rs.getInt("user_id"));

            }
            return wishFound;

        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void updateWish(Wish wish){
        try(Connection con = DriverManager.getConnection(url,user_id,user_pwd)) {
            String SQL = "UPDATE wishlist SET wish_title=?, wish_description=?, wish_url=? WHERE wish_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1,wish.getTitle());
            pstmt.setString(2,wish.getDescription());
            pstmt.setString(3,wish.getUrl());
            pstmt.setInt(4,wish.getWishId());

            pstmt.executeUpdate();

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void updateUser(){

    }

    public void deleteWish(int wishId){
        try(Connection con = DriverManager.getConnection(url,user_id,user_pwd)) {
            String SQL = "DELETE FROM wishlist WHERE wish_id = ?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setInt(1, wishId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(int userId){
        try(Connection con = DriverManager.getConnection(url,user_id,user_pwd)) {
            String SQL1 = "DELETE FROM wishlist WHERE user_id = ?";
            PreparedStatement pstmt1 = con.prepareStatement(SQL1);
            pstmt1.setInt(1,userId);
            pstmt1.executeUpdate();

            String SQL2 = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement pstmt2 = con.prepareStatement(SQL2);
            pstmt2.setInt(1,userId);
            pstmt2.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserWishListDTO getUserAndWishes(int id){
        UserWishListDTO userWishes = null;

        try(Connection con = DriverManager.getConnection(url,user_id,user_pwd)){
            String SQL = "SELECT users.user_id, first_name, last_name, wish_id, wish_title, wish_description, wish_url, wishlist.user_id  FROM users LEFT JOIN wishlist ON users.user_id = wishlist.user_id WHERE users.user_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            int currentID = 0;

            while (rs.next()){
                //user first and last name
                int userID = rs.getInt("users.user_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                // users wishes
                int wishId = rs.getInt("wish_id");
                String title = rs.getString("wish_title");
                String description = rs.getString("wish_description");
                String url = rs.getString("wish_url");
                int uID = rs.getInt("wishlist.user_id");


                if(currentID == id) {
                    userWishes.addWish(new Wish(wishId, title, description, url, uID));
                } else {
                    if(userWishes == null && title != null) {
                        userWishes = new UserWishListDTO(userID, firstName, lastName, new ArrayList<>(List.of(new Wish(wishId, title, description, url, uID))));
                        currentID = userID;
                    } else {
                        userWishes = new UserWishListDTO(userID, firstName, lastName, new ArrayList<>());
                    }
                }

            }
            return userWishes;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    // Some of the code can be used for login
/*    public UserInfoWishDTO getUserInfoWish(int id) {
        UserInfoWishDTO userInfoWish = null;
        try (Connection con = DriverManager.getConnection(url, user_id, user_pwd)) {
            String SQL = "SELECT users.user_id, first_name, last_name, wish_id, wish_title, wish_description, wish_url FROM users JOIN wishlist ON users.user_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            int currentId = 0;

            while(rs.next()){
                int userId = rs.getInt("user_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int wishId = rs.getInt("wish_id");
                String title = rs.getString("wish_title");
                String description = rs.getString("wish_description");
                String url = rs.getString("wish_url");

                if(currentId == userId ){
                    userInfoWish.addWish(new Wish(wishId,title,description,url, userId));
                } else {
                    userInfoWish = new UserInfoWishDTO(userId,firstName,lastName,
                            new ArrayList<>(List.of(new Wish(wishId,title,description,url, userId))));
                }
            }

            return userInfoWish;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/
}
