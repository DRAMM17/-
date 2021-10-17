package functions;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import model.Car;

import java.sql.*;

/**
 * Класс с методами для работы с таблицей "Автомобили"
 */
public class CarMethods {

    /**
     * Функция проверки полей на окне добавления/изменения Автомобиля
     * @param type - тип запроса (добавление или изменение)
     * @param idCar - ID автомобиля
     * @param tfMark - поле "Марка/название автомобиля"
     * @param tfColor - поле - "Цвет"
     * @param tfGovernmentNumber - гос. номер
     * @param tfYearOfIssue - год выпуска
     * @param cbState - выпадающий список с состояниями
     * @param tfVehicleRegCertificateNumber - поле "Номер свидетельства о регистрации"
     * @return isError - наличие ошибки
     */
    public static boolean checkFieldsCar(String type, int idCar, TextField tfMark, TextField tfColor, TextField tfGovernmentNumber, TextField tfYearOfIssue, ComboBox cbState, TextField tfVehicleRegCertificateNumber) {
        boolean isError = false;
        Alert alert = new Alert(Alert.AlertType.ERROR);

        StringBuilder messageError = new StringBuilder("");
        StringBuilder digitError = new StringBuilder("Следующие поля не могут содержать цифры: ");
        StringBuilder letterError = new StringBuilder("Следующие поля не могут содержать буквы: ");
        StringBuilder emptyError = new StringBuilder("Следующие поля не могут быть пустыми: ");
        String governmentNumberError = "";

        // Проверка поля "Год выпуска" на корректность
        String yearError = HelperMethods.checkYear(tfYearOfIssue.getText());

        // Проверка на наличие чисел там, где их быть не должно
        if (HelperMethods.containsDigit(tfColor.getText())) digitError.append("\"Цвет\"");

        // Проверка на наличие букв там, где их быть не должно
        if (HelperMethods.containsLetter(tfYearOfIssue.getText())) letterError.append("\"Год выпуска\" ");
        if (HelperMethods.containsLetter(tfVehicleRegCertificateNumber.getText()))
            letterError.append("\"Номер свидетельства о регистрации\"");

        // Проверка на незаполненность полей
        if (tfMark.getText().equals("")) emptyError.append("\"Марка\" ");
        if (tfColor.getText().equals("")) emptyError.append("\"Цвет\" ");
        if (tfGovernmentNumber.getText().equals("")) emptyError.append("\"Государственный номер\" ");
        if (tfYearOfIssue.getText().equals("")) emptyError.append("\"Год выпуска\" ");
        if (cbState.getValue().equals("Не выбрано")) emptyError.append("\"Статус\" ");
        if (tfVehicleRegCertificateNumber.getText().equals(""))
            emptyError.append("\"Номер свидетельства о регистрации\"");

        // Проверка на запрещённые символы в поле "Гос. номер"
        if (checkGovernmentNumber(tfGovernmentNumber.getText())) governmentNumberError = "Поле \"Государственный номер содержит некорректные символы\"";

        // Проверка поля "Гос. номер" на наличие его в базе данных
        if (!tfGovernmentNumber.getText().isEmpty()) {
            if (type.equals("Update")) {
                if (checkGovernmentNumberAtDublicate(idCar, tfGovernmentNumber.getText(), "Update") == 1)
                    governmentNumberError = "Государственный номер уже есть в базе данных";
            }
            if (type.equals("Add")) {
                idCar = getIdCarAtGovernmentNumber(tfGovernmentNumber.getText());
                if (checkGovernmentNumberAtDublicate(idCar, tfGovernmentNumber.getText(), "Add") == 1)
                    governmentNumberError = "Государственный номер уже есть в базе данных";
            }
        }

        // Добавление запятых
        HelperMethods.addCommas(letterError);
        HelperMethods.addCommas(emptyError);

        //  Формирование сообщения об ошибке
        if (!digitError.toString().equals("Следующие поля не могут содержать цифры: "))
            messageError.append(digitError + "\r");
        if (!letterError.toString().equals("Следующие поля не могут содержать буквы: "))
            messageError.append(letterError + "\r");
        if (!emptyError.toString().equals("Следующие поля не могут быть пустыми: "))
            messageError.append(emptyError + "\r");
        if (!yearError.equals(""))
            messageError.append(yearError + "\r");
        if (!governmentNumberError.equals(""))
            messageError.append(governmentNumberError + "\r");

        if (!messageError.toString().isEmpty()) {
            isError = true;
            alert.setHeaderText(messageError.toString());
            alert.show();
        }
        return isError;
    }

