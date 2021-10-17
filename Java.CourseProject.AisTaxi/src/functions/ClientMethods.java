package functions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.Client;

import java.sql.*;

/**
 * Класс с методами для работы с таблицей "Клиенты"
 */
public class ClientMethods {

    /**
     * Функция для получения всех клиентов в базе данных
     * @return allClients - Связанный список "Все клиенты"
     */
    public static ObservableList<String> getAllClients() {
        ObservableList<String> allClients = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT * FROM TAXI.CLIENTS";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while (resultSet.next()) {
                allClients.add("(" + resultSet.getInt("IdClient") + ")" + " " +
                        resultSet.getString("LastName") + " " +
                        resultSet.getString("FirstName") + " " +
                        resultSet.getString("Patronymic") + " | " +
                        resultSet.getString("PhoneNumber"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return allClients;
    }

    /**
     * Функция для проверки полей Клиента на окне добавления/изменения Заказа
     * @param tfSurname - Фамилия клиента
     * @param tfFirstName - Имя клиента
     * @param tfPatronymic - Отчество клиента
     * @param tfPhoneNumber - Номер телефона
     * @return
     */
    public static boolean checkFieldsClient(TextField tfSurname, TextField tfFirstName, TextField tfPatronymic, TextField tfPhoneNumber) {
        boolean isError = false;
        Alert alert = new Alert(Alert.AlertType.ERROR);

        StringBuilder messageError = new StringBuilder();
        StringBuilder emptyError = new StringBuilder("Следующие поля не могут быть пустыми: ");
        StringBuilder letterError = new StringBuilder("Следующие поля должны содержать только буквы: ");
        String digitError = "";

        // Проверка полей на незаполненность
        if (tfFirstName.getText().isEmpty()) emptyError.append("\"Имя\" ");
        if (tfSurname.getText().isEmpty()) emptyError.append("\"Фамилия\" ");
        if (tfPhoneNumber.getText().isEmpty()) emptyError.append("\"Номер телефона\"");

        // Проверка полей, которые должны содержать только буквы
        if (!HelperMethods.onlyLetters(tfSurname.getText())) letterError.append("\"Фамилия\" ");
        if (!HelperMethods.onlyLetters(tfFirstName.getText())) letterError.append("\"Имя\" ");
        if (!HelperMethods.onlyLetters(tfPatronymic.getText())) letterError.append("\"Отчество\"");

        // Проверка полей, которые должны содержать только цифры
        if (!HelperMethods.onlyDigits(tfPhoneNumber.getText())) digitError = "Поле \"Номер телефона\" может содержать только цифры и знак \"+\"";

        // Добавление запятых
        HelperMethods.addCommas(emptyError);
        HelperMethods.addCommas(letterError);

        // Формирование сообщения об ошибке
        if (!emptyError.toString().equals("Следующие поля не могут быть пустыми: "))
            messageError.append(emptyError + "\r");
        if (!letterError.toString().equals("Следующие поля должны содержать только буквы: "))
            messageError.append(letterError + "\r");
        if (!digitError.isEmpty())
            messageError.append(digitError + "\r");

        if (!messageError.toString().isEmpty()) {
            isError = true;
            alert.setHeaderText(messageError.toString());
            alert.show();
        }
        return isError;
    }

    /**
     * Функция для обработки значения из ComboBox с клиентами для получения имени, фамилии, отчества, номера телефона
     * @param value - значение из ComboBox
     * @param type - тип поля (фамилия, имя, отчество, номер телефона)
     * @return surname, firstname, patronymic, phoneNumber - в зависимости от типа поля
     */
    public static String parseFieldsClientFromCB(String value, String type) {
        String surname = "";
        String firstname = "";
        String patronymic = "";
        String phoneNumber = "";

        if (!value.isEmpty()) {
            int startSurname = value.indexOf(" ");
            int endSurname = value.indexOf(" ", startSurname + 1);
            surname = value.substring(startSurname + 1, endSurname);

            int endFirstname = value.indexOf(" ", endSurname + 1);
            firstname = value.substring(endSurname + 1, endFirstname);

            int endPatronymic = value.indexOf(" ", endFirstname + 1);

            if (endPatronymic != -1) {
                patronymic = value.substring(endFirstname + 1, endPatronymic);
            }

            int startPhoneNumber = value.indexOf("|");
            phoneNumber = value.substring(startPhoneNumber + 2);
        }
        if (type.equals("Surname"))
            return surname;
        if (type.equals("Firstname"))
            return firstname;
        if (type.equals("Patronymic"))
            return patronymic;
        if (type.equals("PhoneNumber"))
            return phoneNumber;
        return "";
    }

    /**
     * Функция для проверки номера телефона на дубликан
     * @param phoneNumber - номер телефона
     * @return isPhoneNumber - наличие номера телефона
     */
    public static int checkPhoneNumberAtDublicate(String phoneNumber) {
        int isPhoneNumber = -1;
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT * FROM TAXI.CLIENTS WHERE PHONENUMBER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setString(1, phoneNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return isPhoneNumber;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        isPhoneNumber = 1;
        return isPhoneNumber;
    }

    /**
     * Функция для получения ID Клиента по номеру телефона
     * @param phoneNumber - номер телефона
     * @return idClient - ID Клиента
     */
    public static int getIdClientAtPhoneNumber(String phoneNumber) {
        int idClient = -1;
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT IDCLIENT FROM TAXI.CLIENTS WHERE PHONENUMBER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setString(1, phoneNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                idClient = resultSet.getInt("IdClient");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return idClient;
    }

    /**
     * Функция для получения объекта Client по ID Клиента
     * @param idClient - ID Клиента
     * @return Client - экземпляр класса Клиент
     */
    public static Client getClient(int idClient) {
        Client client = new Client();
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT * FROM TAXI.CLIENTS WHERE IDCLIENT = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setInt(1, idClient);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                client.setIdClient(idClient);
                client.setFirstname(resultSet.getString("FirstName"));
                client.setLastname(resultSet.getString("LastName"));
                client.setPatronymic(resultSet.getString("Patronymic"));
                client.setPhoneNumber(resultSet.getString("PhoneNumber"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return client;
    }
}