package com.example.lab5;

public class CurrencyRate {

    private final String code;
    private final double rate;

    public CurrencyRate(String code, double rate) {
        System.out.println("CurrencyRate constructor called: " + code + " " + rate);
        this.code = code;
        this.rate = rate;
    }

    public String getCode() {
        System.out.println("getCode() called");
        return code;
    }

    public double getRate() {
        System.out.println("getRate() called");
        return rate;
    }

    @Override
    public String toString() {
        System.out.println("toString() called");
        return code + " - " + rate;
    }
}