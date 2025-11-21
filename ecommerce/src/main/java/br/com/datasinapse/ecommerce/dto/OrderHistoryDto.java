package br.com.datasinapse.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderHistoryDto {

    private Long order_id;
    private String product_name;
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime purchase_date;

    private String customer_username;



    public Long getOrder_id() {
        return order_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getPurchase_date() {
        return purchase_date;
    }

    public String getCustomer_username() {
        return customer_username;
    }



    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setPurchase_date(LocalDateTime purchase_date) {
        this.purchase_date = purchase_date;
    }

    public void setCustomer_username(String customer_username) {
        this.customer_username = customer_username;
    }
}