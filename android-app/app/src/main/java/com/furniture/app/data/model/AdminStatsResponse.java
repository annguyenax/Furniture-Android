package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public class AdminStatsResponse {

    @SerializedName("revenueData") private List<DataPoint> revenueData;
    @SerializedName("topProducts") private List<DataPoint> topProducts;
    @SerializedName("byCategory")  private List<DataPoint> byCategory;
    @SerializedName("totalRevenue") private BigDecimal totalRevenue;
    @SerializedName("totalOrders")  private long totalOrders;

    public List<DataPoint> getRevenueData() { return revenueData; }
    public List<DataPoint> getTopProducts() { return topProducts; }
    public List<DataPoint> getByCategory()  { return byCategory; }
    public BigDecimal getTotalRevenue()     { return totalRevenue; }
    public long getTotalOrders()            { return totalOrders; }

    public static class DataPoint {
        @SerializedName("label") private String label;
        @SerializedName("value") private BigDecimal value;
        public String getLabel()    { return label; }
        public BigDecimal getValue(){ return value; }
    }
}
