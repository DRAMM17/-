package controller;

import functions.ClientMethods;
import functions.HelperMethods;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Car;
import model.Client;
import model.Driver;
import model.Order;

import java.io.IOException;
import java.sql.*;

// Controller. Третьим звеном данной цепи является контроллер. В нем хранится код, который отвечает за обработку
// действий пользователя (любое действие пользователя в системе обрабатывается в контроллере).
// Основное предназначение Контроллера — обрабатывать действия пользователя.
// Именно через Контроллер пользователь вносит изменения в модель. Точнее в данные, которые хранятся в модели.
// Передаёт данные туда и сюда, забирает данные из поля, проверяет корректность данных

// Класс который содержит методы
// Должен обращаться ко вью компонентам, переменные

/**
 * Класс-контроллер, отвечающий за обработку действий таблиц
 */
public class MainController {

    /** Функция инициализации таблиц с данными */
    @FXML
    private void initialize() {
        initializeDrivers();
        initializeCars();
        initializeOrders();
    }

//__________________________________________________ DRIVERS _________________________________________________________//

    // @FXML нужен для того, чтобы fxml-файл имел доступ к приватным полям и методам
    // Автоматическое сопоставление компонентов fx:id с полями в контроллере с тем же именем
    // (Поля должны иметь модификатор public или аннотацию @FXML

    // Связывание компонентов Label с контроллером
    @FXML private Label lbSurname, lbFirstName, lbPatronymic, lbNickname, lbDriversLicenseNumber, lbDateOfIssue, lbDriversIdDriver, lbDriversIdCar, lbStateDriver, lbCar, lbHoursWorked, lbVoucherCost, lbOnLine;

    // Связывание компонентов Button с контроллером
    @FXML private Button btnAddDriver, btnChangeDriver, btnDeleteDriver, btnConnectDrivers, btnShowAllDrivers, btnShowOnLine;

    // Связывание компонента TableView c контроллером
    /**
     * Таблица "Водители" содержащая объекты класса Driver
     */
    @FXML private TableView<Driver> driversTable;

    // Связывание компонентов TableColumn с контроллером
    // При добавлении таблицы, она создаётся не типизированная какими-либо данными
    // Необходимо указать, что она будет хранить объекты класса Driver
    @FXML private TableColumn<Driver, String> patronymicColumn;
    @FXML private TableColumn<Driver, String> firstNameColumn;
    @FXML private TableColumn<Driver, String> surnameColumn;
    @FXML private TableColumn<Driver, String> nicknameColumn;

    // ObservableList - список со слушателем, обновляется, когда происходит изменение
    /** Связанный список, содержащий объекты класса Driver таблицы "Водители" */
    private static ObservableList<Driver> driversList = FXCollections.observableArrayList();

    /**
     * Вызов методов для таблицы "Водители" при подключении к базе данных
     */
    // Вызываемые методы при подключение к базе данных
    public void onConnectionDrivers(ActionEvent actionEvent) {
        HelperMethods.connection();
        dataDriversDisplay();

        // Разблокирование кнопок после подключения
        if(HelperMethods.isConnect == true) {
            btnConnectDrivers.setDisable(true);
            btnAddDriver.setDisable(false);
            btnChangeDriver.setDisable(false);
            btnDeleteDriver.setDisable(false);
            btnShowAllDrivers.setDisable(false);
            btnShowOnLine.setDisable(false);
        }
    }

    // При загрузке контроллера необходимо инициализировать таблицу и свзяать её со списком водителей
    /**
     * Инициализация таблицы и связывание её со списком водителей. А также блокирование кнопок до подключения к БД
     */
    private void initializeDrivers() {
        // Устанавливаем тип и значение, которое должно храниться в колонке
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<Driver, String>("FirstName"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<Driver, String>("Surname"));
        patronymicColumn.setCellValueFactory(new PropertyValueFactory<Driver, String>("Patronymic"));
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<Driver, String>("Nickname"));

        driversTable.setItems(driversList); // Отображение данных

