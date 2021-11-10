package com.example.exchangeratesgraphs.jsony;

import com.example.exchangeratesgraphs.entity.MyChoice;
import com.example.exchangeratesgraphs.entity.MyCurrencies;
import com.example.exchangeratesgraphs.entity.MyWeekValues;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class WeeklyRatesGetter {

    String url;

    @Autowired
    JsonKonwerter jsonKonwerter;

    List<MyCurrencies> myCurrencyList;
    List<String> symbols;
    Map<LocalDate, MyWeekValues> resultMap;
    String valueFrom;
    String valueTo;

    public WeeklyRatesGetter(MyChoice myChoice) {

        myCurrencyList = new ArrayList<>();
        symbols = new ArrayList<>();
        
        valueFrom = myChoice.getFromSymbol();
        valueTo = myChoice.getToSymbol();
        resultMap = new HashMap<>();

        url = "https://www.alphavantage.co/query?function=FX_WEEKLY&from_symbol=EUR&to_symbol=USD&apikey=demo";
        //String url2="https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=USD&to_currency=JPY&apikey=demo";
        //instead of demo you should use your own apikey
        String url2="https://www.alphavantage.co/query?function=FX_WEEKLY&from_symbol="+valueFrom+"&to_symbol="+valueTo+"&apikey=demo";


        String json2 = jsonKonwerter.getJSON(url2);
        System.out.println(url2);
        //System.out.println(json2);

        resultMap = jsonKonwerter.jsonWeeklyToMap(json2,myChoice);
        //resultMap = jsonKonwerter.jsonWeeklyToMap(json2);
        //jsonKonwerter.jsonWeeklyToMap(json2);



    }
}
