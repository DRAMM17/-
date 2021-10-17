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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import model.Car;

import java.sql.*;

public class NewCarController {
    // Связывание компонентов TextField с контроллером
    @FXML TextField tfMark, tfColor, tfGovernmentNumber, tfYearOfIssue, tfVehicleRegCertificateNumber;

    // Связывание компонентов ComboBox с контроллером
    @FXML ComboBox cbMark, cbColor, cbYearOfIssue, cbState, cbFreeDrivers;

    /** Поле "Окно" */
    private Stage window;

    /** Поле "Автомобиль" */
    private Car car;

    /** Поле "ID свободного водителя" */
    private int idFreeDriver;

    /** Поле "Тип запроса" */
    public static String typeQuery;

    /**
     * Функция для подключения слушателей компонентов ComboBox
     */
    public void connectListenersComboBox() {
        // Слушатель для ComboBox "Названия автомобиля"
        cbMark.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object previousValue, Object currentValue) {
                tfMark.setText(currentValue.toString());
            }
        });

        // Слушатель для ComboBox "Цвет"
        cbColor.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object previousValue, Object currentValue) {
                tfColor.setText(currentValue.toString());
            }
        });

        // Слушатель ComboBox "Год выпуска"
        cbYearOfIssue.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object previousValue, Object currentValue) {
                tfYearOfIssue.setText(currentValue.toString());
            }
        });

        // Слушатель ComboBox "Свободные водители"
        cbFreeDrivers.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object previousValue, Object currentValue) {
                int partEndIdDriver = currentValue.toString().indexOf(')', 1);
                if (partEndIdDriver == -1) return;
                else
                    idFreeDriver = Integer.parseInt(String.valueOf(currentValue).substring(1, partEndIdDriver)); // Пропускаем открывающую скобочку и считываем до закрывающей и получаем чистый ID
            }
        });
    }

    /** Функция инициализации */
    @FXML
    private void initialize() {
        tfYearOfIssue.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        connectListenersComboBox();
        tfGovernmentNumber.setPromptText("а000аа00");

        // ComboBox Статус
        ObservableList<String> states = FXCollections.observableArrayList("Свободен", "Занят", "В ремонте", "Забронирован");
        cbState.setItems(states);
        cbState.setValue("Не выбрано"); // Элемент по умолчанию

        // ComboBox Марка
        ObservableList<String> brands = FXCollections.observableArrayList("Audi", "BMW", "Cadillac", "Chery", "Chevrolet", "Citroen", "Daewoo", "Datsun", "Fiat", "Ford", "Honda", "Hyundai", "Infiniti", "Jaguar", "Jeep", "Kia", "Lexus", "Mazda", "Nissan", "Opel", "Peugeot", "Renault", "Seat", "Skoda", "Suzuki", "Toyota", "Volkswagen", "Volvo");
        cbMark.setItems(brands);

        // ComboBox Цвет
        ObservableList<String> colors = FXCollections.observableArrayList("Белый", "Чёрный", "Красный", "Оранжевый", "Жёлтый", "Красный", "Зелёный", "Голубой", "Синий", "Фиолетовый", "Розовый", "Коричневый", "Серый");
        cbColor.setItems(colors);

        // ComboBox Год выпуска
        // Предусмотреть обработку некорректных значений
        ObservableList<String> years = FXCollections.observableArrayList("1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021");
        cbYearOfIssue.setItems(years);

        // ComboBox "Свободные водители"
        ObservableList<String> observableList = FXCollections.observableArrayList("Не выбрано");
        observableList.addAll(DriverMethods.getFreeDrivers());
        cbFreeDrivers.setItems(observableList);
        cbFreeDrivers.setValue("Не выбрано");
    }

    public void setAddStage(Stage window) {
        window.setResizable(false);
        this.window = window;
    }

    /**
     * Функиция для передачи объекта Car
     * @param car - автомобиль
     */
    // Заполнение полей данными объекта Автомобиль
    public void setCar(Car car) {
        this.car = car;
        // Заполнение полей TextField данными объекта Автомобиль
        // При добавлении все поля пустые, при изменении поля заполняются данными объекта Car
        if (car.getIdCar() != 0) { // При добавлении водителя все поля 0 и null, поэтому это условие срабатывает, когда мы изменяем запись
            tfMark.setText(car.getMark());
            tfColor.setText(car.getColor());
            tfGovernmentNumber.setText(car.getGovernmentNumber());
            tfYearOfIssue.setText(car.getYearOfIssue());
            cbState.setValue(car.getStateCar());
            tfVehicleRegCertificateNumber.setText(car.getVehicleRegCertificateNumber());
            cbFreeDrivers.setValue(DriverMethods.getDriver(car.getIdDriver()));
        }
    }

    /**
     * Событие, которое срабатывает при нажатии на кнопку "Подтвердить" на окне "Добавление/изменение автомобиля"
     */
    public void onConfirm(ActionEvent actionEvent) {
        if (typeQuery.equals("Insert")) {
            int add = addCar();
            // После добавления записи, окно закрывается
            if (add != -1) { // 1 - запись добавлена, -1 - запись не добавлена
                updateDriverAtIdCar(); // Обновление записи в таблице "Водители" (добавление ID Автомобиля по указанному ID Водителя)
                window.close();
            }
        } else if (typeQuery.equals("Update")) {
            int update = updateCar();
            if (update != -1)
                window.close();
        }
    }

    /** Событие, которое срабатывает при нажатии на кнопку "Отменить" на окне "Добавление/изменение автомобиля" */
    public void onCancel(ActionEvent actionEvent) {
        window.close();
    }

    /**
     * Добавить автомобиль в базу данных
     * @return isAdd количество добавленных записей или -1, если их нет
     */
    private int addCar() {
        int isAdd = -1;
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            if (CarMethods.checkFieldsCar("Add", 0, tfMark, tfColor, tfGovernmentNumber, tfYearOfIssue, cbState, tfVehicleRegCertificateNumber))
                return isAdd;

            // Добавление автомобиля
            String sqlCommand = "INSERT TAXI.CARS (MARK, COLOR, GOVERNMENTNUMBER, YEAROFISSUE, STATECAR, VEHICLEREGCERTIFICATENUMBER, IDDRIVER) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);

            car.setMark(tfMark.getText());
            car.setColor(tfColor.getText());
            car.setGovernmentNumber(tfGovernmentNumber.getText());
            car.setYearOfIssue(tfYearOfIssue.getText());
            car.setStateCar(cbState.getValue().toString());
            car.setVehicleRegCertificateNumber(tfVehicleRegCertificateNumber.getText());
            car.setIdDriver(idFreeDriver);

            preparedStatement.setString(1, car.getMark());
            preparedStatement.setString(2, car.getColor());
            preparedStatement.setString(3, car.getGovernmentNumber());
            preparedStatement.setString(4, car.getYearOfIssue());
            preparedStatement.setString(5, car.getStateCar());
            preparedStatement.setString(6, car.getVehicleRegCertificateNumber());
            preparedStatement.setInt(7, car.getIdDriver());

            isAdd = preparedStatement.executeUpdate();

            if (isAdd != -1) {
                car.setIdCar(CarMethods.getAddedIdCar(car)); // Присваиваем ID для нового водителя, для будущей проверки
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Запись добавлена");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Запись не добавлена");
                alert.show();
            }
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Запись не добавлена" + "\n" + "Ошибка: " + throwables.getMessage());
            alert.show();
        }
        return isAdd;
    }

    /**
     * Изменение Автомобиля в базе данных
     * @return isUpdate количество изменённых записей или -1, если их нет
     */
    private int updateCar() {
        int isUpdate = -1;
        int idPreviousDriver = car.getIdDriver(); // ID предыдущего водителя
        int idNewDriver = HelperMethods.getIdFromCB(cbFreeDrivers.getValue().toString()); // ID нового водителя

        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            if (CarMethods.checkFieldsCar("Update", car.getIdCar() ,tfMark, tfColor, tfGovernmentNumber, tfYearOfIssue, cbState, tfVehicleRegCertificateNumber))
                return isUpdate;

            // Запрос на изменение Автомобиля
            String sqlCommand = "UPDATE TAXI.CARS SET MARK = ?, COLOR = ?, GOVERNMENTNUMBER = ?, YEAROFISSUE = ?, STATECAR = ?, VEHICLEREGCERTIFICATENUMBER = ?, IDDRIVER = ? WHERE IDCAR = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setString(1, tfMark.getText());
            preparedStatement.setString(2, tfColor.getText());
            preparedStatement.setString(3, tfGovernmentNumber.getText());
            preparedStatement.setString(4, tfYearOfIssue.getText());
            preparedStatement.setString(5, cbState.getValue().toString());
            preparedStatement.setString(6, tfVehicleRegCertificateNumber.getText());
            preparedStatement.setInt(7, car.getIdDriver());
            if (cbFreeDrivers.getValue().toString().equals("Не выбрано")) preparedStatement.setInt(7, 0);
            else preparedStatement.setInt(7, HelperMethods.getIdFromCB(cbFreeDrivers.getValue().toString()));
            preparedStatement.setInt(8, car.getIdCar());
            isUpdate = preparedStatement.executeUpdate();

            // Изменение ID Автомобиля у нового Водителя в таблице "Водители"
            sqlCommand = "UPDATE TAXI.DRIVERS SET IDCAR = ? WHERE IDDRIVER = ?";
            preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setInt(1, car.getIdCar());
            preparedStatement.setInt(2, idNewDriver);
            preparedStatement.executeUpdate();

            // Удаление ID Автомобиля у предыдущего Водителя в таблице "Водители"
            sqlCommand = "UPDATE TAXI.DRIVERS SET IDCAR = 0 WHERE IDDRIVER = ?";
            preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setInt(1, idPreviousDriver);
            preparedStatement.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (isUpdate != -1) alert.setHeaderText("Запись изменена");
            else alert.setHeaderText("Запись не изменена");
            alert.show();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isUpdate;
    }

    /**
     * Добавить в таблицу "Водители" ID Автомобиля для указанного водителя
     */
    private void updateDriverAtIdCar() {
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "UPDATE TAXI.DRIVERS SET IDCAR = ? WHERE IDDRIVER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setInt(1, car.getIdCar());
            preparedStatement.setInt(2, car.getIdDriver());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}