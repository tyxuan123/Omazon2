package com.example.omazonproject;

/**
 * This class is a blueprint for all cart item, which will be used in the purchase page
 *
 * @author XiangLun
 */
public class OrderedItem {
    private String productName;
    private String orderedImagePath;
    private int quantity;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderedImagePath() {
        return orderedImagePath;
    }

    public void setOrderedImagePath(String orderedImagePath) {
        this.orderedImagePath = orderedImagePath;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}