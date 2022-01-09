package com.example.omazonproject;

import com.mysql.cj.protocol.PacketReceivedTimeHolder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Optional;

import javafx.geometry.Bounds;

import javax.mail.MessagingException;

/**
 * This class acts as a controller for the product page
 */
public class AutoFillProductPageClass {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private Product currentProduct;
    DecimalFormat df = new DecimalFormat("0.00");

    @FXML
    private ImageView productImage;

    @FXML
    private Label name;

    @FXML
    private Label category;

    @FXML
    private Label country;

    @FXML
    private Label priceLabel;

    @FXML
    private Label productDescription;

    @FXML
    private Label quantity;

    @FXML
    private Label shipFrom;

    @FXML
    private Label stock;

    @FXML
    private Button profileIcon;

    @FXML
    private Button homeIcon;

    @FXML
    private ImageView favorite;

    @FXML
    public void autoFill(Product product) {

        //Save the object of the current product
        this.currentProduct = product;

        //Autofill the product information into product page
        priceLabel.setText(String.valueOf(df.format(product.getProductPrice())));
        category.setText(product.getCategory());
        name.setText(product.getProductName());
        country.setText("Malaysia");
        stock.setText(String.valueOf(product.getNumOfStock()));
        shipFrom.setText(product.getAddress());
        productDescription.setText(product.getDescription());

        URL u = this.getClass().getResource("/images/" + product.getProductImagePath() + ".png");
        Image image = new Image(String.valueOf(u));
        productImage.setImage(image);
    }

    @FXML
    public void initialize() {
        // Show tooltip message when user point at the icon
        final Tooltip tooltipProfile = new Tooltip();
        tooltipProfile.setText("My Profile");
        profileIcon.setTooltip(tooltipProfile);
        profileIcon.getTooltip().setOnShowing(p -> {
            Bounds bProfile = profileIcon.localToScreen(profileIcon.getBoundsInLocal());
            profileIcon.getTooltip().setX(bProfile.getMaxX() - 70);
            profileIcon.getTooltip().setY(bProfile.getMinY() + 35);
        });

        final Tooltip tooltipHome = new Tooltip();
        tooltipHome.setText("Homepage");
        homeIcon.setTooltip(tooltipHome);
        homeIcon.getTooltip().setOnShowing(h -> {
            Bounds bHome = homeIcon.localToScreen(homeIcon.getBoundsInLocal());
            homeIcon.getTooltip().setX(bHome.getMaxX() - 60);
            homeIcon.getTooltip().setY(bHome.getMinY() + 35);
        });
    }

    @FXML
    void homeButtonPressed(MouseEvent event) throws IOException {

        // Forward user to home page
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("home-page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styling.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void userProfileButtonPressed(MouseEvent event) throws IOException {

        // prevent autofocus to the text field
        Platform.runLater(() -> root.requestFocus());

        // Forward user to user profile page
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user-profile-page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styling.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void addToCartButtonPressed(ActionEvent event) {
        JsonFileUtil jsonFileUtil = new JsonFileUtil();
        // use button to choose quantity
        jsonFileUtil.writeCartFile(currentProduct, Integer.parseInt(quantity.getText()));

        // inform the user that the item is added to cart successfully
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successful");
        alert.setHeaderText(null);
        alert.setContentText("Item added to cart successfully.");
        alert.showAndWait();

    }

    @FXML
    void decreaseQuantityButtonPressed(ActionEvent event) {
        int currentQuantity = Integer.parseInt(quantity.getText());
        if (currentQuantity > 1) {
            quantity.setText(Integer.toString(currentQuantity - 1));
        }
    }

    @FXML
    void increaseQuantityButtonPressed(ActionEvent event) {
        int currentQuantity = Integer.parseInt(quantity.getText());
        quantity.setText(Integer.toString(currentQuantity + 1));
    }

    @FXML
    void favoriteButtonPressed(ActionEvent event) {
        JsonFileUtil jsonFileUtil = new JsonFileUtil();
        jsonFileUtil.writeFavoriteFile(currentProduct);
        URL icon = this.getClass().getResource("/images/favoriteButtonPressed.png");
        Image image = new Image(String.valueOf(icon));
        favorite.setImage(image);
    }

    @FXML
    void BuyNowButtonPressed(ActionEvent event) {
        // calculate the total price in 2 decimal places
        double totalPrice = (int) (Integer.parseInt(quantity.getText()) * Double.parseDouble(priceLabel.getText()) * 100 + 0.5) / 100.0;

        // check whether the user have enough balance
        if (User.getPaymentPassword() == null) {
            // if the user haven't set the payment password before, ask them to set it first
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("A Payment Password is Required");
            alert.setHeaderText(null);
            alert.setContentText("Please set your payment password at the profile page.");
            alert.showAndWait();

        } else if (User.getBalance() < totalPrice) {
            // if the user does not have enough balance, ask them to top up
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Payment Unsuccessful");
            alert.setHeaderText(null);
            alert.setContentText("You do not have sufficient balance to proceed with this payment.");
            alert.showAndWait();

        } else {
            // if the user do have enough balance
            // request payment password
            TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.setTitle("Processing Payment");
            textInputDialog.setHeaderText("Please enter your payment password.");
            textInputDialog.setContentText("Payment password");

            Optional<String> result = textInputDialog.showAndWait();
            if (result.isPresent() && result.get().equals(User.getPaymentPassword())) {
                // if payment password entered is correct
                // subtract the amount from the user's balance in the User class
                double balance = User.getBalance() - totalPrice;
                User.setBalance(balance);

                // connect to the database and update the new balance
                Connection connection = null;
                PreparedStatement psUpdate = null;

                try {
                    DatabaseConnection db = new DatabaseConnection();
                    connection = db.getConnection();

                    psUpdate = connection.prepareStatement("UPDATE user_account SET balance = ? WHERE email = ?");
                    psUpdate.setString(1, String.format("%.2f", balance));
                    psUpdate.setString(2, User.getEmail());
                    psUpdate.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();

                } finally {
                    if (psUpdate != null) {
                        try {
                            psUpdate.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // add ordered item into orders.json file
                JsonFileUtil jsonFileUtil = new JsonFileUtil();
                jsonFileUtil.writeOrdersFile(currentProduct, Integer.parseInt(quantity.getText()));

                try {
                    // send notification email to the seller
                    Email.sendNotification(currentProduct.getSellerEmail(), currentProduct.getProductName(), Integer.parseInt(quantity.getText()), currentProduct.getProductPrice());

                } catch (MessagingException e) {
                    e.printStackTrace();
                }

                // inform the user that the payment is successful
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Payment successful");
                alert.setHeaderText(null);
                alert.setContentText("The payment is successful. You can view your orders at Profile > My Purchase > Orders.");
                alert.showAndWait();
            } else {
                // if the payment password is incorrect
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Payment Unsuccessful");
                alert.setHeaderText(null);
                alert.setContentText("The payment password entered is incorrect.");
                alert.showAndWait();

            }
        }
    }
}