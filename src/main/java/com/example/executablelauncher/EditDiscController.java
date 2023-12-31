package com.example.executablelauncher;

import com.example.executablelauncher.entities.Disc;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditDiscController {
    //region FXML ATTRIBUTES
    @FXML
    private Button cancelButton;

    @FXML
    private TextField fileField;

    @FXML
    private Label fileText;

    @FXML
    private VBox generalBox;

    @FXML
    private Button generalViewButton;

    @FXML
    private FlowPane imagesContainer;

    @FXML
    private TextField orderField;

    @FXML
    private Label orderText;

    @FXML
    private TextField nameField;

    @FXML
    private Label nameText;

    @FXML
    private ScrollPane posterBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button selectImageButton;

    @FXML
    private Button thumbnailsViewButton;

    @FXML
    private Label titleText;

    @FXML
    private Button urlImageLoadButton;
    //endregion

    private DesktopViewController controllerParent;
    private List<File> imagesFiles = new ArrayList<>();
    private File selectedImage = null;
    public Disc discToEdit = null;

    //region INITIALIZATION
    public void setDisc(Disc d){
        discToEdit = d;
        fileField.setText(d.getExecutableSrc());

        titleText.setText(App.textBundle.getString("episodeWindowTitleEdit"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        saveButton.setText(App.buttonsBundle.getString("saveButton"));
        fileText.setText(App.textBundle.getString("file"));
        nameText.setText(App.textBundle.getString("name"));

        selectedImage = new File(d.imgSrc);
        nameField.setText(d.getName());

        fileText.setDisable(true);

        showGeneralView();
    }
    public void setParentController(DesktopViewController controller){
        controllerParent = controller;
    }
    @FXML
    void cancelButton(ActionEvent event) {
        controllerParent.hideBackgroundShadow();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }
    //endregion

    //region THUMBNAILS
    public void setImageFile(String path){
        selectedImage = new File(path);
    }
    @FXML
    void downloadThumbnail(ActionEvent event) {

    }
    @FXML
    void loadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(App.textBundle.getString("selectImage"));
        if (App.lastDirectory != null && Files.exists(Path.of(App.lastDirectory))){
            if (new File(App.lastDirectory).getParentFile() != null)
                fileChooser.setInitialDirectory(new File(new File(App.lastDirectory).getParentFile().getAbsolutePath()));
            else
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }else
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(App.textBundle.getString("allImages"), "*.jpg", "*.png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(((Button) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            selectedImage = selectedFile;
            App.lastDirectory = selectedFile.getPath().substring(0, (selectedFile.getPath().length() - selectedFile.getName().length()));

            int number = -1;

            for (File file : imagesFiles){
                if (Integer.parseInt(file.getName().substring(0, file.getName().lastIndexOf("."))) > number){
                    number = Integer.parseInt(file.getName().substring(0, file.getName().lastIndexOf(".")));
                }
            }

            File newFile = new File("src/main/resources/img/discCovers/" + discToEdit.id + "/" + (number + 1) + ".png");

            try{
                Files.copy(selectedImage.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
                System.err.println("Thumbnail not copied");
            }

            imagesFiles.add(newFile);
            addImage(newFile);
        }
    }
    @FXML
    void loadUrlThumbnail(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("urlPaster-view.fxml"));
            Parent root1 = fxmlLoader.load();
            UrlPasterController controller = fxmlLoader.getController();
            controller.setDiscParent(this);
            controller.initValues(false);
            Stage stage = new Stage();
            stage.setTitle("ImageURLDownloader");
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            stage.setScene(scene);
            App.setPopUpProperties(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadImage(String src){
        File file = new File(src);
        if (file.exists()) {
            selectedImage = file;

            int number = 0;

            for (File f : imagesFiles){
                if (Integer.parseInt(f.getName().substring(0, f.getName().lastIndexOf("."))) > number){
                    number = Integer.parseInt(f.getName().substring(0, f.getName().lastIndexOf(".")));
                }
            }

            File newFile = new File("src/main/resources/img/discCovers/" + discToEdit.id + "/" + (number + 1) + ".png");

            try{
                Files.copy(selectedImage.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
                System.err.println("Thumbnail not copied");
            }

            imagesFiles.add(file);
            addImage(file);
        }
    }
    //endregion

    //region SECTIONS
    private void showImages() {
        if (imagesFiles.isEmpty())
            loadImages();
    }
    private void loadImages(){
        //Add images to view
        File dir = new File("src/main/resources/img/discCovers/" + discToEdit.id);
        if (dir.exists()){
            File[] files = dir.listFiles();
            assert files != null;
            imagesFiles.addAll(Arrays.asList(files));

            for (File f : imagesFiles){
                addImage(f);

                if (selectedImage != null && selectedImage.getAbsolutePath().equals(f.getAbsolutePath())){
                    selectButton((Button) imagesContainer.getChildren().get(imagesFiles.indexOf(f)));
                }
            }
        }
    }
    private void addImage(File file){
        try{
            Image img = new Image(file.toURI().toURL().toExternalForm(), 150, 84, true, true);
            ImageView image = new ImageView(img);

            Button btn = new Button();
            btn.setGraphic(image);
            btn.setText("");
            btn.getStyleClass().add("downloadedImageButton");
            btn.setPadding(new Insets(2));

            btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                selectButton(btn);
            });

            imagesContainer.getChildren().add(btn);
        } catch (MalformedURLException e) {
            System.err.println("EditDiscController: Error loading image thumbnail");
        }
    }
    private void selectButton(Button btn){
        int index = 0;
        int i = 0;
        for (Node n : imagesContainer.getChildren()){
            Button b = (Button)n;
            b.getStyleClass().clear();
            b.getStyleClass().add("downloadedImageButton");
            if (b == btn)
                index = i;
            i++;
        }

        btn.getStyleClass().clear();
        btn.getStyleClass().add("downloadedImageButtonSelected");

        selectedImage = imagesFiles.get(index);
    }
    @FXML
    void showGeneralView() {
        posterBox.setVisible(false);
        generalBox.setVisible(true);

        generalViewButton.getStyleClass().clear();
        generalViewButton.getStyleClass().add("buttonSelected");

        thumbnailsViewButton.getStyleClass().clear();
        thumbnailsViewButton.getStyleClass().add("editButton");
    }
    @FXML
    void showThumbnailsView() {
        posterBox.setVisible(true);
        generalBox.setVisible(false);

        thumbnailsViewButton.getStyleClass().clear();
        thumbnailsViewButton.getStyleClass().add("buttonSelected");

        generalViewButton.getStyleClass().clear();
        generalViewButton.getStyleClass().add("editButton");

        showImages();
    }
    //endregion

    @FXML
    void save(ActionEvent event) {

        if (nameField.getText().isEmpty()){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("emptyField"));
            return;
        }

        if (!orderField.getText().isEmpty() && orderField.getText().matches("\\d{3,}")){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("sortingError"));
            return;
        }

        discToEdit.name = nameField.getText();

        if (!orderField.getText().isEmpty() && !orderField.getText().equals("0"))
            discToEdit.setOrder(Integer.parseInt(orderField.getText()));

        if (selectedImage != null)
            discToEdit.imgSrc = "src/main/resources/img/discCovers/" + discToEdit.id + "/" + selectedImage.getName();

        controllerParent.hideBackgroundShadow();
        controllerParent.updateDisc(discToEdit);

        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