        // Блокирование кнопок до подключения к БД
        btnAddDriver.setDisable(true);
        btnChangeDriver.setDisable(true);
        btnDeleteDriver.setDisable(true);
        btnShowAllDrivers.setDisable(true);
        btnShowOnLine.setDisable(true);
    }

     /**
     * Отображение данных таблицы "Водители"
     */
    public void dataDriversDisplay() {
        driversList.clear(); // Очистка списка
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlQuery = "SELECT * FROM TAXI.DRIVERS";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            // Получение значений записи по названиям полей таблицы
            while (resultSet.next()) {
                int idDriver = resultSet.getInt("IdDriver");
                int idCar = resultSet.getInt("IdCar");
                String firstName = resultSet.getString("FirstName");
                String surname = resultSet.getString("Surname");
                String patronymic = resultSet.getString("Patronymic");
                String nickname = resultSet.getString("Nickname");
                String driversLicenseNumber = resultSet.getString("DriversLicenseNumber");
                String hoursWorked = resultSet.getString("HoursWorked");
                String stateDriver = resultSet.getString("State");
                Date dateOfIssue = resultSet.getDate("DateOfIssue");
                String onLine = resultSet.getString("OnLine");
                // Добавление в список, объекта Водитель со всеми полученными данными
                driversList.add(new Driver(idDriver, idCar, firstName, surname, patronymic, nickname, driversLicenseNumber, hoursWorked, stateDriver, dateOfIssue, onLine)); // Занесение записи в таблицу DRIVERS
            }
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(throwables.toString());
            alert.show();
        }

        // Отображение значений полей объекта Driver в Label
        TableView.TableViewSelectionModel<Driver> selectionModel = driversTable.getSelectionModel();
        // Добавление слушателя для строк таблицы, чтобы можно было автоматически получать выбранную строку
        selectionModel.selectedItemProperty().addListener(new ChangeListener<Driver>() {
            @Override
            public void changed(ObservableValue<? extends Driver> observableValue, Driver previousValue, Driver currentValue) {
                if (currentValue != null) {
                    lbDriversIdDriver.setText("ID водителя: " + currentValue.getIdDriver());
                    lbDriversIdCar.setText("ID автомобиля: " + currentValue.getIdCar());
                    lbFirstName.setText(currentValue.getFirstName());
                    lbSurname.setText(currentValue.getSurname());
                    lbPatronymic.setText(currentValue.getPatronymic());
                    lbNickname.setText("\"" + currentValue.getNickname() + "\"");
                    lbDriversLicenseNumber.setText("Номер: " + currentValue.getDriversLicenseNumber());
                    lbHoursWorked.setText("Количество отработанных часов: " + currentValue.getHoursWorked());
                    lbStateDriver.setText("Состояние: " + currentValue.getStateDriver());
                    lbDateOfIssue.setText("Дата выдачи: " + HelperMethods.formatDate(currentValue.getDateOfIssue().toString(), "Default"));
                    lbOnLine.setText("На линии: " + currentValue.getOnLine());

                    // lbCar
                    // Отправление запроса в таблицу CARS для получение марки автомобиля
                    try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
                        String mark = "";
                        String sqlCommand = "SELECT MARK FROM TAXI.CARS WHERE IDDRIVER = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
                        preparedStatement.setInt(1, currentValue.getIdDriver());
                        ResultSet resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()) {
                            mark = resultSet.getString("Mark");
                        }
                        if (mark.equals("")) lbCar.setText("Нет автомобиля");
                        else lbCar.setText("Автомобиль: " + mark);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    lbVoucherCost.setText("Стоимость путёвки: " + Integer.parseInt(currentValue.getHoursWorked()) * 50);
                }
            }
        });
    }

    /**
     * Отображение нового окна с добавлением/изменением водителя
     * @param driver - водитель
     */
    public void showDriverForm(Driver driver) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/view/driverAddForm.fxml"));
        Parent page = loader.load();
        Stage window = new Stage();
        if (NewDriverController.typeQuery == "Insert") window.setTitle("Добавление водителя"); // Изменение заголовка в зависимости от типа действия
        else window.setTitle("Изменение водителя");
        window.initModality(Modality.APPLICATION_MODAL);
        window.initOwner(Main.getPrimaryStage());
        Scene scene = new Scene(page);
        window.setScene(scene);
        NewDriverController controller = loader.getController();
        controller.setAddStage(window);
        controller.setDriver(driver);
        window.showAndWait(); // Код будет выполняться после закрытия окна
    }

    // Добавление объекта Driver
    /**
     * Событие, которое срабатывает при нажатии на кнопку "Добавить" на странице "Водители"
     */
    public void onAddDriver(ActionEvent actionEvent) throws IOException {
        NewDriverController.typeQuery = "Insert"; // Указание типа операции "Вставка"
        Driver driver = new Driver();
        showDriverForm(driver); // Новое окно для добавления/изменения водителя
        // Предотвращение добавления пустой записи в таблицу (если мы решили отменить добавление)
        if (driver.getIdDriver() != 0) {
            driversList.add(driver);
        }
    }

    // Изменение объекта Driver
    /**
     * Событие, которое вызывается при нажатии на кнопку "Изменить" на странице "Водители"
     */
    public void onChangeDriver(ActionEvent actionEvent) throws IOException {
        // Получаем выбранный объект Водитель по записи в таблице
        Driver selectedDriver = driversTable.getSelectionModel().getSelectedItem();

        // Если его не выбрали ничего не происходит
        if (selectedDriver != null) {
            NewDriverController.typeQuery = "Update"; // Указание типа операции "Изменение"
            showDriverForm(selectedDriver); // Новое окно для добавления/изменения водителя
            dataDriversDisplay();
        }
    }

    // Удаление объекта Driver
    // Удалить запись Водителя, изменить запись Автомобиль, если у этого Водителя была машина
    /**
     * Событие, которое вызывается при нажатии на кнопку "Удалить" на странице "Водители"
     */
    public void onDeleteDriver(ActionEvent actionEvent) {
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            // Удаление записи Водитель по его ID в таблице Водители
            String sqlQuery = "DELETE FROM TAXI.DRIVERS WHERE IDDRIVER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            int currentId = driversTable.getSelectionModel().getSelectedItem().getIdDriver();
            preparedStatement.setInt(1, currentId);
            preparedStatement.executeUpdate();

            // Изменение записи Автомобиля по ID, который раньше принадлежал удалённому Водителю
            sqlQuery = "UPDATE TAXI.CARS SET IDDRIVER = 0 WHERE IDCAR = ?";
            // ID автомобиля удаляемого Водителя
            int idCarCurrentDriver = driversTable.getSelectionModel().getSelectedItem().getIdCar();
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, idCarCurrentDriver);
            preparedStatement.executeUpdate();
            dataDriversDisplay();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Запись удалена");
            alert.show();
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Запись не удалена" + "\r" + throwables.toString());
            alert.show();
        }
    }

    /**
     * Событие, которое вызывается при нажатии на кнопку "Показать всех водителей" на странице "Водители"
     */
    public void onShowAllDrivers(ActionEvent actionEvent) {
        dataDriversDisplay();
    }

    /**
     * Событие, которое вызывается при нажатии на кнопку "Показать всех водителей на линии" на странице "Водители"
     */
    public void onShowOnLine(ActionEvent actionEvent) {
        driversList.clear(); // Очистка списка
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlQuery = "SELECT * FROM TAXI.DRIVERS WHERE ONLINE = \"Да\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            // Получение значений записи по названиям полей таблицы
            while (resultSet.next()) {
                int idDriver = resultSet.getInt("IdDriver");
                int idCar = resultSet.getInt("IdCar");
                String firstName = resultSet.getString("FirstName");
                String surname = resultSet.getString("Surname");
                String patronymic = resultSet.getString("Patronymic");
                String nickname = resultSet.getString("Nickname");
                String driversLicenseNumber = resultSet.getString("DriversLicenseNumber");
                String hoursWorked = resultSet.getString("HoursWorked");
                String stateDriver = resultSet.getString("State");
                Date dateOfIssue = resultSet.getDate("DateOfIssue");
                String onLine = resultSet.getString("OnLine");
                // Добавление в список, объекта Водитель со всеми полученными данными
                driversList.add(new Driver(idDriver, idCar, firstName, surname, patronymic, nickname, driversLicenseNumber, hoursWorked, stateDriver, dateOfIssue, onLine)); // Занесение записи в таблицу DRIVERS
            }
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(throwables.toString());
            alert.show();
        }
    }

