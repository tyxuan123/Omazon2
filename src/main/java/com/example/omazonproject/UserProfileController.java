package com.example.omazonproject;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

/**
 * This class acts as the controller for the user profile page
 *
 * @author XiangLun
 */
public class UserProfileController {

    /**
     * Stage is used to represent a window in a JavaFX desktop application
     */
    private Stage stage;

    /**
     * Scene is the container for all content in a scene graph
     */
    private Scene scene;

    /**
     * Root provides a solution to the issue of defining a reusable component with FXML
     */
    private Parent root;

    /**
     * A label to display user email address
     */
    @FXML
    private Label emailAddress;

    /**
     * A label to display payment password
     */
    @FXML
    private Label paymentPassword;

    /**
     * A text field to fill pick-up address
     */
    @FXML
    private TextArea pickupAddress;

    /**
     * A method to display username
     */
    @FXML
    private TextField username;

    /**
     * A button named setPaymentPasswordButton
     */
    @FXML
    private Button setPaymentPasswordButton;

    /**
     * A button to display profile icon
     */
    @FXML
    private Button profileIcon;

    /**
     * A button to display home icon
     */
    @FXML
    private Button homeIcon;

    /**
     * A circle to display user profile image
     */
    @FXML
    private Circle userProfileImage;

    /**
     * This initialize method set up tooltip message for the icons, fills the user's information, and
     * determine whether it is needed to hide the set payment password option
     */
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

        // fill user's information
        emailAddress.setText(User.getEmail());
        username.setText(User.getUsername());
        pickupAddress.setText(User.getAddress());

        // decides whether it is needed to hide the set payment password option
        if (User.getPaymentPassword() != null) {
            paymentPassword.setVisible(false);
            setPaymentPasswordButton.setVisible(false);
        }

        // load user profile image
        Image image;
        String userImageName = User.getEmail() + "-" + User.getUsername();
        File file = new File("assets/" + userImageName + ".png");

