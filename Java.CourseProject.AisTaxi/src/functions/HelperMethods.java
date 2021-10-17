package functions;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

/**
 * Класс с общими вспомогательными методами
 */
public class HelperMethods {
    public static String DB_URL = "jdbc:mysql://localhost:3306/taxi";
    public final static String USER = "root";
    public final static String PASSWORD = "root";

    public static boolean isConnect = false;

    /**
     * Функция для подключения к базе данных
     */
    // Подключение к базе данных
    public static void connection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Ошибка загрузки драйвера!" + "\n" + ex.getMessage());
            alert.show();
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            isConnect = true;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Подключение");
            alert.setHeaderText("Подключение успешно выполнено");
            alert.show();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1049) {
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", USER, PASSWORD)) {
                    String sqlCommand = "CREATE DATABASE TAXI";
                    Statement query = connection.createStatement();
                    query.executeUpdate(sqlCommand);

                    query = connection.createStatement();
                    sqlCommand = "CREATE TABLE TAXI.DRIVERS(" +
                            "IdDriver int auto_increment primary key not null," +
                            "IdCar int not null," +
                            "FirstName varchar(45) not null," +
                            "Surname varchar(45) not null," +
                            "Patronymic varchar(45)," +
                            "Nickname varchar(45) not null," +
                            "DriversLicenseNumber varchar(45) not null," +
                            "HoursWorked int not null," +
                            "State varchar(45) not null," +
                            "DateOfIssue varchar(45) not null" +
                            ")";
                    query.executeUpdate(sqlCommand);

                    sqlCommand = "CREATE TABLE TAXI.CARS(" +
                            "IdCar int auto_increment primary key not null," +
                            "IdDriver int," +
                            "Mark varchar(45) not null," +
                            "Color varchar(45) not null," +
                            "StateCar varchar(45) not null," +
                            "GovernmentNumber varchar(45) not null," +
                            "YearOfIssue varchar(45) not null," +
                            "VehicleRegCertificateNumber varchar(45) not null)";
                    query.executeUpdate(sqlCommand);

                    sqlCommand = "CREATE TABLE TAXI.ORDERS(" +
                            "IdOrder int auto_increment primary key not null," +
                            "IdDriver int not null," +
                            "StartTime time not null," +
                            "StartDate date not null," +
                            "StartAddress varchar(45) not null," +
                            "EndAddress varchar(45) not null" +
                            ")";
                    query.executeUpdate(sqlCommand);

                    isConnect = true;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Успешное подключение");
                    alert.show();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Ошибка подключения!" + "\n" + ex.getMessage());
                alert.show();
            }
        }
    }

    /**
     * Функция для добавления запятых в сообщении об ошибке
     * @param sbMessageError - сообщение об ошибке
     */
    // Формирование сообщения об ошибке (добавление запятых)
    public static void addCommas(StringBuilder sbMessageError) {
        if (!sbMessageError.toString().equals("")) {
            // Добавление запятых к сообщению об ошибке
            int start = 0;
            while (start != sbMessageError.length() - 1) { // Отсчёт начинается с 0, последний символ (") 124 из 125, чтобы цикл завершился
                start = sbMessageError.indexOf("\"", start + 1);
                if (start == sbMessageError.length() - 1) continue; // Чтобы не вышло за границы массива
                if (start == -1) return; // Если нет кавычек
                else if (sbMessageError.toString().charAt(start + 1) == ' ') {
                    sbMessageError.replace(start + 1, start + 1, ",");
                }
            }
        }
    }

    /**
     * Функция для проверки года вида "ГГГГ"
     * @param inputYear - введённый год
     * @return yearError - сообщение с ошибкой, "" - если её нет
     */
    public static String checkYear(String inputYear) {
        // Если больше четырёх символов то всё
        String yearError = "";

        Calendar calendar = Calendar.getInstance(); // Почему Calendar, а не Date, потому что Java говорит, что метод класса Date для получения года устарел,
        int currentYear = calendar.get(calendar.YEAR);

        // Проверить можно ли объединить в одно условие
        if (!inputYear.isEmpty()) {
            if (inputYear.length() != 4)
                return yearError = "В поле \"Год выпуска\", количество символов должно быть равно четырём";
            if (Integer.parseInt(inputYear) > currentYear)
                yearError = "В поле \"Год выпуска\", год не может быть больше текущего";
        }
        return yearError;
    }

    /**
     * Функция для получения ID из компонента ComboBox
     * @param s - строковое значение типа "(0) ФИО"
     * @return id
     */
    public static int getIdFromCB(String s) {
        int id = 0;
        if(!s.isEmpty()) {
            int endId = s.indexOf(')');
            if (endId == -1) return id;
            id = Integer.parseInt(s.substring(1, endId));
        }
        return id;
    }

    /**
     * Функция для изменения представления даты
     * @param s - строковое представление даты
     * @param type - тип форматирования (Default - dd.mm.yyyy, DataBase - yyyy-mm-dd)
     * @return newDate - изменённый формат даты
     */
    // По точкам, а не по индексам, исправить
    public static String formatDate(String s, String type) {
        String newDate = "";

        if (!s.isEmpty()) {
            // From "yyyy-mm-dd" to dd.mm.yyyy
            if (type.equals("Default")) {
                int yearEnd = s.indexOf('-');
                int year = Integer.parseInt(s.substring(0, yearEnd));

                int monthEnd = s.indexOf('-', yearEnd + 1);
                int month = Integer.parseInt(s.substring(yearEnd + 1, monthEnd));

                int day = Integer.parseInt(s.substring(monthEnd + 1));

                newDate = day + "." + month + "." + year;

            }
            // From "dd.mm.yyyy" to "yyyy-mm-dd"
            if (type.equals("DataBase")) {
                int dayEnd = s.indexOf('.');
                int day = Integer.parseInt(s.substring(0, dayEnd));

                int monthEnd = s.indexOf('.', dayEnd + 1);
                int month = Integer.parseInt(s.substring(dayEnd + 1, monthEnd));

                int year = Integer.parseInt(s.substring(monthEnd + 1));
                newDate = year + "-" + month + "-" + day;
            }
        }
        return newDate;
    }

    /**
     * Функция для проверки, содержит ли строка цифру
     * @param s - строковое представление
     * @return containsDigit - наличие числа
     */
    // Содержит ли строка число
    public static boolean containsDigit(String s) {
        boolean containsDigit = false;

        if (!s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (containsDigit = Character.isDigit(c))
                    break;
            }
        }
        return containsDigit;
    }

    /**
     * Функция для проверки, содержит ли строка букву
     * @param s - строковое представление
     * @return containsDigit - наличие буквы
     */
    // Содержит ли строка букву
    public static boolean containsLetter(String s) {
        boolean containsLetter = false;

        if (!s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (containsLetter = Character.isLetter(c))
                    break;
            }
        }
        return containsLetter;
    }

    /**
     * Функция для проверки, содержит ли строка только буквы
     * @param s - строковое представление
     * @return onlyLetters - только буквы
     */
    public static boolean onlyLetters(String s) {
        boolean onlyLetters = true;
        if (!s.isEmpty()) {
            for (int i = 0; i < s.length(); i++) {
                char symbol = s.charAt(i);
                if (Character.isLetter(symbol)) {
                    continue;
                } else onlyLetters = false;
            }
        }
        return onlyLetters;
    }

    /**
     * Функция для проверки, содержит ли строка только цифры
     * @param s - строковое представление
     * @return onlyLetters - только цифры
     */
    public static boolean onlyDigits(String s) {
        boolean onlyDigits = true;
        if (!s.isEmpty()) {
            for (int i = 0; i < s.length(); i++) {
                char symbol = s.charAt(i);
                if (Character.isDigit(symbol)) {
                    continue;
                } else onlyDigits = false;
            }
        }
        return onlyDigits;
    }
}