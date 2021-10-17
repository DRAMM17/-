package model;

// Первая компонента/модуль — так называемая модель. Она содержит всю бизнес-логику приложения.
// Модель — самая независимая часть системы. Настолько независимая, что она не должна ничего знать о модулях
// Вид и Контроллер. Модель настолько независима, что ее разработчики могут практически ничего не знать о Виде и Контроллере.
// Основное предназначение Вида — предоставлять информацию из Модели в удобном для восприятия пользователя формате.
// Основное ограничение Вида — он никак не должен изменять модель.

// То где будут храниться данных модели, обработка данных производится, геттеры и сеттеры, класс данных, конструктор

import java.sql.Date;

/**
 * Класс-модель Водитель
 */
public class Driver {

    /** Поле ID Водителя */
    private int idDriver;

    /** Поле ID Автомобиля */
    private int idCar;

    /** Поле Имя */
    private String firstName;

    /** Поле Фамилия */
    private String surname;

    /** Поле Отчество */
    private String patronymic;

    /** Поле Прозвище */
    private String nickname;

    /** Поле Серия и номер водительского удостоверения */
    private String driversLicenseNumber;

    /** Поле Количество отработанных часов */
    private String hoursWorked;

    /** Поле Состояние */
    private String stateDriver;

    /** Поле Дата выдачи водительского удостоверения */
    private Date dateOfIssue;

    /** Поле На линии */
    private String onLine;

    /**
     * Конструктор - создание нового объекта
     */
    public Driver() {
    }

    /**
     * Конструктор - создание нового объекта с определёнными значениями
     * @param idDriver - ID Водителя
     * @param idCar - ID Автомобиля
     * @param firstName - Имя
     * @param surname - Фамилия
     * @param patronymic - Отчество
     * @param nickname - Прозвище
     * @param driversLicenseNumber - Серия и номер водительского удостоверения
     * @param hoursWorked - Количество отработанных часов
     * @param stateDriver - Состояние
     * @param dateOfIssue - Дата выдачи водительского удостоверения
     * @param onLine - На линии
     */
    public Driver(int idDriver, int idCar, String firstName, String surname, String patronymic, String nickname, String driversLicenseNumber, String hoursWorked, String stateDriver, Date dateOfIssue, String onLine) {
        this.idDriver = idDriver;
        this.idCar = idCar;
        this.firstName = firstName;
        this.surname = surname;
        this.patronymic = patronymic;
        this.nickname = nickname;
        this.driversLicenseNumber = driversLicenseNumber;
        this.hoursWorked = hoursWorked;
        this.stateDriver = stateDriver;
        this.dateOfIssue = dateOfIssue;
        this.onLine = onLine;
    }

    public int getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(int idDriver) {
        this.idDriver = idDriver;
    }

    public int getIdCar() {
        return idCar;
    }

    public void setIdCar(int idCar) {
        this.idCar = idCar;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    public void setDriversLicenseNumber(String driversLicenseNumber) {
        this.driversLicenseNumber = driversLicenseNumber;
    }

    public String getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(String hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(Date dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public String getStateDriver() {
        return stateDriver;
    }

    public void setStateDriver(String stateDriver) {
        this.stateDriver = stateDriver;
    }

    public String getOnLine() {
        return onLine;
    }

    public void setOnLine(String onLine) {
        this.onLine = onLine;
    }
}