        //Check if the user profile image exists or not
        if (file.exists()) {
            //If exists, display the user profile image
            image = new Image(new File("assets/" + userImageName + ".png").toURI().toString());
            userProfileImage.setFill(new ImagePattern(image));
        }
    }

    /**
     * This method is used to let user change profile image
     * The image will be saved in a folder named assets by using user email address and username to name the image
     */
    @FXML
    public void editUserImagePressed() {
        try {
            //Upload user profile image
            Stage stage = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Upload profile image");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"), new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            Image image = new Image(selectedFile.toURI().toString(), 200, 200, false, false);
            userProfileImage.setFill(new ImagePattern(image));

            //Store user profile image in assets folder
            //user profile image name = "(user_email_address)-(user_username)"
            File fileoutput = new File("assets/" + User.getEmail() + "-" + User.getUsername() + ".png");
            BufferedImage BI = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(BI, "png", fileoutput);

        } catch (NullPointerException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will direct user to user account balance page after clicking it
     *
     * @param event An instance of the ActionEvent class
     * @throws IOException when the resource file is not found
     */
    @FXML
    void accountBalanceButtonPressed(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user-account-balance-page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method remove the user's from the database after getting the user's permission
     *
     * @param event An instance of the ActionEvent class
     */
    @FXML
    void deleteAccountButtonPressed(ActionEvent event) {
        // create a pop-up message to alert the user
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Delete Account");
        alert.setHeaderText(null);

        // create a text field
        TextField textField = new TextField();

        // create a grid pane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 40, 10, 10));

        // set up buttons
        ButtonType delete = new ButtonType("DELETE", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(delete, ButtonType.CANCEL);

        // add content into grid pane
        gridPane.add(new Label("Are you sure you want to delete your account?\nYour account will be permanently erased. Enter CONFIRM to proceed."), 0, 0);
        gridPane.add(textField, 0, 1);
        alert.getDialogPane().setContent(gridPane);

        // enable/disable the deleteButton depending on user's input
        Node deleteButton = alert.getDialogPane().lookupButton(delete);
        deleteButton.setDisable(true);
        textField.textProperty().addListener((observable, oldValue, newValue) -> deleteButton.setDisable(!newValue.equals("CONFIRM")));

        // auto-focus on the text field
        Platform.runLater(textField::requestFocus);

        // show the alert box and wait for user
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == delete) {
            // if delete button is pressed,
            // delete user's account in our database
            Connection connection = null;
            PreparedStatement psDelete = null;

            try {
                // connect to database
                DatabaseConnection db = new DatabaseConnection();
                connection = db.getConnection();

                // set up mySQL statement and execute update
                psDelete = connection.prepareStatement("DELETE FROM user_account WHERE email = ?");
                psDelete.setString(1, User.getEmail());
                psDelete.executeUpdate();

                // remove user's info in the User class
                User.setAddress(null);
                User.setUsername(null);
                User.setEmail(null);
                User.setPassword(null);
                User.setPaymentPassword(null);
                User.setBalance(0);

                // say goodbye
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Account Deleted");
                alert1.setHeaderText(null);
                alert1.setContentText("Your account was deleted successfully. All your data was removed from our services. Thank you for using Omazon.");
                alert1.showAndWait();

                // forward user to the login page
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-login-page.fxml")));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styling.css")).toExternalForm());
                stage.setScene(scene);
                stage.show();

            } catch (SQLException | IOException e) {
                e.printStackTrace();

            } finally {
                if (psDelete != null) {
                    try {
                        psDelete.close();
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

        }
    }

    /**
     * This method will direct user to the favorite list page after clicking it
     *
     * @param event An instance of the ActionEvent class
     * @throws IOException when the resource file is not found
     */
    @FXML
    void favouriteListButtonPressed(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user-favorite-list-page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method will direct user to user home page after clicking it
     *
     * @param event An instance of the ActionEvent class
     * @throws IOException when the resource file is not found
     */
    @FXML
    void homepageButtonPressed(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("home-page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method will direct user to user purchase page after clicking it
     *
     * @param event An instance of the ActionEvent class
     * @throws IOException when the resource file is not found
     */
    @FXML
    void myPurchaseButtonPressed(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("purchase-page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method will save the user's information to the database if there is any changes
     */
    @FXML
    void saveButtonPressed() {
        // when the save button is pressed

        if (!username.getText().isEmpty() && !username.getText().equals(User.getUsername())) {
            //if the username in the text field is not empty and is not the same as the username stored in the User class,
            Connection connection = null;
            PreparedStatement psUpdateUsername = null;

            try {
                //change the user's username in the database
                DatabaseConnection databaseConnection = new DatabaseConnection();
                connection = databaseConnection.getConnection();
                psUpdateUsername = connection.prepareStatement("UPDATE user_account SET username = ? WHERE email = ?");
                psUpdateUsername.setString(1, username.getText());
                psUpdateUsername.setString(2, User.getEmail());
                psUpdateUsername.executeUpdate();

                // change the user's username in the User class
                User.setUsername(username.getText());

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (psUpdateUsername != null) {
                    try {
                        psUpdateUsername.close();

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
        }

        if (User.getAddress() == null) {
            // if the user doesn't set the address before
            if (pickupAddress.getText() != null) {
                // if the address in the text field is not empty
                //change the user's address in the database
                Connection connection = null;
                PreparedStatement psUpdateAddress = null;

                try {
                    // change the user's address in the database
                    DatabaseConnection databaseConnection = new DatabaseConnection();
                    connection = databaseConnection.getConnection();
                    psUpdateAddress = connection.prepareStatement("UPDATE user_account SET address = ? WHERE email = ?");
                    psUpdateAddress.setString(1, pickupAddress.getText());
                    psUpdateAddress.setString(2, User.getEmail());
                    psUpdateAddress.executeUpdate();

                    // change the user's address in the User class
                    User.setAddress(pickupAddress.getText());

                } catch (SQLException e) {
                    e.printStackTrace();

                } finally {
                    if (psUpdateAddress != null) {
                        try {
                            psUpdateAddress.close();

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
            }
        } else {
            // if the user set the address before
            if (!pickupAddress.getText().isEmpty() && !User.getAddress().equals(pickupAddress.getText())) {
                Connection connection = null;
                PreparedStatement psUpdateAddress = null;

                try {
                    // change the user's address in the database
                    DatabaseConnection databaseConnection = new DatabaseConnection();
                    connection = databaseConnection.getConnection();
                    psUpdateAddress = connection.prepareStatement("UPDATE user_account SET address = ? WHERE email = ?");
                    psUpdateAddress.setString(1, pickupAddress.getText());
                    psUpdateAddress.setString(2, User.getEmail());
                    psUpdateAddress.executeUpdate();

                    // change the user's address in the User class
                    User.setAddress(pickupAddress.getText());

                } catch (SQLException e) {
                    e.printStackTrace();

                } finally {
                    if (psUpdateAddress != null) {
                        try {
                            psUpdateAddress.close();

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
            }
        }
    }

    /**
     * This method will direct user to seller login page after clicking it
     *
     * @param event An instance of the ActionEvent class
     * @throws IOException when the resource file is not found
     */
    @FXML
    void startSellingButtonPressed(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("seller-login-page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styling.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void changeLoginPasswordButtonPressed() throws MessagingException {
        // create a new custom dialog box with password field
        Dialog<ButtonType> preDialog = new Dialog<>();
        preDialog.setTitle("Change Login Password");
        preDialog.setHeaderText("Please enter your current password before changing the password.");

        // set up the buttons
        ButtonType forgetPasswordButtonType = new ButtonType("Forget password?");
        preDialog.getDialogPane().getButtonTypes().addAll(forgetPasswordButtonType, ButtonType.OK, ButtonType.CANCEL);

        // create a grid pane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        // create a password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter current password");

        // set contents of the grid pane
        gridPane.add(new Label("Current Password:"), 0, 0);
        gridPane.add(passwordField, 1, 0);
        preDialog.getDialogPane().setContent(gridPane);

        // focus on the password field by default
        Platform.runLater(passwordField::requestFocus);

        // check button pressed
        Optional<ButtonType> btnPressed = preDialog.showAndWait();

        if (btnPressed.isPresent() && btnPressed.get() == ButtonType.OK) {
            // if OK button is pressed,
            // check the password entered with the current password
            String currentPassword = passwordField.getText();
            if (!currentPassword.isEmpty() && currentPassword.equals(User.getPassword())) {
                // if password entered is correct
                changePassword();

            } else {
                // if the password entered does not match with the current password
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Incorrect Password");
                alert.setHeaderText("The password entered does not match with the current login password.");
                alert.setContentText("Please try again.");
                alert.showAndWait();
            }

        } else if (btnPressed.isPresent() && btnPressed.get() == forgetPasswordButtonType) {
            // if forget button is pressed,
            // inform the user that they will receive an email
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reset Password");
            alert.setHeaderText(null);
            alert.setContentText("An email containing a confirmation code will be sent to your email address.");
            alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> btn = alert.showAndWait();
            if (btn.isPresent() && btn.get() == ButtonType.OK) {
                // if the user request an email,
                // send a confirmation email to the user
                VerificationEmail verificationEmail = new VerificationEmail();
                verificationEmail.sendVerificationEmail(User.getEmail(), "forgetPassword");

                // request the confirmation code from the user
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setTitle("Confirmation Email Sent Successfully");
                textInputDialog.setHeaderText("Please enter the confirmation code.");
                textInputDialog.setContentText("Confirmation code:");
                Optional<String> code = textInputDialog.showAndWait();

                if (code.isPresent() && code.get().equals(Integer.toString(verificationEmail.verificationCode))) {
                    // if the code entered is correct,
                    // let the user change the password
                    changePassword();
                } else {
                    // If the code entered does not match,
                    // Display error pop-up message
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("Error");
                    alert1.setHeaderText("The confirmation code entered is empty or does not match.");
                    alert1.setContentText("Please try again.");
                    alert1.showAndWait();
                }
            }
        }
    }

    @FXML
    void setPaymentPasswordButtonPressed() {
        // create a dialog box to request the current password from the user
        Dialog<String> getCurrentPasswordDialog = new Dialog<>();
        getCurrentPasswordDialog.setTitle("Set Payment Password");
        getCurrentPasswordDialog.setHeaderText("Please enter your login password before setting the payment password.");

        // set up buttons
        getCurrentPasswordDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // create a new password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter current password");

        // create a grid pane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        // add contents into the grid pane
        gridPane.add(new Label("Current Password:"), 0, 0);
        gridPane.add(passwordField, 1, 0);

        // add grid pane into the dialog box
        getCurrentPasswordDialog.getDialogPane().setContent(gridPane);

        // auto-focus on the password field
        Platform.runLater(passwordField::requestFocus);

        // Convert the result to a password-confirmation password pair when the set button is clicked.
        getCurrentPasswordDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });

        // show the dialog box
        Optional<String> password = getCurrentPasswordDialog.showAndWait();

        // check the password entered
        if (password.isPresent() && password.get().equals(User.getPassword())) {
            // if the password entered matches,
            // let the user set the password

            // create a new custom dialog box with two inputs
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Set Payment Password");
            dialog.setHeaderText("Please enter your payment password.\nYou are NOT allowed to change the payment password in the future");

            // set up the button.
            ButtonType setButtonType = new ButtonType("Set", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(setButtonType, ButtonType.CANCEL);

            // create a grid pane
            GridPane gp = new GridPane();
            gp.setHgap(10);
            gp.setVgap(10);
            gp.setPadding(new Insets(20, 150, 10, 10));

            // create the required password fields and labels
            PasswordField newPass1 = new PasswordField();
            newPass1.setPromptText("Enter payment password");
            PasswordField newPass2 = new PasswordField();
            newPass2.setPromptText("Re-enter the password");
            Text warningText = new Text("The password does not match");
            warningText.setFill(Color.RED);

            // add the contents into the grid pane
            gp.add(new Label("Payment password:"), 0, 0);
            gp.add(newPass1, 1, 0);
            gp.add(new Label("Confirm payment password:"), 0, 1);
            gp.add(newPass2, 1, 1);
            gp.add(warningText, 0, 2);
            dialog.getDialogPane().setContent(gp);

            // Enable/Disable change button depending on whether the passwords match.
            // Show/Hide warning text depending on whether the passwords match.
            Node setButton = dialog.getDialogPane().lookupButton(setButtonType);
            setButton.setDisable(true);
            warningText.setVisible(false);
            newPass2.textProperty().addListener((observable, oldValue, newValue) -> {
                warningText.setVisible(!newValue.isEmpty());
                setButton.setDisable(true);
                if (newValue.equals(newPass1.getText()) && !newPass1.getText().isEmpty()) {
                    warningText.setVisible(false);
                    setButton.setDisable(false);
                }
            });
            newPass1.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.equals(newPass2.getText()) && !newPass2.getText().isEmpty()) {
                    warningText.setVisible(true);
                    setButton.setDisable(true);
                } else if (newValue.equals(newPass2.getText()) && !newPass2.getText().isEmpty()) {
                    warningText.setVisible(false);
                    setButton.setDisable(false);
                }
            });

            // request focus on the first password field
            Platform.runLater(newPass1::requestFocus);

            // Convert the result to a password-confirmation password pair when the set button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == setButtonType) {
                    return new Pair<>(newPass1.getText(), newPass2.getText());
                }
                return null;
            });

            // Show the dialog
            Optional<Pair<String, String>> result = dialog.showAndWait();

            // If the confirmation password matches
            result.ifPresent(newPassword -> {

                // Connect to the database and change the user's payment password
                Connection connection = null;
                PreparedStatement psUpdatePass = null;

                try {
                    // Change the user's payment password in the database
                    DatabaseConnection db = new DatabaseConnection();
                    connection = db.getConnection();
                    psUpdatePass = connection.prepareStatement("UPDATE user_account SET paymentPassword = ? WHERE email = ?");
                    psUpdatePass.setString(1, newPassword.getKey());
                    psUpdatePass.setString(2, User.getEmail());
                    psUpdatePass.executeUpdate();

                    // Change the user's payment password in the User class
                    User.setPaymentPassword(newPassword.getKey());

                    // Display successful pop-up message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Payment password set successfully.");
                    alert.showAndWait();

                    // Hide set payment password option
                    paymentPassword.setVisible(false);
                    setPaymentPasswordButton.setVisible(false);

                } catch (SQLException e) {
                    e.printStackTrace();

                } finally {
                    if (psUpdatePass != null) {
                        try {
                            psUpdatePass.close();

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
            });

        } else {
            // if the password entered does not match,
            // display password does not match pop-up message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incorrect Password");
            alert.setHeaderText("The password entered does not match with the current password.");
            alert.setContentText("Please try again.");
            alert.showAndWait();
        }
    }

    /**
     * This method create a dialog box that request the user to enter a new password and the confirmation password.
     * If the passwords match, change the user's account password in our database and display a successful pop-up
     * message if the password change successfully.
     *
     * @author XiangLun
     */
    public void changePassword() {
        // generate a dialog box to let the user enter their desired password to change to

        // create a new custom dialog box with two inputs
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Change Login Password");
        dialog.setHeaderText("Please enter your new password.");

        // Set up the button.
        ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        // Create a grid pane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create the required labels and password fields.
        PasswordField newPass1 = new PasswordField();
        newPass1.setPromptText("Enter new password");
        PasswordField newPass2 = new PasswordField();
        newPass2.setPromptText("Re-enter new password");
        Text warningText = new Text("The password does not match");
        warningText.setFill(Color.RED);

        // Place the labels and password fields into the grid pane
        grid.add(new Label("New password:"), 0, 0);
        grid.add(newPass1, 1, 0);
        grid.add(new Label("Confirm new password:"), 0, 1);
        grid.add(newPass2, 1, 1);
        grid.add(warningText, 0, 2);
        dialog.getDialogPane().setContent(grid);

        // Enable/Disable change button depending on whether the passwords match.
        // Show/Hide warning text depending on whether the passwords match.
        Node changeButton = dialog.getDialogPane().lookupButton(changeButtonType);
        changeButton.setDisable(true);
        warningText.setVisible(false);
        newPass2.textProperty().addListener((observable, oldValue, newValue) -> {
            warningText.setVisible(!newValue.isEmpty());
            changeButton.setDisable(true);
            if (newValue.equals(newPass1.getText()) && !newPass1.getText().isEmpty()) {
                warningText.setVisible(false);
                changeButton.setDisable(false);
            }
        });
        newPass1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(newPass2.getText()) && !newPass2.getText().isEmpty()) {
                warningText.setVisible(true);
                changeButton.setDisable(true);
            } else if (newValue.equals(newPass2.getText()) && !newPass2.getText().isEmpty()) {
                warningText.setVisible(false);
                changeButton.setDisable(false);
            }
        });

        // Request focus on the first password field by default
        Platform.runLater(newPass1::requestFocus);

        // Convert the result to a password-confirmation password pari when the button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                return new Pair<>(newPass1.getText(), newPass2.getText());
            }
            return null;
        });

        // Show the dialog
        Optional<Pair<String, String>> result = dialog.showAndWait();

        // If the confirmation password matches
        result.ifPresent(newPassword -> {

            // Check whether the password entered is same as the current password
            if (!newPassword.getKey().equals(User.getPassword())) {
                // If the password entered is not the same as the current password,
                // Connect to the database and change the user's password
                Connection connection = null;
                PreparedStatement psUpdatePass = null;

                try {
                    // Change the user's password in the database
                    DatabaseConnection db = new DatabaseConnection();
                    connection = db.getConnection();
                    psUpdatePass = connection.prepareStatement("UPDATE user_account SET password = ? WHERE email = ?");
                    psUpdatePass.setString(1, newPassword.getKey());
                    psUpdatePass.setString(2, User.getEmail());
                    psUpdatePass.executeUpdate();

                    // Change the user's password in the User class
                    User.setPassword(newPassword.getKey());

                    // Display successful pop-up message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Password changed successfully.");
                    alert.showAndWait();

                } catch (SQLException e) {
                    e.printStackTrace();

                } finally {
                    if (psUpdatePass != null) {
                        try {
                            psUpdatePass.close();

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
            } else {
                // If the password entered is the same as the current password
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("The password that you wish to change to is the same as the current password.");
                alert.setContentText("Please try again.");
                alert.showAndWait();
            }
        });

    }

}
