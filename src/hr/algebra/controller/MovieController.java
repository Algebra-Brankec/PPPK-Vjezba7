/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.dao.RepositoryFactory;
import hr.algebra.utilities.FileUtils;
import hr.algebra.utilities.ImageUtils;
import hr.algebra.utilities.ValidationUtils;
import hr.algebra.viewmodel.MovieViewModel;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;

/**
 * FXML Controller class
 *
 * @author daniel.bele
 */
public class MovieController implements Initializable {

    private Map<TextField, Label> validationMap;

    private final ObservableList<MovieViewModel> people = FXCollections.observableArrayList();

    private MovieViewModel selectedMovieViewModel;

    @FXML
    private TabPane tpContent;
    @FXML
    private Tab tabEdit;
    @FXML
    private Tab tabList;
    @FXML
    private ImageView ivMovie;
    @FXML
    private TableView<MovieViewModel> tvMovie;
    @FXML
    private TableColumn<MovieViewModel, String> tcFirstName, tcLastName, tcEmail;
    @FXML
    private TableColumn<MovieViewModel, Integer> tcAge;
    @FXML
    private TextField tfFirstName, tfLastName, tfAge, tfEmail;
    @FXML
    private Label lbFirstNameError, lbLastNameError, lbAgeError, lbEmailError, lbIconError;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initValidation();
        initMovie();
        initTable();
        addIntegerMask(tfAge);
        setupListeners();
    }

    private void initValidation() {
        validationMap = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(tfFirstName, lbFirstNameError),
                new AbstractMap.SimpleImmutableEntry<>(tfLastName, lbLastNameError),
                new AbstractMap.SimpleImmutableEntry<>(tfAge, lbAgeError),
                new AbstractMap.SimpleImmutableEntry<>(tfEmail, lbEmailError))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void initMovie() {
        try {
            RepositoryFactory.getRepository().getMovie().forEach(person -> people.add(new MovieViewModel(person)));
        } catch (Exception ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
            new Alert(Alert.AlertType.ERROR, "Unable to load the form. Exiting...").show();
        }
    }

    private void initTable() {
        tcFirstName.setCellValueFactory(person -> person.getValue().getFirstNameProperty());
        tcLastName.setCellValueFactory(person -> person.getValue().getLastNameProperty());
        tcAge.setCellValueFactory(person -> person.getValue().getAgeProperty().asObject());
        tcEmail.setCellValueFactory(person -> person.getValue().getEmailProperty());
        tvMovie.setItems(people);
    }

    private void addIntegerMask(TextField tf) {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("\\d*")) {
                return change;
            }
            return null;
        };
        // first we must convert integer to string, and then we apply filter
        tf.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
    }

    private void setupListeners() {
        tpContent.setOnMouseClicked(event -> {
            if (tpContent.getSelectionModel().getSelectedItem().equals(tabEdit)) {
                bindMovie(null);
            }
        });
        people.addListener((ListChangeListener.Change<? extends MovieViewModel> change) -> {
            if (change.next()) {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(pvm -> {
                        try {
                            RepositoryFactory.getRepository().deleteMovie(pvm.getMovie());
                        } catch (Exception ex) {
                            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                } else if (change.wasAdded()) {
                    change.getAddedSubList().forEach(pvm -> {
                        try {
                            int idMovie = RepositoryFactory.getRepository().addMovie(pvm.getMovie());
                            pvm.getMovie().setIDMovie(idMovie);
                        } catch (Exception ex) {
                            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
            }
        });
    }

    @FXML
    private void delete(ActionEvent event) {
        if (tvMovie.getSelectionModel().getSelectedItem() != null) {
            people.remove(tvMovie.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void edit(ActionEvent event) {
        if (tvMovie.getSelectionModel().getSelectedItem() != null) {
            bindMovie(tvMovie.getSelectionModel().getSelectedItem());
            tpContent.getSelectionModel().select(tabEdit);            
        }
    }

    private void bindMovie(MovieViewModel viewModel) {
        resetForm();
        
        selectedMovieViewModel = viewModel != null ? viewModel : new MovieViewModel(null);
        tfFirstName.setText(selectedMovieViewModel.getFirstNameProperty().get());
        tfLastName.setText(selectedMovieViewModel.getLastNameProperty().get());
        tfAge.setText(String.valueOf(selectedMovieViewModel.getAgeProperty().get()));
        tfEmail.setText(selectedMovieViewModel.getEmailProperty().get());
        ivMovie.setImage(selectedMovieViewModel.getPictureProperty().get() != null ? new Image(new ByteArrayInputStream(selectedMovieViewModel.getPictureProperty().get())) : new Image(new File("src/assets/no_image.png").toURI().toString()));
    }

    private void resetForm() {
        validationMap.values().forEach(lb -> lb.setVisible(false));
        lbIconError.setVisible(false);
    }

    @FXML
    private void uploadImage(ActionEvent event) {
        File file = FileUtils.uploadFileDialog(tfAge.getScene().getWindow(), "jpg", "jpeg", "png");
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            try {
                String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                selectedMovieViewModel.getMovie().setPicture(ImageUtils.imageToByteArray(image, ext));
                ivMovie.setImage(image);
            } catch (IOException ex) {
                Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void commit(ActionEvent event) {
        if (formValid()) {
            selectedMovieViewModel.getMovie().setFirstName(tfFirstName.getText().trim());
            selectedMovieViewModel.getMovie().setLastName(tfLastName.getText().trim());
            selectedMovieViewModel.getMovie().setAge(Integer.valueOf(tfAge.getText().trim()));
            selectedMovieViewModel.getMovie().setEmail(tfEmail.getText().trim());
            if (selectedMovieViewModel.getMovie().getIDMovie() == 0) {
                people.add(selectedMovieViewModel);
            } else {
                try {
                    // we cannot listen to the upates
                    RepositoryFactory.getRepository().updateMovie(selectedMovieViewModel.getMovie());
                    tvMovie.refresh();
                } catch (Exception ex) {
                    Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            selectedMovieViewModel = null;
            tpContent.getSelectionModel().select(tabList);
            resetForm();
        }
    }

    private boolean formValid() {
        // discouraged but ok!
        final AtomicBoolean ok = new AtomicBoolean(true);
        validationMap.keySet().forEach(tf -> {
            if (tf.getText().trim().isEmpty() || tf.getId().contains("Email") && !ValidationUtils.isValidEmail(tf.getText().trim())) {
                validationMap.get(tf).setVisible(true);
                ok.set(false);
            } else {
                validationMap.get(tf).setVisible(false);
            }
        });

        if (selectedMovieViewModel.getPictureProperty().get() == null) {
            lbIconError.setVisible(true);
            ok.set(false);
        } else {
            lbIconError.setVisible(false);
        }
        return ok.get();
    }

}
