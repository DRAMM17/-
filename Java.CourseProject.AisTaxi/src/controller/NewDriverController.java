package controller;

import functions.CarMethods;
import functions.DriverMethods;
import functions.HelperMethods;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import model.Driver;

import java.sql.*;

/**
 * Класс-контроллер, отвечающий за обработку добавления и изменения объекта Водитель со свойствами <b>window<b/>, <b>driver</b>, <b>idFreeCar</b>, <b>typeQuery</b>
 */
public class NewDriverController {
    // Связывание компонентов TextField с контроллером
    @FXML TextField tfFirstName, tfSurname, tfPatronymic, tfNickname, tfHoursWorked ,tfDriversLicenseNumber;

    // Связывание компонентов ComboBox с контроллером
    @FXML ComboBox cbFreeCars, cbStates, cbOnLine;

    // Связывание компонента DatePicker с контроллером
    @FXML DatePicker dpDateOfIssue;

    /** Поле "Окно" */
    private Stage window;

    /** Поле "Водитель" */
    private Driver driver;

    /** Поле "ID свободной машины" */
    private int idFreeCar;

    /** Поле "Тип запроса" */
    public static String typeQuery;

    /** Функция инициализации */
    @FXML private void initialize() {
        // Форматирование строки, ввод только целочисленных значений
        tfHoursWorked.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

        // ComboBox с состояниями
        ObservableList<String> states = FXCollections.observableArrayList("Здоров", "На больничном", "В отпуске");
        cbStates.setItems(states);
        cbStates.setValue("Здоров"); // Элемент по умолчанию

        ObservableList<String> onLine = FXCollections.observableArrayList("Да", "Нет");
        cbOnLine.setItems(onLine);
        cbOnLine.setValue("Нет"); // Элемент по умолчанию

        // ComboBox со свободными машинами
        ObservableList<String> observableList = FXCollections.observableArrayList("Не выбрано");
        observableList.addAll(getFreeCars());
        cbFreeCars.setItems(observableList);
        cbFreeCars.setValue("Не выбрано");

        // Слушатель для ComboBox со всеми свободными машинами
        cbFreeCars.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object previousValue, Object currentValue) {
                int partEndIdCar = currentValue.toString().indexOf(')', 1);
                if (partEndIdCar == -1) return;
                else
                    idFreeCar = Integer.parseInt(String.valueOf(currentValue).substring(1, partEndIdCar)); // Пропускаем открывающую скобочку и считываем до закрывающей и получаем чистый ID
            }
        });
    }

    public void setAddStage(Stage window) {
        this.window = window;
        window.setResizable(false);
        // Блокирование ввода значений в поле "Количество отработанных часов"
        if (typeQuery.equals("Insert")) {
            tfHoursWorked.setDisable(true);
            tfHoursWorked.setText("0");
            cbOnLine.setDisable(true);
        }
    }

    /**
     * Функиция для передачи объекта Driver
     * @param driver - водитель
     */
    public void setDriver(Driver driver) {
        this.driver = driver;
        // Заполнение полей TextField данными объекта Водитель
        // При добавлении все поля пустые, при изменении поля заполняются данными объекта Driver
        if (driver.getIdDriver() != 0) { // При добавлении водителя все поля 0 и null, поэтому это условие срабатывает, когда мы изменяем запись
            tfFirstName.setText(driver.getFirstName());
            tfSurname.setText(driver.getSurname());
            tfPatronymic.setText(driver.getPatronymic());
            tfNickname.setText(driver.getNickname());
            tfDriversLicenseNumber.setText(driver.getDriversLicenseNumber());
            dpDateOfIssue.setValue(driver.getDateOfIssue().toLocalDate());
            cbOnLine.setValue(driver.getOnLine());
            tfHoursWorked.setText(driver.getHoursWorked());
            cbFreeCars.setValue(CarMethods.getCarInfoAtId(driver.getIdCar()));
            cbStates.setValue(driver.getStateDriver());
        }
    }

    /**
     * Событие, которое срабатывает при нажатии на кнопку "Подтвердить" на окне "Добавление/изменение водителя"
     */
    public void onConfirm(ActionEvent actionEvent) {
        if (typeQuery.equals("Insert")) {
            int add = addDriver();
            // После добавления записи, окно закрывается
            if (add != -1) { // 1 - запись добавлена, -1 - запись не добавлена
                updateCarAtIdDriver(); // Обновление записи в таблице "Автомобили" (добавление ID Водителя по указанному ID Автомобиля)
                window.close();
            }
        } else if (typeQuery.equals("Update")) {
            int update = updateDriver();
            // После изменения записи, окно закрывается
            if (update != -1) // 1 - запись изменена, -1 - запись не изменена
                window.close();
        }
    }

    /** Событие, которое срабатывает при нажатии на кнопку "Отменить" на окне "Добавление/изменение водителя" */
    public void onCancel(ActionEvent actionEvent) {
        window.close();
    }

    /**
     * Добавить водителя в базу данных
     * @return isAdd количество добавленных записей или -1, если их нет
     */
    private int addDriver() {
        int isAdd = -1;
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            // !!!!
            if (DriverMethods.checkFieldsDriver("Add", 0, tfFirstName, tfSurname, tfPatronymic, tfNickname, tfHoursWorked, tfDriversLicenseNumber, dpDateOfIssue, cbStates))
                return isAdd;

            // Добавление записи Водитель
            String sqlCommand = "INSERT TAXI.DRIVERS (IDCAR, FIRSTNAME, SURNAME, PATRONYMIC, NICKNAME, DRIVERSLICENSENUMBER, HOURSWORKED, STATE, DATEOFISSUE, ONLINE) VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);

            // Установка свойств объекту Driver
            driver.setIdCar(idFreeCar);
            driver.setFirstName(tfFirstName.getText());
            driver.setSurname(tfSurname.getText());
            driver.setPatronymic(tfPatronymic.getText());
            driver.setNickname(tfNickname.getText());
            driver.setDriversLicenseNumber(tfDriversLicenseNumber.getText());
            driver.setHoursWorked("0");
            driver.setStateDriver(cbStates.getValue().toString());
            driver.setDateOfIssue(Date.valueOf(dpDateOfIssue.getValue()));
            driver.setOnLine(cbOnLine.getValue().toString());

            // Установка значений для подготовленного запроса
            preparedStatement.setInt(1, driver.getIdCar());
            preparedStatement.setString(2, driver.getFirstName());
            preparedStatement.setString(3, driver.getSurname());
            preparedStatement.setString(4, driver.getPatronymic());
            preparedStatement.setString(5, driver.getNickname());
            preparedStatement.setString(6, driver.getDriversLicenseNumber());
            preparedStatement.setString(7, driver.getHoursWorked());
            preparedStatement.setString(8, driver.getStateDriver());
            preparedStatement.setDate(9, driver.getDateOfIssue());
            preparedStatement.setString(10, driver.getOnLine());

            // Выполнение запроса
            isAdd = preparedStatement.executeUpdate();

            if (isAdd != -1) {
                driver.setIdDriver(DriverMethods.getAddedIdDriver(driver)); // Присваиваем ID для нового водителя, для будущей проверки
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Запись добавлена");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Запись не добавлена");
                alert.show();
            }
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Запись не добавлена" + "\n" + "Ошибка: " + throwables.getMessage());
            alert.show();
            return isAdd;
        }
        return isAdd;
    }

    /**
     * Изменение Водителя в базе данных
     * @return isUpdate количество изменённых записей или -1, если их нет
     */
    private int updateDriver() {
        int isUpdate = -1;
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            // Проверка полей
            if (DriverMethods.checkFieldsDriver("Update", driver.getIdDriver(), tfFirstName, tfSurname, tfPatronymic, tfNickname, tfHoursWorked, tfDriversLicenseNumber, dpDateOfIssue, cbStates))
                return isUpdate;
            int idPreviousCar = driver.getIdCar(); // ID предыдущего автомобиля
            int idNewCar = HelperMethods.getIdFromCB(cbFreeCars.getValue().toString()); // ID нового автомобиля
            // Запрос на изменение Водителя
            String sqlQuery = "UPDATE TAXI.DRIVERS SET FIRSTNAME = ?, SURNAME = ?, PATRONYMIC = ?, NICKNAME = ?, DRIVERSLICENSENUMBER = ?, DATEOFISSUE = ?, HOURSWORKED = ?, STATE = ?, ONLINE = ?, IDCAR = ? WHERE IDDRIVER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, tfFirstName.getText());
            preparedStatement.setString(2, tfSurname.getText());
            preparedStatement.setString(3, tfPatronymic.getText());
            preparedStatement.setString(4, tfNickname.getText());
            preparedStatement.setString(5, tfDriversLicenseNumber.getText());
            preparedStatement.setString(6, dpDateOfIssue.getValue().toString());
            preparedStatement.setString(7, tfHoursWorked.getText());
            preparedStatement.setString(8, cbStates.getSelectionModel().getSelectedItem().toString());
            preparedStatement.setString(9, cbOnLine.getValue().toString());
            if (cbFreeCars.getValue().toString().equals("Не выбрано"))
                preparedStatement.setInt(10, 0);
            else preparedStatement.setInt(10, idNewCar);
            preparedStatement.setInt(11, driver.getIdDriver());
            isUpdate = preparedStatement.executeUpdate();

            // Изменение ID Водителя у нового Автомобиля в таблице "Автомобили"
            sqlQuery = "UPDATE TAXI.CARS SET IDDRIVER = ? WHERE IDCAR = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, driver.getIdDriver());
            preparedStatement.setInt(2, idNewCar);
            preparedStatement.executeUpdate();

            // Удаление ID Водителя у предыдущего Автомобиля в таблице "Автомобили"
            sqlQuery = "UPDATE TAXI.CARS SET IDDRIVER = 0 WHERE IDCAR = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, idPreviousCar);
            preparedStatement.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (isUpdate != -1) alert.setHeaderText("Запись изменена");
            else alert.setHeaderText("Запись не изменена");
            alert.show();
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Запись не изменена" + "\n" + "Ошибка: " + throwables.getMessage());
            alert.show();
            return isUpdate;
        }
        return isUpdate;
    }

    /**
     * Добавить в таблицу "Автомобили" ID Водителя для указанного автомобиля
     */
    private void updateCarAtIdDriver() {
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "UPDATE TAXI.CARS SET IDDRIVER = ? WHERE IDCAR = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setInt(1, driver.getIdDriver());
            preparedStatement.setInt(2, driver.getIdCar());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Запрос на получение всех незанятых водителями машин
     * @return возвращает список незанятых автомобилей
     */
    public ObservableList<String> getFreeCars() {
        ObservableList<String> freeCars = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT * FROM TAXI.CARS WHERE IDDRIVER IS NULL OR IDDRIVER = \"\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while (resultSet.next()) {
                int idCar = resultSet.getInt("IdCar");
                String mark = resultSet.getString("Mark");
                String color = resultSet.getString("Color");
                freeCars.add("(" + idCar + ")" + " " + color + " " + mark);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return freeCars;
    }
}