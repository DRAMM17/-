package functions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import model.Driver;

import java.sql.*;

/**
 * Класс с методами для работы с таблицей "Водители"
 */
public class DriverMethods {

    /**
     * Функция для получения строкового представления вида (ID) Фамилия Имя Отчество "Прозвище" по ID Водителя
     * @param id - ID Водителя
     * @return driver - Водитель
     */
    public static String getDriver(int id) {
        String driver = "Не выбрано";
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT * FROM TAXI.DRIVERS WHERE IDDRIVER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int idDriver = resultSet.getInt("IdDriver");
                String surname = resultSet.getString("Surname");
                String firstName = resultSet.getString("FirstName");
                String patronymic = resultSet.getString("Patronymic");
                String nickname = resultSet.getString("Nickname");
                driver = "(" + idDriver + ")" + " " + surname + " " + firstName + " " + patronymic + " " + "\"" + nickname + "\"";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return driver;
    }

    /**
     * Функция для получения ID Водителя по серии и номеру водительского удостоверения
     * @param driversLicenseNumber - Серия и номер водительского удостоверения
     * @return idDriver - ID Водителя
     */
    public static int getDriverAtDriversLicenseNumber(String driversLicenseNumber) {
        int idDriver = -1;
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT IDDRIVER FROM TAXI.DRIVERS WHERE DRIVERSLICENSENUMBER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setString(1, driversLicenseNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                idDriver = resultSet.getInt("IdDriver");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return idDriver;
    }

    /**
     * Функция для проверки полей Клиента на окне добавления/изменения Заказа
     * @param type - тип запроса (добавление или изменение)
     * @param idDriver - ID Водителя
     * @param tfFirstName - Имя водителя
     * @param tfSurname - Фамилия водителя
     * @param tfPatronymic - Отчество водителя
     * @param tfNickname - Прозвище водителя
     * @param tfHoursWorked - Количество отработанных часов
     * @param tfDriversLicenseNumber - Серия и номер водительского удостоверения
     * @param dpDateOfIssue - Дата выдачи водительского удостоверения
     * @param cbStates - Состояние
     * @return isError - наличие ошибки
     */
    public static boolean checkFieldsDriver(String type, int idDriver, TextField tfFirstName, TextField tfSurname, TextField tfPatronymic, TextField tfNickname, TextField tfHoursWorked,TextField tfDriversLicenseNumber, DatePicker dpDateOfIssue, ComboBox cbStates) {
        boolean isError = false;
        Alert alert = new Alert(Alert.AlertType.ERROR);

        StringBuilder messageError = new StringBuilder("");
        StringBuilder digitError = new StringBuilder("Следующие поля не могут содержать цифры: ");
        StringBuilder emptyError = new StringBuilder("Следующие поля не могут быть пустыми: ");
        StringBuilder letterError = new StringBuilder("Следующие поля не могут содержать буквы: ");
        StringBuilder countSymbolsError = new StringBuilder("");
        String driversLicenseNumberError = "";

        // Проверка на наличие чисел там, где их быть не должно
        if (HelperMethods.containsDigit(tfFirstName.getText())) digitError.append("\"Имя\" ");
        if (HelperMethods.containsDigit(tfSurname.getText())) digitError.append("\"Фамилия\" ");
        if (HelperMethods.containsDigit(tfPatronymic.getText())) digitError.append("\"Отчество\"");

        // Проверка на наличие букв там, где их быть не должно
        if (HelperMethods.containsLetter(tfDriversLicenseNumber.getText())) letterError.append("\"Номер водительского удостоверения\" ");

        // Проверка поля "Серия и номер водительского удостоверения" на наличие его в базе данных
        if (!tfDriversLicenseNumber.getText().isEmpty()) {
                if (type.equals("Update")) {
                    if (DriverMethods.checkDriversLicenseNumber(idDriver, tfDriversLicenseNumber.getText(), "Update") == 1)
                        driversLicenseNumberError = "Серия и номер водительского удостоверения уже есть в базе данных";
                }
                if (type.equals("Add")) {
                    // Получение ID Водителя по номеру лицензии
                    idDriver = getDriverAtDriversLicenseNumber(tfDriversLicenseNumber.getText());
                    if (DriverMethods.checkDriversLicenseNumber(idDriver, tfDriversLicenseNumber.getText(), "Add") == 1)
                        driversLicenseNumberError = "Серия и номер водительского удостоверения уже есть в базе данных";
                }
        }

        // Проверка поля "Серия и номер водительского удостоверения" на количество символов
        if (!tfDriversLicenseNumber.getText().isEmpty()) {
            if (tfDriversLicenseNumber.getText().length() != 10)
                countSymbolsError.append("Поле \"Серия и номер водительского удостоверения\" должно состоять из 10 символов");
        }

        // Проверка на незаполненность TextField полей
        if (tfFirstName.getText().equals("")) emptyError.append("\"Имя\" ");
        if (tfSurname.getText().equals("")) emptyError.append("\"Фамилия\" ");
        if (tfNickname.getText().equals("")) emptyError.append("\"Прозвище\" ");
        if (tfHoursWorked.getText().isEmpty()) emptyError.append("\"Количество отработанных часов\" ");
        if (tfDriversLicenseNumber.getText().equals("")) emptyError.append("\"Номер водительского удостоверения\" ");
        if (cbStates.getValue().equals("")) emptyError.append("\"Состояние\" ");
        if (String.valueOf(dpDateOfIssue.getValue()).equals("null")) emptyError.append("\"Дата выдачи водительского удостоверения\"");

        // Добавление запятых
        HelperMethods.addCommas(digitError);
        HelperMethods.addCommas(emptyError);

        // Формирование сообщения об ошибке
        if (!emptyError.toString().equals("Следующие поля не могут быть пустыми: "))
            messageError.append(emptyError + "\r");

        if (!digitError.toString().equals("Следующие поля не могут содержать цифры: "))
            messageError.append(digitError + "\r");

        if (!letterError.toString().equals("Следующие поля не могут содержать буквы: "))
            messageError.append(letterError + "\r");

        if (!driversLicenseNumberError.isEmpty())
            messageError.append(driversLicenseNumberError + "\r");

        if (!countSymbolsError.toString().equals(""))
            messageError.append(countSymbolsError + "\r");

        if (!messageError.toString().equals("")) {
            isError = true;
            alert.setHeaderText(messageError.toString());
            alert.show();
        }
        return isError;
    }

    /**
     * Функция для получения всех водителей без машин
     * @return freeDrivers - связанный список с водителями без машин
     */
    public static ObservableList<String> getFreeDrivers() {
        ObservableList<String> freeDrivers = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT * FROM TAXI.DRIVERS WHERE IDCAR IS NULL OR IDCAR = \"\" OR IDCAR = 0";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while (resultSet.next()) {
                int idDriver = resultSet.getInt("IdDriver");
                String surname = resultSet.getString("Surname");
                String firstName = resultSet.getString("FirstName");
                String patronymic = resultSet.getString("Patronymic");
                String nickname = resultSet.getString("Nickname");
                freeDrivers.add("(" + idDriver + ")" + " " + surname + " " + firstName + " " + patronymic + " " + "\"" + nickname + "\"");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return freeDrivers;
    }

    /**
     * Функция для проверки, совпадает ли добавляемый/изменяемый номер водительского удостоверения в БД
     * @param id - ID Водителя
     * @param driversLicenseNumber - Серия и номер водительского удостоверения
     * @param type - тип запроса
     * @return isDriversLicenseNumber - наличие серии и номера водительского удостоверения
     */
    public static int checkDriversLicenseNumber(int id, String driversLicenseNumber, String type) {
        int isDriversLicenseNumber = -1;
        String sqlCommand = "";

        if (!driversLicenseNumber.isEmpty()) {
            try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
                if (type.equals("Add"))
                    sqlCommand = "SELECT * FROM TAXI.DRIVERS WHERE DRIVERSLICENSENUMBER = ?";
                if (type.equals("Update"))
                    sqlCommand = "SELECT * FROM TAXI.DRIVERS WHERE DRIVERSLICENSENUMBER = ? AND IDDRIVER != ?";

                PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);

                preparedStatement.setString(1, driversLicenseNumber);

                if (type.equals("Update"))
                    preparedStatement.setInt(2, id);

                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    isDriversLicenseNumber = 1;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return isDriversLicenseNumber;
    }

    /**
     * Получить IDDRIVER только что добавленного водителя
     * @return idDriver - ID Водителя
     */
    public static int getAddedIdDriver(Driver driver) {
        int idDriver = -1;
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT IDDRIVER FROM TAXI.DRIVERS WHERE NICKNAME = ? AND DRIVERSLICENSENUMBER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setString(1, driver.getNickname());
            preparedStatement.setString(2, driver.getDriversLicenseNumber());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                idDriver = resultSet.getInt("IdDriver");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return idDriver;
        }
        return idDriver;
    }

    /**
     * Функция для получения списка водителей на линии
     * @return onLineDrivers - связанный список водителей на линии
     */
    public static ObservableList<String> getOnLineDrivers () {
        ObservableList<String> onLineDrivers = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlQuery = "SELECT * FROM TAXI.DRIVERS WHERE ONLINE = \"Да\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            // Получение значений записи по названиям полей таблицы
            while (resultSet.next()) {
                onLineDrivers.add(
                        "(" + resultSet.getInt("IdDriver") + ")" + " " +
                                resultSet.getString("Surname") + " " +
                                resultSet.getString("FirstName") + " " +
                                resultSet.getString("Patronymic") + " " +
                                "\"" + resultSet.getString("Nickname") + "\""
                );
            }
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(throwables.toString());
            alert.show();
        }
        return onLineDrivers;
    }
}