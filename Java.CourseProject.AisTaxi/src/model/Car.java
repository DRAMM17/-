package model;

/**
 * Класс-модель Автомобиль
 */
public class Car {

    /** Поле ID Автомобиля */
    private int idCar;

    /** Поле ID Водителя */
    private int idDriver;

    /** Поле Название автомобиля */
    private String mark;

    /** Поле Цвет */
    private String color;

    /** Поле Статус Автомобиля */
    private String stateCar;

    /** Поле с */
    private String governmentNumber;

    /** Поле Год выпуска */
    private String yearOfIssue;

    /** Поле Номер свидетельства о регистрации */
    private String vehicleRegCertificateNumber;


    /**
     * Конструктор - создание нового объекта
     */
    public Car() {
    }

    /**
     * Конструктор - создание нового объекта с определёнными значениями
     * @param idCar - ID Автомобиля
     * @param idDriver - ID Водителя
     * @param mark - Название автомобиля
     * @param color - Цвет
     * @param stateCar - Статус Автомобиля
     * @param governmentNumber - Название автомобиля
     * @param yearOfIssue - Год выпуска
     * @param vehicleRegCertificateNumber - Номер свидетельства о регистрации
     */
    public Car(int idCar, int idDriver, String mark, String color, String stateCar, String governmentNumber, String yearOfIssue, String vehicleRegCertificateNumber) {
        this.idCar = idCar;
        this.idDriver = idDriver;
        this.mark = mark;
        this.color = color;
        this.stateCar = stateCar;
        this.governmentNumber = governmentNumber;
        this.yearOfIssue = yearOfIssue;
        this.vehicleRegCertificateNumber = vehicleRegCertificateNumber;
    }

    public int getIdCar() {
        return idCar;
    }

    public void setIdCar(int idCar) {
        this.idCar = idCar;
    }

    public int getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(int idDriver) {
        this.idDriver = idDriver;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGovernmentNumber() {
        return governmentNumber;
    }

    public void setGovernmentNumber(String governmentNumber) {
        this.governmentNumber = governmentNumber;
    }

    public String getYearOfIssue() {
        return yearOfIssue;
    }

    public void setYearOfIssue(String yearOfIssue) {
        this.yearOfIssue = yearOfIssue;
    }

    public String getVehicleRegCertificateNumber() {
        return vehicleRegCertificateNumber;
    }

    public void setVehicleRegCertificateNumber(String vehicleRegCertificateNumber) {
        this.vehicleRegCertificateNumber = vehicleRegCertificateNumber;
    }

    public String getStateCar() {
        return stateCar;
    }

    public void setStateCar(String stateCar) {
        this.stateCar = stateCar;
    }
}
