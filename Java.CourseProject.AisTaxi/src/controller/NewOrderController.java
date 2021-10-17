package controller;

import functions.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Client;
import model.Order;

import java.sql.*;
import java.util.Calendar;

public class NewOrderController {
    // Связывание компонентов ComboBox с контроллером
    @FXML
    ComboBox cbOnLineDrivers, cbAllClients, cbState;

    // Связывание компонентов TextField с контроллером
    @FXML
    TextField tfDriver, tfSurname, tfFirstName, tfPatronymic, tfPhoneNumber, tfStartTime, tfStartAddress, tfEndAddress;

    @FXML Button btnAddClient;

    // Связывание компонентов TextField с контроллером
    @FXML
    DatePicker dpStartDate;

    /** Поле "Окно" */
    private Stage window;

    /** Поле "Заказ" */
    private Order order;

    /** Поле "Тип запроса" */
    public static String typeQuery;

    /** Функция инициализации */
    @FXML
    private void initialize() {
        updateCbClient();
        connectListenersComboBox();

        // ComboBox Статус
        ObservableList<String> states = FXCollections.observableArrayList("Выполяется", "Выполнен", "Отменён");
        cbState.setItems(states);
        cbState.setValue("Не выбрано"); // Элемент по умолчанию

        // ComboBox "Водители на линии"
        ObservableList<String> onLineDrivers = FXCollections.observableArrayList();
        onLineDrivers.addAll(DriverMethods.getOnLineDrivers());
        cbOnLineDrivers.setItems(onLineDrivers);
    }

    /**
     * Обновить ComboBox "Все клиенты"
     */
    public void updateCbClient() {
        // ComboBox "Все клиенты"
        ObservableList<String> olAllClients = FXCollections.observableArrayList();
        olAllClients.addAll(ClientMethods.getAllClients());
        cbAllClients.setItems(olAllClients);
    }

