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
import hr.algebra.viewmodel.PersonViewModel;
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
public class PeopleController implements Initializable {

    private Map<TextField, Label> validationMap;

    private final ObservableList<PersonViewModel> people = FXCollections.observableArrayList();

    private PersonViewModel selectedPersonViewModel;

    @FXML
    private TabPane tpContent;
    @FXML
    private Tab tabEdit;
    @FXML
    private Tab tabList;
    @FXML
    private ImageView ivPerson;
    @FXML
    private TableView<PersonViewModel> tvPeople;
    @FXML
    private TableColumn<PersonViewModel, String> tcFirstName, tcLastName, tcEmail;
    @FXML
    private TableColumn<PersonViewModel, Integer> tcAge;
    @FXML
    private TextField tfFirstName, tfLastName;
    @FXML
    private Label lbFirstNameError, lbLastNameError, lbIconError;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initValidation();
        initPeople();
        initTable();
        setupListeners();
    }

    private void initValidation() {
        validationMap = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(tfFirstName, lbFirstNameError),
                new AbstractMap.SimpleImmutableEntry<>(tfLastName, lbLastNameError))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void initPeople() {
        try {
            RepositoryFactory.getRepository().getPeople().forEach(person -> people.add(new PersonViewModel(person)));
        } catch (Exception ex) {
            Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
            new Alert(Alert.AlertType.ERROR, "Unable to load the form. Exiting...").show();
        }
    }

    private void initTable() {
        tcFirstName.setCellValueFactory(person -> person.getValue().getFirstNameProperty());
        tcLastName.setCellValueFactory(person -> person.getValue().getLastNameProperty());
        tvPeople.setItems(people);
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
                bindPerson(null);
            }
        });
        people.addListener((ListChangeListener.Change<? extends PersonViewModel> change) -> {
            if (change.next()) {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(pvm -> {
                        try {
                            RepositoryFactory.getRepository().deletePerson(pvm.getPerson());
                        } catch (Exception ex) {
                            Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                } else if (change.wasAdded()) {
                    change.getAddedSubList().forEach(pvm -> {
                        try {
                            int idPerson = RepositoryFactory.getRepository().addPerson(pvm.getPerson());
                            pvm.getPerson().setIDPerson(idPerson);
                        } catch (Exception ex) {
                            Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
            }
        });
    }

    @FXML
    private void delete(ActionEvent event) {
        if (tvPeople.getSelectionModel().getSelectedItem() != null) {
            people.remove(tvPeople.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void edit(ActionEvent event) {
        if (tvPeople.getSelectionModel().getSelectedItem() != null) {
            bindPerson(tvPeople.getSelectionModel().getSelectedItem());
            tpContent.getSelectionModel().select(tabEdit);            
        }
    }

    private void bindPerson(PersonViewModel viewModel) {
        resetForm();
        
        selectedPersonViewModel = viewModel != null ? viewModel : new PersonViewModel(null);
        tfFirstName.setText(selectedPersonViewModel.getFirstNameProperty().get());
        tfLastName.setText(selectedPersonViewModel.getLastNameProperty().get());
        ivPerson.setImage(selectedPersonViewModel.getPictureProperty().get() != null ? new Image(new ByteArrayInputStream(selectedPersonViewModel.getPictureProperty().get())) : new Image(new File("src/assets/no_image.png").toURI().toString()));
    }

    private void resetForm() {
        validationMap.values().forEach(lb -> lb.setVisible(false));
        lbIconError.setVisible(false);
    }

    @FXML
    private void uploadImage(ActionEvent event) {
        File file = FileUtils.uploadFileDialog(tfLastName.getScene().getWindow(), "jpg", "jpeg", "png");
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            try {
                String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                selectedPersonViewModel.getPerson().setPicture(ImageUtils.imageToByteArray(image, ext));
                ivPerson.setImage(image);
            } catch (IOException ex) {
                Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void commit(ActionEvent event) {
        if (formValid()) {
            selectedPersonViewModel.getPerson().setFirstName(tfFirstName.getText().trim());
            selectedPersonViewModel.getPerson().setLastName(tfLastName.getText().trim());
            if (selectedPersonViewModel.getPerson().getIDPerson() == 0) {
                people.add(selectedPersonViewModel);
            } else {
                try {
                    // we cannot listen to the upates
                    RepositoryFactory.getRepository().updatePerson(selectedPersonViewModel.getPerson());
                    tvPeople.refresh();
                } catch (Exception ex) {
                    Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            selectedPersonViewModel = null;
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

        if (selectedPersonViewModel.getPictureProperty().get() == null) {
            lbIconError.setVisible(true);
            ok.set(false);
        } else {
            lbIconError.setVisible(false);
        }
        return ok.get();
    }

}