//___________________________________________________ CARS ___________________________________________________________//

    // Связывание компонентов Label с контроллером
    @FXML private Label lbIdAuto, lbIdDriver, lbColor, lbMark, lbGovernmentNumber, lbYearOfIssue, lbState, lbVehicleRegCertificateNumber;

    // Связывание компонента TableView c контроллером
    /** Таблица "Автомобили" содержащая объекты класса Car */
    @FXML private TableView<Car> carsTable;

    // Связывание компонентов Button с контроллером
    @FXML private Button btnAddCar, btnChangeCar, btnDeleteCar, btnConnectCars, btnShowAllCars, btnShowReserveCars, btnShowCarsUnderRepair;

    // Связывание компонентов TableColumn с контроллером
    // При добавлении таблицы, она создаётся не типизированная какими-либо данными
    // Необходимо указать, что она будет хранить объекты класса Car
    @FXML private TableColumn<Car, String> markColumn;
    @FXML private TableColumn<Car, String> colorColumn;
    @FXML private TableColumn<Car, String> governmentNumberColumn;

    // Связывание компонента TableView c контроллером
    /** Таблица "Автомобили" содержащая объекты класса Car */
    private static ObservableList<Car> carsList = FXCollections.observableArrayList();

    // При загрузке контроллера необходимо инициализировать таблицу и свзяать её со списком автомобилей
    /**
     * Инициализация таблицы и связывание её со списком автомобилей. А также блокирование кнопок до подключения к БД
     */
    private void initializeCars() {
        // Устанавливаем тип и значение, которое должно храниться в колонке
        markColumn.setCellValueFactory(new PropertyValueFactory<Car, String>("Mark"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<Car, String>("Color"));
        governmentNumberColumn.setCellValueFactory(new PropertyValueFactory<Car, String>("GovernmentNumber"));
        carsTable.setItems(carsList); // Отображение данных

        btnAddCar.setDisable(true);
        btnChangeCar.setDisable(true);
        btnDeleteCar.setDisable(true);
        btnShowAllCars.setDisable(true);
        btnShowReserveCars.setDisable(true);
        btnShowCarsUnderRepair.setDisable(true);
    }

    // Подключение к БД и вывод на TableView объектов Car
    /**
     * Вызов методов для таблицы "Автомобили" при подключении к базе данных
     */
    public void onConnectionCars(ActionEvent actionEvent) {
        HelperMethods.connection();
        dataCarsDisplay();

        if(HelperMethods.isConnect == true) {
            btnConnectCars.setDisable(true);
            btnAddCar.setDisable(false);
            btnChangeCar.setDisable(false);
            btnDeleteCar.setDisable(false);
            btnShowAllCars.setDisable(false);
            btnShowReserveCars.setDisable(false);
            btnShowCarsUnderRepair.setDisable(false);
        }
    }

    /**
     * Отображение данных таблицы "Автомобили"
     */
    public void dataCarsDisplay() {
        carsList.clear(); // Очистка списка
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT * FROM TAXI.CARS";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);

            // Получение значений записи по названиям полей таблицы
            while (resultSet.next()) {
                int idCar = resultSet.getInt("IdCar");
                int idDriver = resultSet.getInt("IdDriver");
                String mark = resultSet.getString("Mark");
                String color = resultSet.getString("Color");
                String stateCar = resultSet.getString("StateCar");
                String governmentNumber = resultSet.getString("GovernmentNumber");
                String yearOfIssue = resultSet.getString("YearOfIssue");
                String vehicleRegCertificateNumber = resultSet.getString("VehicleRegCertificateNumber");
                // Добавление в список объекта Автомобиль со всеми полученными данными
                carsList.add(new Car(idCar, idDriver, mark, color, stateCar, governmentNumber, yearOfIssue, vehicleRegCertificateNumber)); // Создание объекта Car и добавление его в список
            }
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(throwables.toString());
        }

        // Отображение значений полей объекта Car в Label
        TableView.TableViewSelectionModel<Car> selectionModel = carsTable.getSelectionModel();
        // Добавление слушателя для строк таблицы, чтобы можно было автоматически получать выбранную строку
        selectionModel.selectedItemProperty().addListener(new ChangeListener<Car>() {
            @Override
            public void changed(ObservableValue<? extends Car> observableValue, Car previousValue, Car currentValue) {
                if (currentValue != null) {
                    lbIdAuto.setText("ID водителя: " + currentValue.getIdDriver());
                    lbIdDriver.setText("ID автомобиля: " + currentValue.getIdCar());
                    lbColor.setText(currentValue.getColor());
                    lbMark.setText(currentValue.getMark());
                    lbGovernmentNumber.setText("Гос. номер: " + currentValue.getGovernmentNumber());
                    lbYearOfIssue.setText("Год выпуска: " + currentValue.getYearOfIssue());
                    lbState.setText("Состояние: " + currentValue.getStateCar());
                    lbVehicleRegCertificateNumber.setText("Номер свидетельства о рег-ии ТС: " + currentValue.getVehicleRegCertificateNumber());
                }
            }
        });
    }

    /**
     * Отображение нового окна с добавлением/изменением автомобиля
     * @param car - водитель
     */
    public void showCarForm(Car car) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/view/carAddForm.fxml"));
        Parent page = loader.load();
        Stage window = new Stage();
        window.setMinWidth(230);
        window.setMinHeight(530);
        if (NewCarController.typeQuery == "Insert") window.setTitle("Добавление автомобиля"); // Изменение заголовка в зависимости от типа действия
        else window.setTitle("Изменение автомобиля");
        window.initModality(Modality.APPLICATION_MODAL);
        window.initOwner(Main.getPrimaryStage());
        Scene scene = new Scene(page);
        window.setScene(scene);
        NewCarController controller = loader.getController();
        controller.setAddStage(window);
        controller.setCar(car);
        window.showAndWait(); // Код будет выполняться после закрытия окна
    }

    // Добавление объекта Car
    /**
     * Событие, которое срабатывает при нажатии на кнопку "Добавить" на странице "Автомобили"
     */
    public void onAddCar(ActionEvent actionEvent) throws IOException {
        NewCarController.typeQuery = "Insert";
        Car car = new Car();
        showCarForm(car); // Новое окно для добавления/изменения водителя
        // Предотвращение добавления пустой записи в таблицу (если решили отменить добавление)
        if (car.getIdCar() != 0)
            carsList.add(car);
    }

    // Изменение объекта Car
    /**
     * Событие, которое вызывается при нажатии на кнопку "Изменить" на странице "Автомобили"
     */
    public void onChangeCar(ActionEvent actionEvent) throws IOException {
        // Получаем выбранный объект Автомобиль по записи в таблице
        Car selectedAuto = carsTable.getSelectionModel().getSelectedItem();

        // Если его не выбрали ничего не происходит
        if (selectedAuto != null) {
            NewCarController.typeQuery = "Update";
            showCarForm(selectedAuto);
            dataCarsDisplay();
        }
    }

    // Удаление объекта Car
    // Удалить запись Автомобиль, изменить запись Водитель, если у этого Автомобиля был водитель
    /**
     * Событие, которое вызывается при нажатии на кнопку "Удалить" на странице "Автомобили"
     */
    public void onDeleteCar(ActionEvent actionEvent) {
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            // Удаление записи Автомобиль по его ID в таблице Автомобили
            String sqlQuery = "DELETE FROM TAXI.CARS WHERE IDCAR = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, carsTable.getSelectionModel().getSelectedItem().getIdCar());
            preparedStatement.executeUpdate();

            // Изменение записи Автомобиля по ID, который раньше принадлежал удалённому Водителю
            sqlQuery = "UPDATE TAXI.DRIVERS SET IDCAR = 0 WHERE IDDRIVER = ?";
            // ID водителя удаляемого Автомобиля
            int idDriverCurrentCar = carsTable.getSelectionModel().getSelectedItem().getIdDriver();
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, idDriverCurrentCar);
            preparedStatement.executeUpdate();

            dataCarsDisplay();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Запись удалена");
            alert.show();
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Запись не удалена" + "\n" + throwables.getMessage());
            alert.show();
        }
    }

    /**
     * Событие, которое вызывается при нажатии на кнопку "Все автомобили" на странице "Автомобили"
     */
    public void onShowAllCars(ActionEvent actionEvent) {
        dataCarsDisplay();
    }

    /**
     * Событие, которое вызывается при нажатии на кнопку "Забронированные автомобили" на странице "Автомобили"
     */
    public void onShowReserveCars(ActionEvent actionEvent) {
        carsList.clear(); // Очистка списка
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT * FROM TAXI.CARS WHERE STATECAR = \"Забронирован\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);

            // Получение значений записи по названиям полей таблицы
            while (resultSet.next()) {
                int idCar = resultSet.getInt("IdCar");
                int idDriver = resultSet.getInt("IdDriver");
                String mark = resultSet.getString("Mark");
                String color = resultSet.getString("Color");
                String stateCar = resultSet.getString("StateCar");
                String governmentNumber = resultSet.getString("GovernmentNumber");
                String yearOfIssue = resultSet.getString("YearOfIssue");
                String vehicleRegCertificateNumber = resultSet.getString("VehicleRegCertificateNumber");
                // Добавление в список объекта Автомобиль со всеми полученными данными
                carsList.add(new Car(idCar, idDriver, mark, color, stateCar, governmentNumber, yearOfIssue, vehicleRegCertificateNumber)); // Создание объекта Car и добавление его в список
            }
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(throwables.toString());
        }
    }

    /**
     * Событие, которое вызывается при нажатии на кнопку "Автомобили в ремонте" на странице "Автомобили"
     */
    public void onShowCarsUnderRepair(ActionEvent actionEvent) {
        carsList.clear(); // Очистка списка
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT * FROM TAXI.CARS WHERE STATECAR = \"В ремонте\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);

            // Получение значений записи по названиям полей таблицы
            while (resultSet.next()) {
                int idCar = resultSet.getInt("IdCar");
                int idDriver = resultSet.getInt("IdDriver");
                String mark = resultSet.getString("Mark");
                String color = resultSet.getString("Color");
                String stateCar = resultSet.getString("StateCar");
                String governmentNumber = resultSet.getString("GovernmentNumber");
                String yearOfIssue = resultSet.getString("YearOfIssue");
                String vehicleRegCertificateNumber = resultSet.getString("VehicleRegCertificateNumber");
                // Добавление в список объекта Автомобиль со всеми полученными данными
                carsList.add(new Car(idCar, idDriver, mark, color, stateCar, governmentNumber, yearOfIssue, vehicleRegCertificateNumber)); // Создание объекта Car и добавление его в список
            }
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(throwables.toString());
        }
    }

