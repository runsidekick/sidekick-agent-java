package com.runsidekick.agent.logpoint;

import java.util.UUID;

public class Item {

    private Double doubleVal;
    private String strVal;
    private Boolean boolVal;
    private Item item;

    public Item() {
        doubleVal = Math.random() * 100;
        strVal = UUID.randomUUID().toString();
        boolVal = doubleVal.intValue() % 3 == 0;
    }

    @Override
    public String toString() {
        return "Item{" + "doubleVal=" + doubleVal + ", strVal='" + strVal + '\'' + ", boolVal=" + boolVal + '}';
    }

    public Double getDoubleVal() {
        return doubleVal;
    }

    public String getStrVal() {
        return strVal;
    }

    public Boolean getBoolVal() {
        return boolVal;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}