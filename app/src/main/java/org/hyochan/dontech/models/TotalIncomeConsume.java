package org.hyochan.dontech.models;

/**
 * Created by hyochan on 2016. 10. 13..
 */

public class TotalIncomeConsume {
    int total, income, consume;

    public TotalIncomeConsume(int total, int income, int consume) {
        this.total = total;
        this.income = income;
        this.consume = consume;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }
}
