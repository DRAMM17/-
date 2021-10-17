package functions;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * Класс с методами для работы с таблицей "Заказы"
 */
public class OrderMethods {
    /**
     * Функция для проверки полей Заказа в окне добавления/изменения Заказа
     * @param tfDriver - Водитель
     * @param tfSurname - Фамилия Клиента
     * @param tfFirstName - Имя Клиента
     * @param tfPatronymic - Отчество Клиента
     * @param tfPhoneNumber - Номер телефона Клиента
     * @param dpStartDate - Дата начала принятия Заказа
     * @param tfStartTime - Время начала принятия Заказа
     * @param tfStartAddress - Начальный адрес
     * @param tfEndAddress - Конечный адрес
     * @param cbState - Статус Заказа
     * @return isError - наличие ошибки
     */
    public static boolean checkFieldsOrder(TextField tfDriver, TextField tfSurname, TextField tfFirstName, TextField tfPatronymic, TextField tfPhoneNumber, DatePicker dpStartDate, TextField tfStartTime, TextField tfStartAddress, TextField tfEndAddress, ComboBox cbState) {
        boolean isError = false;
        Alert alert = new Alert(Alert.AlertType.ERROR);

        StringBuilder messageError = new StringBuilder();
        StringBuilder emptyError = new StringBuilder("Следующие поля не могут быть пустыми: ");
        StringBuilder letterError = new StringBuilder("Следующие поля должны содержать только буквы: ");
        String digitError = "";

        // Проверка полей на незаполненность
        if (tfDriver.getText().isEmpty()) emptyError.append("\"Водитель\" ");
        if (tfSurname.getText().isEmpty()) emptyError.append("\"Фамилия\" ");
        if (tfFirstName.getText().isEmpty()) emptyError.append("\"Имя\" ");
        if (tfPhoneNumber.getText().isEmpty()) emptyError.append("\"Номер телефона\" ");
        if (String.valueOf(dpStartDate.getValue()).equals("null")) emptyError.append("\"Дата приёма заказа\" ");
        if (tfStartTime.getText().isEmpty()) emptyError.append("\"Время приёма\" ");
        if (tfStartAddress.getText().isEmpty()) emptyError.append("\"Начальный адрес\" ");
        if (tfEndAddress.getText().isEmpty()) emptyError.append("\"Конечный адрес\" ");
        if (cbState.getValue().toString().equals("Не выбрано")) emptyError.append("\"Статус\"");

        // Проверка на буквы там, где должны быть только они
        if (!HelperMethods.onlyLetters(tfSurname.getText())) letterError.append("\"Фамилия\" ");
        if (!HelperMethods.onlyLetters(tfFirstName.getText())) letterError.append("\"Имя\" ");
        if (!HelperMethods.onlyLetters(tfPatronymic.getText())) letterError.append("\"Отчество\"");

        // Проверка на цифры там, где должны быть только они
        if (!HelperMethods.onlyDigits(tfPhoneNumber.getText())) digitError = "Поле \"Номер телефона\" может содержать только цифры и знак \"+\"";

        // Добавление запатых
        HelperMethods.addCommas(emptyError);
        HelperMethods.addCommas(letterError);

        // Формирование сообщения об ошибке
        if (!digitError.isEmpty())
            messageError.append(digitError + "\r");
        if (!emptyError.toString().equals("Следующие поля не могут быть пустыми: "))
            messageError.append(emptyError + "\r");
        if (!letterError.toString().equals("Следующие поля должны содержать только буквы: "))
            messageError.append(letterError + "\r");

        if (!messageError.toString().isEmpty()) {
            isError = true;
            alert.setHeaderText(messageError.toString());
            alert.show();
        }
        return isError;
    }
}