//__________________________________________________ ORDERS __________________________________________________________//

    // Связывание компонентов Label с контроллером
    @FXML Label lbIdOrder, lbOrdersIdDriver, lbOrdersIdClient, lbStartTime, lbStartDate, lbStartAddress, lbEndAddress;

    // Связывание компонентов Button с контроллером
    @FXML Button btnConnectOrders, btnAddOrder, btnChangeOrder, btnDeleteOrder;

    // Связывание компонента TableView c контроллером
    /** Таблица "Автомобили" содержащая объекты класса Car */
    @FXML TableView<Order> ordersTable;

    // Связывание компонентов TableColumn с контроллером
    // При добавлении таблицы, она создаётся не типизированная какими-либо данными
    // Необходимо указать, что она будет хранить объекты класса Order
    @FXML private TableColumn<Order, Date> startDateColumn;
    @FXML private TableColumn<Order, Time> startTimeColumn;
    @FXML private TableColumn<Order, Integer> ordersIdDriverColumn;
    @FXML private TableColumn<Order, String> orderStatusColumn;

    // Связывание компонента TableView c контроллером
    /** Таблица "Заказы" содержащая объекты класса Order */
    ObservableList<Order> ordersList = FXCollections.observableArrayList();

    // При загрузке контроллера необходимо инициализировать таблицу и свзяать её со списком заказов
    /**
     * Инициализация таблицы и связывание её со списком заказов. А также блокирование кнопок до подключения к БД
     */
    private void initializeOrders() {
        startDateColumn.setCellValueFactory(new PropertyValueFactory<Order, Date>("StartDate"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<Order, Time>("StartTime"));
        ordersIdDriverColumn.setCellValueFactory(new PropertyValueFactory<Order, Integer>("IdDriver"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("OrderStatus"));

        btnAddOrder.setDisable(true);
        btnChangeOrder.setDisable(true);
        btnDeleteOrder.setDisable(true);
    }

    /**
     * Отображение данных таблицы "Заказы"
     */
    public void dataOrdersDisplay() {
        ordersList.clear(); // Очищение списка
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT * FROM TAXI.ORDERS";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);

            // Получение значений записи по названиям полей таблицы
            while (resultSet.next()) {
                ordersList.add(new Order(
                        resultSet.getInt("IdOrder"),
                        resultSet.getInt("IdDriver"),
                        resultSet.getInt("IdClient"),
                        resultSet.getString("StartTime"),
                        resultSet.getDate("StartDate"),
                        resultSet.getString("OrderStatus"),
                        resultSet.getString("StartAddress"),
                        resultSet.getString("EndAddress")));
            }
            ordersTable.setItems(ordersList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Отображение нового окна с добавлением/изменением заказа
     * @param order - заказ
     */
    public void showOrderForm(Order order, Client client) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/view/orderAddForm.fxml"));
        Parent page = loader.load();
        Stage window = new Stage();
        if (NewOrderController.typeQuery.equals("Insert")) window.setTitle("Добавление заказа"); // Изменение заголовка в зависимости от типа действия
        else window.setTitle("Изменение заказа");
        window.initModality(Modality.APPLICATION_MODAL);
        window.initOwner(Main.getPrimaryStage());
        Scene scene = new Scene(page);
        window.setScene(scene);
        NewOrderController controller = loader.getController();
        controller.setAddStage(window);
        controller.setOrder(order, client);
        window.showAndWait(); // Код будет выполняться после закрытия окна
    }

    // Подключение к БД и вывод на TableView объектов Order
    /**
     * Вызов методов для таблицы "Автомобили" при подключении к базе данных
     */
    public void onConnectionOrders(ActionEvent actionEvent) {
        HelperMethods.connection();
        dataOrdersDisplay();

        // Разблокирование кнопок после подключения
        if(HelperMethods.isConnect == true) {
            btnConnectOrders.setDisable(true);
            btnAddOrder.setDisable(false);
            btnChangeOrder.setDisable(false);
            btnDeleteOrder.setDisable(false);
        }

        // Отображение значений полей объекта Order в Label
        TableView.TableViewSelectionModel<Order> selectionModel = ordersTable.getSelectionModel();
        // Добавление слушателя для строк таблицы, чтобы можно было автоматически получать выбранную строку
        selectionModel.selectedItemProperty().addListener(new ChangeListener<Order>() {
            @Override
            public void changed(ObservableValue<? extends Order> observableValue, Order previousValue, Order currentValue) {
                if (currentValue != null) {
                    lbIdOrder.setText("ID заказа: " + currentValue.getIdOrder());
                    lbOrdersIdDriver.setText("ID водителя: " + currentValue.getIdDriver());
                    lbOrdersIdClient.setText("ID клиента: " + currentValue.getIdClient());
                    lbStartDate.setText(HelperMethods.formatDate(currentValue.getStartDate().toString(), "Default"));
                    lbStartTime.setText(currentValue.getStartTime().toString());
                    lbStartAddress.setText(currentValue.getStartAddress());
                    lbEndAddress.setText(currentValue.getEndAddress());
                }
            }
        });
    }

    // Добавление объекта Order
    /**
     * Событие, которое срабатывает при нажатии на кнопку "Добавить" на странице "Заказы"
     */
    public void onAddOrder(ActionEvent actionEvent) throws IOException {
        NewOrderController.typeQuery = "Insert";
        Order order = new Order();
        Client client = new Client();
        showOrderForm(order, client);
        if (order.getIdOrder() != 0)
            ordersList.add(order);
        dataOrdersDisplay();
    }

    // Изменение объекта Order
    /**
     * Событие, которое вызывается при нажатии на кнопку "Изменить" на странице "Заказы"
     */
    public void onChangeOrder(ActionEvent actionEvent) throws IOException {
        NewOrderController.typeQuery = "Update";
        // Получаем выбранный объект Заказ по записи в таблице
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        Client selectedClient = ClientMethods.getClient(selectedOrder.getIdClient());
        // Если его не выбрали ничего не происходит
        if (selectedOrder != null) {
            NewOrderController.typeQuery = "Update";
            showOrderForm(selectedOrder, selectedClient);
            dataOrdersDisplay();
        }
    }

    /**
     * Cобытие, которое срабатывает при нажатии на кнопку "Удалить" на странице "Заказы"
     */
    public void onDeleteOrder(ActionEvent actionEvent) {
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlQuery = "DELETE FROM TAXI.ORDERS WHERE IDORDER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, ordersTable.getSelectionModel().getSelectedItem().getIdOrder());
            preparedStatement.executeUpdate();
            dataOrdersDisplay();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Запись удалена");
            alert.show();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}