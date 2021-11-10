package com.example.exchangeratesgraphs.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(uniqueConstraints =
        //other constraints
@UniqueConstraint(name = "UniqueWpis", columnNames = { "funkcja", "data", "fromSymbol", "toSymbol" }))
public class MyWeekValues {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String funkcja;
    private String fromSymbol;
    private String toSymbol;
    private LocalDate data;
    private String dataStr;
    private double open;
    private double high;
    private double low;
    private double close;




    public MyWeekValues() {
    }

    public String getFunkcja() {
        return funkcja;
    }

    public void setFunkcja(String description) {
        this.funkcja = description;
    }
}
