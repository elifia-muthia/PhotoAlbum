/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photo_album_project;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.imageio.ImageIO;

/**
 *
 * @author 44280
 */
public class AlbumFX extends Application {

    Album album;
    Stage stage;
    byte[] tempImage;

    // main page
    VBox mainVBox;
    Button btnDelete;
    TableView<Post> tblAlbum;
    Label lblTitleMain;
    Button btnAddPost, btnSaveChanges;
    Scene mainScene;

    // add photo page
    GridPane addPhotoPane;
    Label lblTitleAddPhoto, lblCaption, lblPhoto;
    Button btnBack, btnEnter, btnUploadPhoto;
    DatePicker dateEntered;
    TextArea txtCaption;
    Scene addPhotoScene;

    public AlbumFX() {
        album = new Album();

        // main page
        mainVBox = new VBox();
        btnDelete = new Button("Delete Selected Row");
        tblAlbum = new TableView<Post>();
        lblTitleMain = new Label("Photo Album");
        btnAddPost = new Button("Add New Photo");
        btnSaveChanges = new Button("Save Changes");

        // add photo page
        addPhotoPane = new GridPane();
        lblTitleAddPhoto = new Label("New Photo");
        lblCaption = new Label("Caption");
        lblPhoto = new Label();
        btnBack = new Button("Back");
        btnEnter = new Button("Add to Photo Album");
        btnUploadPhoto = new Button("Upload Photo");
        dateEntered = new DatePicker();
        txtCaption = new TextArea();
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        // Main Page
        mainVBox.setSpacing(10);
        mainVBox.setPadding(new Insets(10, 0, 0, 10));
        lblTitleMain.setFont(Font.font("Roboto", FontWeight.EXTRA_BOLD, 40));

        tblAlbum.setEditable(true);
        Callback<TableColumn<Post, String>, TableCell<Post, String>> cellFactory
                = (TableColumn<Post, String> param) -> new EditingCell();
        Callback<TableColumn<Post, Date>, TableCell<Post, Date>> dateCellFactory
                = (TableColumn<Post, Date> param) -> new DateEditingCell();

        TableColumn<Post, ImageView> photoCol = new TableColumn("Photo");
        photoCol.setMinWidth(400);
        photoCol.setCellValueFactory(new PropertyValueFactory<>("imageView"));

        TableColumn<Post, String> captionCol = new TableColumn("Caption");
        captionCol.setMinWidth(300);
        captionCol.setCellValueFactory(new PropertyValueFactory<>("caption"));
        captionCol.setCellFactory(cellFactory);
        captionCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Post, String> t) -> {
                    ((Post) t.getTableView().getItems()
                            .get(t.getTablePosition().getRow()))
                            .setCaption(t.getNewValue());
                });

        TableColumn<Post, Date> dateCol = new TableColumn("Date");
        dateCol.setMinWidth(300);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(dateCellFactory);
        dateCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Post, Date> t) -> {
                    ((Post) t.getTableView().getItems()
                            .get(t.getTablePosition().getRow()))
                            .setDate(t.getNewValue());
                });

        tblAlbum.setPrefWidth(1100);
        tblAlbum.setPrefHeight(600);
        tblAlbum.getColumns().setAll(photoCol, captionCol, dateCol);
        tblAlbum.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ObservableList<Post> posts = FXCollections.observableArrayList(album.getPosts());
        tblAlbum.setItems(posts);
        mainVBox.getChildren().addAll(lblTitleMain, btnAddPost, btnSaveChanges, tblAlbum, btnDelete);

        btnSaveChanges.setOnAction(e -> saveChanges());
        btnAddPost.setOnAction(e -> goToAddPhoto());
        btnDelete.setOnAction(e -> deletePost());

        // Add Photo Page
        addPhotoPane.setAlignment(Pos.CENTER);
        addPhotoPane.setHgap(20);
        addPhotoPane.setVgap(20);

        addPhotoPane.addRow(0, btnBack);
        addPhotoPane.add(lblTitleAddPhoto, 0, 1, 3, 1);
        lblTitleAddPhoto.setFont(Font.font("Roboto", FontWeight.EXTRA_BOLD, 40));
        addPhotoPane.setHalignment(lblTitleAddPhoto, HPos.CENTER);
        addPhotoPane.add(lblPhoto, 0, 2, 3, 5);
        addPhotoPane.setHalignment(lblPhoto, HPos.CENTER);
        try {
            Image image = new Image(getClass().getResourceAsStream("/photo_album_project/images/upload_image.png"));
            lblPhoto.setGraphic(new ImageView(image));
        } catch (Exception e) {
            e.printStackTrace();
        }
        addPhotoPane.add(btnUploadPhoto, 0, 7, 3, 1);
        addPhotoPane.setHalignment(btnUploadPhoto, HPos.CENTER);
        addPhotoPane.add(lblCaption, 0, 8);
        lblCaption.setFont(Font.font("Roboto", FontWeight.EXTRA_BOLD, 15));
        addPhotoPane.add(dateEntered, 2, 8);
        addPhotoPane.setHalignment(dateEntered, HPos.RIGHT);
        addPhotoPane.add(txtCaption, 0, 9, 3, 3);
        addPhotoPane.add(btnEnter, 0, 12, 3, 1);
        addPhotoPane.setHalignment(btnEnter, HPos.RIGHT);
        btnUploadPhoto.setOnAction(e -> uploadPhoto());
        btnBack.setOnAction(e -> goToMainPage());
        btnEnter.setOnAction(e -> addPost());

        mainScene = new Scene(mainVBox, 1400, 800);
        addPhotoScene = new Scene(addPhotoPane, 600, 800);

        stage.setTitle("Photo Album");
        stage.setScene(mainScene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void goToAddPhoto() {
        stage.setScene(addPhotoScene);
        stage.show();
    }

    private void goToMainPage() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/photo_album_project/images/upload_image.png"));
            lblPhoto.setGraphic(new ImageView(image));
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtCaption.setText("");
        dateEntered.setValue(null);
        ObservableList<Post> posts = FXCollections.observableArrayList(album.getPosts());
        tblAlbum.setItems(posts);
        stage.setScene(mainScene);
        stage.show();
    }

    private void addPost() {
        try {
            ZoneId defaultZoneId = ZoneId.systemDefault();
            LocalDate temp = dateEntered.getValue();
            Date date = Date.from(temp.atStartOfDay(defaultZoneId).toInstant());
            Post post = new Post(tempImage, txtCaption.getText(), date);
            boolean success = album.addPost(post);
            if (success) {
                goToMainPage();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Unable to Upload Photo!");
                alert.setContentText("The size of the photo is too big. Please upload a photo of a smaller size.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to Add Post to Album!");
            alert.setContentText("Please upload a photo or select a date before adding a new post to the album. ");
            alert.showAndWait();
        }
    }

    private void uploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(filter);
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                FileInputStream fis = new FileInputStream(selectedFile);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                try {
                    for (int readNum; (readNum = fis.read(buf)) != -1;) {
                        bos.write(buf, 0, readNum);
                        System.out.println("read " + readNum + " bytes,");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                tempImage = bos.toByteArray();
                ByteArrayInputStream bais = new ByteArrayInputStream(tempImage);
                BufferedImage bImage = ImageIO.read(bais);
                Image image = SwingFXUtils.toFXImage(bImage, null);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(300);
                imageView.setPreserveRatio(true);
                lblPhoto.setGraphic(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deletePost() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this client?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == yesButton) {
            Post post = tblAlbum.getSelectionModel().getSelectedItem();
            album.deletePost(post);
            tblAlbum.getItems().remove(post);
        }
    }

    private void saveChanges() {
        List<Post> posts = tblAlbum.getItems();
        album.updatePosts(posts);
        ObservableList<Post> allPosts = FXCollections.observableArrayList(album.getPosts());
        tblAlbum.setItems(allPosts);
    }
    
    
    // MLA Citation for the EditingCell and DateEditingCell classes
    // Hasan Kara. “Editable JavaFX TableView with Textfield, Datepicker and Dropdown Menue.” 
    // Github, gist.github.com/haisi/0a82e17daf586c9bab52. Accessed 7 Apr. 2020.
    private class EditingCell extends TableCell<Post, String> {

        private TextField textField;

        private EditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }
        
        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnAction((e) -> commitEdit(textField.getText()));
            textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (!newValue) {
                    System.out.println("Commiting " + textField.getText());
                    commitEdit(textField.getText());
                }
            });
        }
        
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(item);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }

    }

    class DateEditingCell extends TableCell<Post, java.util.Date> {

        private DatePicker datePicker;

        private DateEditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createDatePicker();
                setText(null);
                setGraphic(datePicker);
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getDate().toString());
            setGraphic(null);
        }

        @Override
        public void updateItem(java.util.Date item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (datePicker != null) {
                        datePicker.setValue(getDate());
                    }
                    setText(null);
                    setGraphic(datePicker);
                } else {
                    setText(getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    setGraphic(null);
                }
            }
        }

        private void createDatePicker() {
            datePicker = new DatePicker(getDate());
            datePicker.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            datePicker.setOnAction((e) -> {
                System.out.println("Committed: " + datePicker.getValue().toString());
                commitEdit(java.util.Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            });
        }

        private LocalDate getDate() {
            return getItem() == null ? LocalDate.now() : getItem().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

}
