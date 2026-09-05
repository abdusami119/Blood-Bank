import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class BloodBankGUI extends Application {
    private static final String[] BLOOD_GROUPS = {
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    };

    private final BloodBank bloodBank = new BloodBank();
    private final ObservableList<DonorRow> donorRows = FXCollections.observableArrayList();
    private final ObservableList<HospitalRow> hospitalRows = FXCollections.observableArrayList();
    private final ObservableList<RequestRow> requestRows = FXCollections.observableArrayList();
    private final ObservableList<FamilyRow> familyRows = FXCollections.observableArrayList();
    private final Label statusLabel = new Label("Ready");
    private final Label[] stockLabels = new Label[BLOOD_GROUPS.length];

    @Override
    public void start(Stage stage) {
        stage.setTitle("Blood Bank Management System");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");
        root.setTop(createHeader());
        root.setCenter(createTabs());
        root.setBottom(createStatusBar());

        Scene scene = new Scene(root, 1180, 760);
        scene.getStylesheets().add(getClass().getResource("/blood-bank.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private VBox createHeader() {
        Label title = new Label("Blood Bank Management System");
        title.getStyleClass().add("header-title");
        Label subtitle = new Label("এক ফোঁটা রক্ত, বাঁচাতে পারে একটি প্রাণ — আসুন সবাই মিলে রক্তদান করি");
        subtitle.getStyleClass().add("header-subtitle");

        StackPane bloodLogo = new StackPane();
        Circle logoCircle = new Circle(24);
        logoCircle.getStyleClass().add("logo-circle");
        Polygon logoDrop = new Polygon(0, -22, -13, 5, 0, 18, 13, 5);
        logoDrop.getStyleClass().add("logo-drop");
        Label logoText = new Label("B+");
        logoText.getStyleClass().add("logo-text");
        bloodLogo.getChildren().addAll(logoCircle, logoDrop, logoText);

        VBox titleBox = new VBox(3, title, subtitle);
        HBox headerContent = new HBox(16, bloodLogo, titleBox);
        headerContent.setAlignment(Pos.CENTER_LEFT);
        VBox header = new VBox(headerContent);
        header.getStyleClass().add("header");
        return header;
    }

    private TabPane createTabs() {
        TabPane tabs = new TabPane();
        Tab donorTab = new Tab("Donors", createDonorPanel());
        Tab stockTab = new Tab("Blood Stock", createStockPanel());
        Tab hospitalTab = new Tab("Hospitals", createHospitalPanel());
        Tab patientTab = new Tab("Patients", createPatientPanel());
        Tab familyTab = new Tab("Family Records", createFamilyPanel());
        donorTab.setClosable(false);
        stockTab.setClosable(false);
        hospitalTab.setClosable(false);
        patientTab.setClosable(false);
        familyTab.setClosable(false);
        tabs.getTabs().addAll(donorTab, stockTab, hospitalTab, patientTab, familyTab);
        return tabs;
    }

    private VBox createDonorPanel() {
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        ComboBox<String> bloodGroupBox = new ComboBox<>(FXCollections.observableArrayList(BLOOD_GROUPS));
        TextField locationField = new TextField();
        ComboBox<String> donorTypeBox = new ComboBox<>(
                FXCollections.observableArrayList("Paid", "Unpaid"));
        Spinner<Integer> donationSpinner = new Spinner<>();
        donationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        bloodGroupBox.getSelectionModel().selectFirst();
        donorTypeBox.getSelectionModel().selectFirst();

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Name"), nameField);
        form.addRow(1, new Label("Phone"), phoneField);
        form.addRow(2, new Label("Blood Group"), bloodGroupBox);
        form.addRow(3, new Label("Location"), locationField);
        form.addRow(4, new Label("Donor Type"), donorTypeBox);
        form.addRow(5, new Label("Donation Units"), donationSpinner);

        Button addButton = new Button("Add Donor");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(event -> {
            try {
                String name = requiredValue(nameField, "Name");
                String phone = requiredValue(phoneField, "Phone");
                String location = requiredValue(locationField, "Location");
                String bloodGroup = bloodGroupBox.getValue();
                Donor donor = donorTypeBox.getValue().equals("Paid")
                        ? new PaidDonor(name, phone, bloodGroup, location)
                        : new UnpaidDonor(name, phone, bloodGroup, location);
                bloodBank.addDonor(donor);
                bloodBank.donateBlood(donor, donationSpinner.getValue());
                refreshDonors("");
                statusLabel.setText("Donor added and blood stock updated");
                nameField.clear();
                phoneField.clear();
                locationField.clear();
                    donationSpinner.getValueFactory().setValue(1);
            } catch (IllegalArgumentException exception) {
                showError(exception.getMessage());
            }
        });

        VBox formBox = new VBox(12, new Label("Register Donor"), form, addButton);
        formBox.getStyleClass().add("form-box");

        TextField searchField = new TextField();
        searchField.setPromptText("Example: A+");
        Button searchButton = new Button("Search");
        Button showAllButton = new Button("Show All");
        searchButton.setOnAction(event -> {
            try {
                refreshDonors(searchField.getText().trim());
            } catch (IllegalArgumentException exception) {
                showError(exception.getMessage());
            }
        });
        showAllButton.setOnAction(event -> {
            searchField.clear();
            refreshDonors("");
        });

        HBox searchBar = new HBox(10, new Label("Search blood group:"), searchField,
                searchButton, showAllButton);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        TableView<DonorRow> donorTable = createDonorTable();
        donorTable.setItems(donorRows);
        donorTable.setMinHeight(360);

        VBox content = new VBox(16, formBox, searchBar, donorTable);
        content.setPadding(new Insets(18));
        VBox.setVgrow(donorTable, javafx.scene.layout.Priority.ALWAYS);
        return content;
    }

    private TableView<DonorRow> createDonorTable() {
        TableView<DonorRow> table = new TableView<>();
        table.setPlaceholder(new Label("No donors registered yet."));
        table.getColumns().addAll(
                textColumn("Name", DonorRow::name),
                textColumn("Phone", DonorRow::phone),
                textColumn("Blood Group", DonorRow::bloodGroup),
                textColumn("Location", DonorRow::location),
                textColumn("Type", DonorRow::role));
        return table;
    }

    private TableColumn<DonorRow, String> textColumn(
            String title, java.util.function.Function<DonorRow, String> value) {
        TableColumn<DonorRow, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new SimpleStringProperty(value.apply(cell.getValue())));
        column.setPrefWidth(220);
        return column;
    }

    private VBox createStockPanel() {
        GridPane stockGrid = new GridPane();
        stockGrid.setHgap(12);
        stockGrid.setVgap(12);
        for (int index = 0; index < BLOOD_GROUPS.length; index++) {
            VBox card = new VBox(8);
            card.setAlignment(Pos.CENTER);
            card.getStyleClass().add("stock-card");
            Label groupLabel = new Label(BLOOD_GROUPS[index]);
            groupLabel.getStyleClass().add("stock-group");
            stockLabels[index] = new Label("0 units");
            card.getChildren().addAll(groupLabel, stockLabels[index]);
            stockGrid.add(card, index % 4, index / 4);
        }

        ComboBox<String> bloodGroupBox = new ComboBox<>(FXCollections.observableArrayList(BLOOD_GROUPS));
        bloodGroupBox.getSelectionModel().selectFirst();
        Spinner<Integer> unitsSpinner = new Spinner<>();
        unitsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
        Button addStockButton = new Button("Add Stock");

        addStockButton.setOnAction(event -> {
            String group = bloodGroupBox.getValue();
            int units = unitsSpinner.getValue();
            bloodBank.addBlood(group, units);
            refreshStock();
            statusLabel.setText(units + " unit(s) added to " + group);
        });
        HBox actionBar = new HBox(10, new Label("Blood Group:"), bloodGroupBox,
                new Label("Units to add:"), unitsSpinner, addStockButton);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        TextField patientNameField = new TextField();
        patientNameField.setPromptText("Patient name");
        TextField patientPhoneField = new TextField();
        patientPhoneField.setPromptText("Patient phone");
        TextField hospitalField = new TextField();
        hospitalField.setPromptText("Hospital name");
        Button requestButton = new Button("Request Blood");
        requestButton.getStyleClass().add("primary-button");
        requestButton.setOnAction(event -> {
            String group = bloodGroupBox.getValue();
            int units = unitsSpinner.getValue();
            try {
                Patient patient = new Patient(
                        requiredValue(patientNameField, "Patient name"),
                        requiredValue(patientPhoneField, "Patient phone"),
                        group,
                        requiredValue(hospitalField, "Hospital name"));
                BloodRequest request = bloodBank.requestBlood(patient, group, units);
                if (request.getStatus().equals("Accepted")) {
                    statusLabel.setText("Request accepted for " + patient.getName());
                    showInfo("Blood request accepted for " + patient.getName()
                            + "\n" + units + " unit(s) of " + group + " issued.");
                } else {
                    showError("Blood not available for " + group + ". Request rejected.");
                }
                refreshStock();
                patientNameField.clear();
                patientPhoneField.clear();
                hospitalField.clear();
            } catch (IllegalArgumentException exception) {
                showError(exception.getMessage());
            }
        });

        HBox requestBar = new HBox(10, new Label("Patient request:"), patientNameField,
                patientPhoneField, hospitalField, requestButton);
        requestBar.setAlignment(Pos.CENTER_LEFT);
        VBox content = new VBox(20, stockGrid, actionBar, requestBar);
        content.setPadding(new Insets(24));
        return content;
    }

    private VBox createHospitalPanel() {
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField locationField = new TextField();
        TextField contactField = new TextField();

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Hospital Name"), nameField);
        form.addRow(1, new Label("Phone"), phoneField);
        form.addRow(2, new Label("Location"), locationField);
        form.addRow(3, new Label("Contact Person"), contactField);

        Button addButton = new Button("Register Hospital");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(event -> {
            try {
                Hospital hospital = new Hospital(
                        requiredValue(nameField, "Hospital name"),
                        requiredValue(phoneField, "Phone"),
                        requiredValue(locationField, "Location"),
                        requiredValue(contactField, "Contact person"));
                bloodBank.addHospital(hospital);
                refreshHospitals("");
                statusLabel.setText("Hospital registered successfully");
                nameField.clear();
                phoneField.clear();
                locationField.clear();
                contactField.clear();
            } catch (IllegalArgumentException exception) {
                showError(exception.getMessage());
            }
        });

        VBox formBox = new VBox(12, new Label("Register Hospital"), form, addButton);
        formBox.getStyleClass().add("form-box");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by hospital or location");
        Button searchButton = new Button("Search");
        Button showAllButton = new Button("Show All");
        searchButton.setOnAction(event -> refreshHospitals(searchField.getText().trim()));
        showAllButton.setOnAction(event -> {
            searchField.clear();
            refreshHospitals("");
        });

        HBox searchBar = new HBox(10, new Label("Find hospital:"), searchField,
                searchButton, showAllButton);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        TableView<HospitalRow> hospitalTable = createHospitalTable();
        hospitalTable.setItems(hospitalRows);
        VBox content = new VBox(16, formBox, searchBar, hospitalTable);
        content.setPadding(new Insets(18));
        VBox.setVgrow(hospitalTable, javafx.scene.layout.Priority.ALWAYS);
        return content;
    }

    private TableView<HospitalRow> createHospitalTable() {
        TableView<HospitalRow> table = new TableView<>();
        table.setPlaceholder(new Label("No hospitals registered yet."));
        table.setMinHeight(360);
        table.getColumns().addAll(
                hospitalColumn("Hospital", HospitalRow::name),
                hospitalColumn("Phone", HospitalRow::phone),
                hospitalColumn("Location", HospitalRow::location),
                hospitalColumn("Contact Person", HospitalRow::contactPerson));
        return table;
    }

    private TableColumn<HospitalRow, String> hospitalColumn(
            String title, java.util.function.Function<HospitalRow, String> value) {
        TableColumn<HospitalRow, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new SimpleStringProperty(value.apply(cell.getValue())));
        column.setPrefWidth(250);
        return column;
    }

    private VBox createPatientPanel() {
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        ComboBox<String> bloodGroupBox = new ComboBox<>(
                FXCollections.observableArrayList(BLOOD_GROUPS));
        TextField hospitalField = new TextField();
        Spinner<Integer> unitsSpinner = new Spinner<>();
        unitsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        bloodGroupBox.getSelectionModel().selectFirst();

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Patient Name"), nameField);
        form.addRow(1, new Label("Phone"), phoneField);
        form.addRow(2, new Label("Blood Group"), bloodGroupBox);
        form.addRow(3, new Label("Hospital"), hospitalField);
        form.addRow(4, new Label("Required Units"), unitsSpinner);

        Button requestButton = new Button("Request Blood");
        requestButton.getStyleClass().add("primary-button");
        requestButton.setOnAction(event -> {
            try {
                String bloodGroup = bloodGroupBox.getValue();
                Patient patient = new Patient(
                        requiredValue(nameField, "Patient name"),
                        requiredValue(phoneField, "Patient phone"),
                        bloodGroup,
                        requiredValue(hospitalField, "Hospital"));
                BloodRequest request = bloodBank.requestBlood(
                        patient, bloodGroup, unitsSpinner.getValue());
                requestRows.add(0, new RequestRow(request));
                refreshStock();
                if (request.getStatus().equals("Accepted")) {
                    statusLabel.setText("Blood request accepted for " + patient.getName());
                    showInfo("Request accepted. Blood is available for the patient.");
                } else {
                    statusLabel.setText("Blood not available for " + bloodGroup);
                    showError("Blood not available. Request rejected.");
                }
                nameField.clear();
                phoneField.clear();
                hospitalField.clear();
            } catch (IllegalArgumentException exception) {
                showError(exception.getMessage());
            }
        });

        VBox formBox = new VBox(12, new Label("Patient Blood Request"), form, requestButton);
        formBox.getStyleClass().add("form-box");

        TableView<RequestRow> requestTable = new TableView<>(requestRows);
        requestTable.setPlaceholder(new Label("No patient requests yet."));
        requestTable.setMinHeight(300);
        requestTable.getColumns().addAll(
                requestColumn("Patient", RequestRow::patient),
                requestColumn("Hospital", RequestRow::hospital),
                requestColumn("Blood Group", RequestRow::bloodGroup),
                requestColumn("Units", RequestRow::units),
                requestColumn("Status", RequestRow::status));

        VBox content = new VBox(16, formBox, new Label("Request History"), requestTable);
        content.setPadding(new Insets(18));
        VBox.setVgrow(requestTable, javafx.scene.layout.Priority.ALWAYS);
        return content;
    }

    private TableColumn<RequestRow, String> requestColumn(
            String title, java.util.function.Function<RequestRow, String> value) {
        TableColumn<RequestRow, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new SimpleStringProperty(value.apply(cell.getValue())));
        column.setPrefWidth(220);
        return column;
    }

        private VBox createFamilyPanel() {
        familyRows.addAll(
                new FamilyRow(new Grandfather("Abdul Karim", 72)),
                new FamilyRow(new Father("Mohammad Rahman", 45)),
            new FamilyRow(new Child("Sami Rahman", 19)));

            Label title = new Label("Family Records");
        title.getStyleClass().add("section-title");
        Label description = new Label(
                "Family relationship records: Grandfather, Father and Child.");
        description.getStyleClass().add("muted-text");

        TableView<FamilyRow> familyTable = new TableView<>(familyRows);
        familyTable.setPlaceholder(new Label("No family members yet."));
        familyTable.setMinHeight(360);
        familyTable.getColumns().addAll(
            familyColumn("Name", FamilyRow::name),
            familyColumn("Age", FamilyRow::age),
            familyColumn("Relationship", FamilyRow::relation));

        VBox content = new VBox(12, title, description, familyTable);
        content.setPadding(new Insets(24));
        VBox.setVgrow(familyTable, javafx.scene.layout.Priority.ALWAYS);
        return content;
        }

        private TableColumn<FamilyRow, String> familyColumn(
            String title, java.util.function.Function<FamilyRow, String> value) {
        TableColumn<FamilyRow, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new SimpleStringProperty(value.apply(cell.getValue())));
        column.setPrefWidth(260);
        return column;
        }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(statusLabel);
        statusBar.getStyleClass().add("status-bar");
        return statusBar;
    }

    private void refreshDonors(String bloodGroup) {
        List<Donor> donors = bloodGroup.isBlank()
                ? bloodBank.getDonors()
                : bloodBank.findDonorsByBloodGroup(bloodGroup);
        donorRows.clear();
        for (Donor donor : donors) {
            donorRows.add(new DonorRow(donor));
        }
    }

    private void refreshStock() {
        for (int index = 0; index < BLOOD_GROUPS.length; index++) {
            stockLabels[index].setText(bloodBank.getBloodStock(BLOOD_GROUPS[index]) + " units");
        }
    }

    private void refreshHospitals(String searchText) {
        List<Hospital> hospitals = searchText.isBlank()
                ? bloodBank.getHospitals()
                : bloodBank.findHospitals(searchText);
        hospitalRows.clear();
        for (Hospital hospital : hospitals) {
            hospitalRows.add(new HospitalRow(hospital));
        }
    }

    private String requiredValue(TextField field, String label) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(label + " cannot be empty.");
        }
        return value;
    }

    private void showError(String message) {
        statusLabel.setText(message);
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText("Error");
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText("Request Accepted");
        alert.showAndWait();
    }

    private record DonorRow(String name, String phone, String bloodGroup,
                            String location, String role) {
        private DonorRow(Donor donor) {
            this(donor.getName(), donor.getPhone(), donor.getBloodGroup(),
                    donor.getLocation(), donor.getRole());
        }
    }

    private record HospitalRow(String name, String phone, String location, String contactPerson) {
        private HospitalRow(Hospital hospital) {
            this(hospital.getName(), hospital.getPhone(), hospital.getLocation(),
                    hospital.getContactPerson());
        }
    }

    private record FamilyRow(String name, String age, String relation) {
        private FamilyRow(FamilyMember member) {
            this(member.getName(), String.valueOf(member.getAge()), member.getRelation());
        }
    }

    private record RequestRow(String patient, String hospital, String bloodGroup,
                              String units, String status) {
        private RequestRow(BloodRequest request) {
            this(request.getPatient().getName(), request.getPatient().getHospital(),
                    request.getBloodGroup(), String.valueOf(request.getUnits()), request.getStatus());
        }
    }
}
