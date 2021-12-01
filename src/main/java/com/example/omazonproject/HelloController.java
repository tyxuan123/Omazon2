package com.example.omazonproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible to control the events happening in the login and sign-up page
 */
public class HelloController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    // Email entered by user at the login page
    private TextField emailEntered_Login;

    @FXML
    // Password entered by user at the login page
    private PasswordField passwordEntered_Login;

    @FXML
    // Confirm password entered by user at the sign-up page
    private PasswordField confirmPassword_SignUp;

    @FXML
    // Email entered by user at the sign-up page
    private TextField emailEntered_SignUp;

    @FXML
    // Password entered by user at the sign-up page
    private PasswordField passwordEntered_SignUp;

    @FXML
    // Username entered by user at the sign-up page
    private TextField username_SignUp;

    @FXML
    // The "Please enter email and password" label
    private Label loginMessageLabel;

    @FXML
    // The "Password does not match or is empty" label
    private Label notMatchLabel;

    @FXML
    // Sign-up button pressed at the sign-up page
    // This method will check all the information entered by the user while the user is signing up
    public void signUpButtonPressed(MouseEvent event) {
        // hide the "Password does not match or is empty" label
        notMatchLabel.setVisible(false);

        // Determine whether the username is empty,
        if (!username_SignUp.getText().isBlank()) {
            // If username entered is not empty,
            // Validate the email address entered by the user
            if (valEmail(emailEntered_SignUp.getText())) {
                // If valid email address is entered,
                // Determine whether the confirmation password is equal to password
                if (passwordEntered_SignUp.getText().equals(confirmPassword_SignUp.getText()) && ((!passwordEntered_SignUp.getText().isBlank())) && ((!confirmPassword_SignUp.getText().isBlank()))) {
                    // If password and confirmation password matches,
                    // 1. Send verification email

                    // 2. Check code entered

                    // 3. Register the user when the verification code matches, otherwise, let the user re-enter the email or choose to re-send the email.
                    // registerUser();

                    // 4.Display login successful pop-up message
                    Alert alert = new Alert(Alert.AlertType.NONE);
                    alert.setGraphic(new ImageView(Objects.requireNonNull(this.getClass().getResource("GreenTick.gif")).toString()));
                    alert.setTitle("Success");
                    alert.setHeaderText("Your account has been created.");
                    alert.setContentText("Thank you for signing up at Omazon :D");
                    alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    alert.showAndWait();

                } else {
                    // If password and confirmation password does not match or is empty,
                    // Show the "password does not match or is empty" label
                    notMatchLabel.setVisible(true);
                }

            } else {
                // If invalid email address is entered,
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid email address");
                alert.setHeaderText("The email address entered either is invalid or empty.");
                alert.setContentText("Please re-enter a valid email address.");
                alert.showAndWait();
            }
        } else {
            // If username entered is empty,
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid username");
            alert.setHeaderText("The username entered is empty.");
            alert.setContentText("Please re-enter a valid username.");
            alert.showAndWait();
        }

    }

    @FXML
    // Prompt sign-up button pressed in the login page
    public void signUpPromptButtonPressed(MouseEvent event) throws IOException {
        // Forward user to the sign-up page
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("RegisterPage.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    // Quit button in the sign-up page pressed
    public void registrationPageQuitButtonPressed(MouseEvent event) throws IOException {
        // Forward user to the login page
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    // Login button pressed in the login page
    public void loginButtonPressed(MouseEvent event) throws IOException {
        // hide the "Please enter email and password" label
        loginMessageLabel.setVisible(false);

        if (!emailEntered_Login.getText().isBlank() && !passwordEntered_Login.getText().isBlank()) {
            //validateLogin();
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("homepage.fxml")));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            loginMessageLabel.setVisible(true);
        }
    }

    /**
     * This method was made to validate the email entered by user in the sign-up page
     *
     * @param input the email entered by the user
     * @return a boolean value indicating the validity of the email
     */
    public static boolean valEmail(String input) {
        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPat = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPat.matcher(input);
        return matcher.find();
    }

    public void registerUser() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String username = username_SignUp.getText();
        String email = emailEntered_SignUp.getText();
        String password = passwordEntered_SignUp.getText();
        String insertFields = "INSERT INTO user_account (username, email, password) VALUES ('";
        String insertValues = username + "','" + email + "','" + password + "')";
        String insertToRegister = insertFields + insertValues;

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertToRegister);
        } catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void validateLogin() {

        try {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            Statement statement = connectDB.createStatement();
            String email = emailEntered_Login.getText();
            String password = passwordEntered_Login.getText();
            ResultSet queryResult = statement.executeQuery("SELECT * FROM user_account WHERE email = '" + email + "' AND password ='" + password + "'");
            if (queryResult.next()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Login Successful!");
                alert.setHeaderText(null);
                alert.setContentText("Welcome to Omazon");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid credentials. Please re-enter a valid credentials.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

