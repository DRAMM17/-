package model;

import java.sql.Date;

/**
 * Класс заказа со свойствами <b>idOrder</b>/
 */
public class Order {
    //** Поле ID заказа  */
    private int idOrder; // ID заказа
    private int idDriver; // В лог. модели данных удалить "Водитель, выполняющий заказ"
    private int idClient;
    private String startTime; // Время начала выполнения заказа
    private Date startDate; // Дата начала выполнения заказа
    private String orderStatus;
    private String startAddress; // Начальный адрес
    private String endAddress; // Конечный адрес

    public Order(int idOrder, int idDriver, int idClient, String startTime, Date startDate, String orderStatus, String startAddress, String endAddress) {
        this.idOrder = idOrder;
        this.idDriver = idDriver;
        this.idClient = idClient;
        this.startTime = startTime;
        this.startDate = startDate;
        this.orderStatus = orderStatus;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }

    public Order() {
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public int getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(int idDriver) {
        this.idDriver = idDriver;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void getOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }
}
