
package site.nomoreparties.stellarburger.order.get;

import site.nomoreparties.stellarburger.order.get.Order;

import java.util.List;

public class Orders {


    private List<Order> orders;

    private boolean success;

    private Long total;

    private Long totalToday;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTotalToday() {
        return totalToday;
    }

    public void setTotalToday(Long totalToday) {
        this.totalToday = totalToday;
    }

}
