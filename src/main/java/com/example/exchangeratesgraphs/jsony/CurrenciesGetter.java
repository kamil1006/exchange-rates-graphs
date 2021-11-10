package com.example.exchangeratesgraphs.jsony;

import com.example.exchangeratesgraphs.entity.MyCurrencies;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class CurrenciesGetter {

    String url;

    @Autowired
    JsonKonwerter jsonKonwerter;

    List<MyCurrencies> myCurrencyList;
    List<String> symbols;
    Map<String,String> resultMap;

    public CurrenciesGetter() {

        myCurrencyList = new ArrayList<>();
        symbols = new ArrayList<>();


        url = "https://openexchangerates.org/api/currencies.json";

        String json = jsonKonwerter.getJSON(url);

        myCurrencyList = jsonKonwerter.jsonToListCurrencies(json);

        //myCurrencyList = jsonKonwerter.jsonToListCurrencies(json);
        //------------------------------------------------------------------------
        myCurrencyList.stream().forEach(myCurrency -> {
            symbols.add(myCurrency.getSymbol());
        });

        Collections.sort(myCurrencyList,(s1, s2) -> {
            if(s1.getName().compareTo(s2.getName())>1){
                return 1;
            }
            else return -1;
        });

        Collections.sort(symbols);
        //------------------------------------------------------------------------

    }
}
