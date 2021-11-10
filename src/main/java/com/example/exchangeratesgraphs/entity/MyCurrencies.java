package com.example.exchangeratesgraphs.entity;

import lombok.Data;

@Data
public class MyCurrencies {
    private String symbol;
    private String name;

    public MyCurrencies(String symbol, String country) {
        this.symbol = symbol;
        this.name = country;
    }
}
