package model;

/**
 * Класс-модель Клиент
 */
public class Client {
    /** Поле ID Клиента */
    private int idClient;

    /** Поле Имя */
    private String firstname;

    /** Поле Фамилия */
    private String lastname;

    /** Поле Отчество */
    private String patronymic;

    /** Поле Номер телефона */
    private String phoneNumber;

    /**
     * Конструктор - создание нового объекта
     */
    public Client() {
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}