    /**
     * Получить ID Автомобиля только что добавленного водителя
     * @return idCar - ID Автомобиля
     */
    public static int getAddedIdCar(Car car) {
        int idCar = -1;
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT IDCAR FROM TAXI.CARS WHERE GOVERNMENTNUMBER = ? AND VEHICLEREGCERTIFICATENUMBER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setString(1, car.getGovernmentNumber());
            preparedStatement.setString(2, car.getVehicleRegCertificateNumber());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                idCar = resultSet.getInt("IdCar");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return idCar;
    }

    /**
     * @param governmentNumber - гос. номер
     * @return idCar - ID Автомобиля
     */
    public static int getIdCarAtGovernmentNumber(String governmentNumber) {
        int idCar = -1;
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT IDCAR FROM TAXI.CARS WHERE GOVERNMENTNUMBER = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setString(1, governmentNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                idCar = resultSet.getInt("IdCar");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return idCar;
    }

    /**
     * Функиця проверки гос. номера на запрещённые символы
     * @param governmentNumber - гос. номер
     * @return containsForbiddenSymbols - наличие запрещённых символов
     */
    public static boolean checkGovernmentNumber(String governmentNumber) {
        boolean containsForbiddenSymbols = false;
        String arrayAllowedSymbols = "0 1 2 3 4 5 6 7 8 9 а в е к м н о р с т у х";
        if (!governmentNumber.isEmpty()) {
            governmentNumber = governmentNumber.toLowerCase();
            for (int i = 0; i < governmentNumber.toCharArray().length; i++) {
                char currentSymbol = governmentNumber.charAt(i);
                int isAllowedSymbol = arrayAllowedSymbols.indexOf(currentSymbol);
                // Если символа нет в списке разрешённых, то он запрещённый
                if (isAllowedSymbol == -1) {
                    containsForbiddenSymbols = true;
                    break;
                }
            }
        }
        return containsForbiddenSymbols;
    }

    /**
     * Проверка гос. номера на дубликат в базе данных
     * @param idCar - ID Автомобиля
     * @param governmentNumber - гос. номер
     * @param type - тип запроса
     * @return isGovernmentNumber - наличие гос. номера в базе данных
     */
    public static int checkGovernmentNumberAtDublicate(int idCar, String governmentNumber, String type) {
        int isGovernmentNumber = -1;
        String sqlCommand = "";

        if (!governmentNumber.isEmpty()) {
            try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
                if (type.equals("Add"))
                    sqlCommand = "SELECT * FROM TAXI.CARS WHERE GOVERNMENTNUMBER = ?";
                if (type.equals("Update"))
                    sqlCommand = "SELECT * FROM TAXI.CARS WHERE GOVERNMENTNUMBER = ? AND IDCAR != ?";

                PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
                preparedStatement.setString(1, governmentNumber);

                if (type.equals("Update"))
                    preparedStatement.setInt(2, idCar);

                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    isGovernmentNumber = 1;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return isGovernmentNumber;
    }

    /**
     * Функция получения информации о машине по ID Автомобиля
     * @param idCar
     * @return carInfo - информация о автомобиле ( (ID) Цвет Название )
     */
    public static String getCarInfoAtId(int idCar) {
        String carInfo = "Нет автомобиля";
        try (Connection connection = DriverManager.getConnection(HelperMethods.DB_URL, HelperMethods.USER, HelperMethods.PASSWORD)) {
            String sqlCommand = "SELECT IDCAR, COLOR, MARK FROM TAXI.CARS WHERE IDCAR = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setInt(1, idCar);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                carInfo = "(" + resultSet.getInt("IdCar") + ") " +
                               resultSet.getString("Color") + " " +
                               resultSet.getString("Mark");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return carInfo;
    }
}