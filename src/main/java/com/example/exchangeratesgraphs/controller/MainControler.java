package com.example.exchangeratesgraphs.controller;

import com.example.exchangeratesgraphs.entity.MyChoice;
import com.example.exchangeratesgraphs.entity.MyCurrencies;
import com.example.exchangeratesgraphs.entity.MyWeekValues;
import com.example.exchangeratesgraphs.jsony.CurrenciesGetter;
import com.example.exchangeratesgraphs.jsony.WeeklyRatesGetter;
import com.example.exchangeratesgraphs.repository.MyWeekValuesRepository;
import com.example.exchangeratesgraphs.stats.StatsCalc;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MainControler {

    CurrenciesGetter currenciesGetter;
    WeeklyRatesGetter weeklyRatesGetter;


    MyWeekValuesRepository myWeekValuesRepository;

    //@Autowired
    StatsCalc statsCalc;


    String[][] tablicaWynikow;
    String[][] tablicaWynikowZTrendem;
    Map<LocalDate, MyWeekValues> resultMap;
    List<MyWeekValues> resultList;

    //StatsCalc statsCalc;
    //--------------------------------------------------------------------
    @Autowired
    public MainControler( MyWeekValuesRepository myWeekValuesRepository) {
        currenciesGetter = new CurrenciesGetter();
        this.myWeekValuesRepository = myWeekValuesRepository;

    }
    //--------------------------------------------------------------------
    @GetMapping(path = "")
    public String home(Model model) {
        //return "index";

        model.addAttribute("currencies_symbols", currenciesGetter.getSymbols());
        model.addAttribute("currencies", currenciesGetter.getMyCurrencyList());
        model.addAttribute("wybor",new MyChoice());



        return "index.html";
    }
    //--------------------------------------------------------------------
    @PostMapping(path = "/get_weekly_data")
    public String gettingWeeklyData(Model model, @ModelAttribute("wybor") MyChoice myChoice) {
        //return "index";
       // System.out.println(myChoice);

       // System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
      //  System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

       // myChoice.setFromSymbol("EUR");
       // myChoice.setToSymbol("PLN");

        if(myChoice.getFromSymbol().equals(myChoice.getToSymbol())){
            model.addAttribute("currencies_symbols", currenciesGetter.getSymbols());
            model.addAttribute("currencies", currenciesGetter.getMyCurrencyList());
            model.addAttribute("wybor",new MyChoice());



            return "index.html";

        }
        else {

            if (!myChoice.getFromSymbol().equals(myChoice.getToSymbol()))
                weeklyRatesGetter = new WeeklyRatesGetter(myChoice);

            resultMap = weeklyRatesGetter.getResultMap();
            resultList = new ArrayList<>();
            //System.out.println(resultMap);
            for (Map.Entry<LocalDate, MyWeekValues> entry : resultMap.entrySet()) {
                //System.out.println(entry.getKey() + ":" + entry.getValue());
                try {
                    myWeekValuesRepository.save(entry.getValue());
                    resultList.add(entry.getValue());
                } catch (Exception e) {
                    System.out.println("****************** juz bylo ********************************");
                }

            }
            statsCalc = new StatsCalc(myChoice, myWeekValuesRepository);
            //statsCalc.StatsCalcMake(myChoice, myWeekValuesRepository);

            tablicaWynikow = statsCalc.getTablicaWynikow();
            tablicaWynikowZTrendem = statsCalc.getTablicaWynikowZTrendem();

            for (int i = 0; i <= statsCalc.getIloscWpisow(); i++) {
                // System.out.println(tablicaWynikow[i][0]);
                //System.out.println(tablicaWynikow[i][1]);


            }

            model.addAttribute("max_date", statsCalc.getMyWeekValuesMax());
            model.addAttribute("daty", statsCalc.getListaWszystkichDat());
            model.addAttribute("wybor", myChoice);

            return "graph.html";
        }

    }
    //--------------------------------------------------------------------
    @RequestMapping(path="/get_initial_data", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getInitialData(@RequestBody com.fasterxml.jackson.databind.JsonNode inf, Model model) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type","application/json");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        String odData = inf.get("data_od").asText();
        String doData = inf.get("data_do").asText();
        System.out.println(odData);
        System.out.println(doData);
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        Gson gson = new Gson();
        String kodJson ="";
       // kodJson = gson.toJson(tablicaWynikow);
        kodJson = gson.toJson(tablicaWynikowZTrendem);
        System.out.println(kodJson);
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(kodJson);

    }
        //--------------------------------------------------------------------
        @RequestMapping(path="/get_range_data", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<String> getRangeData(@RequestBody com.fasterxml.jackson.databind.JsonNode inf, Model model) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type","application/json");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            String odData = inf.get("data_od").asText();
            String doData = inf.get("data_do").asText();
            System.out.println(odData);
            System.out.println(doData);
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            Gson gson = new Gson();
            String kodJson ="";
            // kodJson = gson.toJson(tablicaWynikow);
            String[][] strings = statsCalc.calculateTrendFromTo(statsCalc.getTablicaWynikow(), odData, doData);

            //kodJson = gson.toJson(tablicaWynikowZTrendem);
            kodJson = gson.toJson(strings);
            System.out.println(kodJson);
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(kodJson);

        }
    //--------------------------------------------------------------------

}