    /**
     * Функция для подключения слушателей компонентов ComboBox
     */
    public void connectListenersComboBox() {
        // Слушатель для ComboBox "Все водители"
        cbOnLineDrivers.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object currentValue) {
                tfDriver.setText(currentValue.toString());
            }
        });

        // Слушатель для ComboBox "Все клиенты"
        cbAllClients.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object currentValue) {
                tfSurname.setText(ClientMethods.parseFieldsClientFromCB(currentValue.toString(), "Surname"));
                tfFirstName.setText(ClientMethods.parseFieldsClientFromCB(currentValue.toString(), "Firstname"));
                tfPatronymic.setText(ClientMethods.parseFieldsClientFromCB(currentValue.toString(), "Patronymic"));
                tfPhoneNumber.setText(ClientMethods.parseFieldsClientFromCB(currentValue.toString(), "PhoneNumber"));
            }
        });
    }

    public void setAddStage(Stage window) {
        window.setResizable(false);
        this.window = window;
        if (typeQuery.equals("Update")) {
            cbOnLineDrivers.setDisable(true);
            tfFirstName.setDisable(true);
            tfSurname.setDisable(true);
            tfPatronymic.setDisable(true);
            tfPhoneNumber.setDisable(true);
            btnAddClient.setDisable(true);
            cbAllClients.setDisable(true);
        }
    }

    /**
     * Функиция для передачи объекта Order
     *
     * @param order - водитель
     */
    public void setOrder(Order order, Client client) {
        this.order = order;
        // Заполнение полей TextField данными объекта Заказ
        // При добавлении все поля пустые, при изменении поля заполняются данными объекта Order
        if (order.getIdOrder() != 0) {
            cbOnLineDrivers.setValue(order.getIdDriver());
            dpStartDate.setValue(order.getStartDate().toLocalDate());
            tfStartTime.setText(order.getStartTime());
            tfStartAddress.setText(order.getStartAddress());
            tfEndAddress.setText(order.getEndAddress());
            cbState.setValue(order.getOrderStatus());

            tfFirstName.setText(client.getFirstname());
            tfSurname.setText(client.getLastname());
            tfPatronymic.setText(client.getPatronymic());
            tfPhoneNumber.setText(client.getPhoneNumber());
        }
    }

    /**
     * Событие, которое срабатывает при нажатии на кнопку "Подтвердить" на окне "Добавление/изменение заказа"
     */
    public void onConfirm(ActionEvent actionEvent) {
        if (typeQuery.equals("Insert")) {
            int add = addOrder();
            if (add != -1) {
                window.close();
            }
        } else if (typeQuery.equals("Update")) {
            int update = updateOrder();
            if (update != -1)
                window.close();
        }
    }

    /**
     * Событие, которое срабатывает при нажатии на кнопку "Отменить" на окне "Добавление/изменение водителя"
     */
    public void onCancel(ActionEvent actionEvent) {
        window.close();
    }

    /**
     * Добавить заказ в базу данных
     *
     * @return isAdd количество добавленных записей или -1, если их нет
     */
    private int addOrder() {
        int isAdd = -1;
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            // Проверка полей
            if (OrderMethods.checkFieldsOrder(tfDriver, tfSurname, tfFirstName, tfPatronymic, tfPhoneNumber, dpStartDate, tfStartTime, tfStartAddress, tfEndAddress, cbState))
                return isAdd;

            // Добавление записи Заказ
            String sqlCommand = "INSERT TAXI.ORDERS (IDDRIVER, IDCLIENT, STARTDATE, STARTTIME, STARTADDRESS, ENDADDRESS, ORDERSTATUS) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);

            // Установка свойств объекту Order
            order.setIdDriver(HelperMethods.getIdFromCB(tfDriver.getText()));
            order.setIdClient(ClientMethods.getIdClientAtPhoneNumber(tfPhoneNumber.getText()));
            order.setStartDate(Date.valueOf(dpStartDate.getValue()));
            order.setStartTime(tfStartTime.getText());
            order.setStartAddress(tfStartAddress.getText());
            order.setEndAddress(tfEndAddress.getText());
            order.setOrderStatus(cbState.getValue().toString());

            // Установка значений для подготовленного запроса
            preparedStatement.setInt(1, order.getIdDriver());
            preparedStatement.setInt(2, order.getIdClient());
            preparedStatement.setDate(3, order.getStartDate());
            preparedStatement.setString(4, order.getStartTime());
            preparedStatement.setString(5, order.getStartAddress());
            preparedStatement.setString(6, order.getEndAddress());
            preparedStatement.setString(7, order.getOrderStatus());

            // Выполнение запроса
            isAdd = preparedStatement.executeUpdate();

            if (isAdd != -1) {
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
            alert.setHeaderText("Запись не добавлена");
            alert.show();
        }
        return isAdd;
    }

    private int updateOrder() {
        int isUpdate = -1;
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            if (OrderMethods.checkFieldsOrder(tfDriver, tfSurname, tfFirstName, tfPatronymic, tfPhoneNumber, dpStartDate, tfStartTime, tfStartAddress, tfEndAddress, cbState))
                return isUpdate;

            // Изменение записи Заказ
            String sqlCommand = "UPDATE TAXI.ORDERS SET IDDRIVER = ?, IDCLIENT = ?, STARTDATE = ?, STARTTIME = ?, STARTADDRESS = ?, ENDADDRESS = ?, ORDERSTATUS = ? WHERE IDORDER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);

            // Установка значений для подготовленного запроса
            preparedStatement.setInt(1, order.getIdDriver());
            preparedStatement.setInt(2, order.getIdClient());
            preparedStatement.setDate(3, Date.valueOf(dpStartDate.getValue()));
            preparedStatement.setString(4, tfStartTime.getText());
            preparedStatement.setString(5, tfStartAddress.getText());
            preparedStatement.setString(6, tfEndAddress.getText());
            preparedStatement.setString(7, cbState.getValue().toString());
            preparedStatement.setInt(8, order.getIdOrder());
            isUpdate = preparedStatement.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (isUpdate != -1) alert.setHeaderText("Запись изменена");
            else alert.setHeaderText("Запись не изменена");
            alert.show();
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Запись не изменена" + "\n" + throwables.getMessage());
            alert.show();
        }
        return isUpdate;
    }

    /**
     * Добавить клиента в базу данных
     * @return isAdd количество добавленных записей или -1, если их нет
     */
    public int onAddClient(ActionEvent actionEvent) {
        int isAdd = -1;
        // Проверка полей
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            if (ClientMethods.checkFieldsClient(tfSurname, tfFirstName, tfPatronymic, tfPhoneNumber))
                return isAdd;

            String sqlCommand = "INSERT TAXI.CLIENTS (FIRSTNAME, LASTNAME, PATRONYMIC, PHONENUMBER) VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setString(1, tfFirstName.getText());
            preparedStatement.setString(2, tfSurname.getText());
            preparedStatement.setString(3, tfPatronymic.getText());
            preparedStatement.setString(4, tfPhoneNumber.getText());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (ClientMethods.checkPhoneNumberAtDublicate(tfPhoneNumber.getText()) != -1) {
                isAdd = preparedStatement.executeUpdate();
                if (isAdd != -1) {
                    alert.setHeaderText("Клиент добавлен");
                    alert.show();
                } else {
                    alert.setHeaderText("Клиент не добавлен");
                    alert.show();
                }
            } else {
                alert.setHeaderText("Такой номер телефона уже имеется");
                alert.show();
            }
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Клиент не добавлен" + "\n" + throwables.getMessage());
            alert.show();
        }
        updateCbClient(); // Обновление ComboBox со всеми клиентами
        return isAdd;
    }

    /**
     * Функция для добавления текущего времени в TextField
     */
    public void onAddTime(ActionEvent actionEvent) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String time = hour + ":" + minute;
        tfStartTime.setText(time);
    }
}