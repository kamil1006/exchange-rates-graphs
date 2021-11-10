package com.example.exchangeratesgraphs.stats;

import com.example.exchangeratesgraphs.entity.MyChoice;
import com.example.exchangeratesgraphs.entity.MyWeekValues;
import com.example.exchangeratesgraphs.repository.MyWeekValuesRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Data
public class StatsCalc {

    //@Autowired


    List<MyWeekValues> result;
    MyWeekValues myWeekValuesMin;
    MyWeekValues myWeekValuesMax;
    String[][] tablicaWynikow;
    String[][] tablicaWynikowZTrendem;

    double a;
    double b;

    double a2;
    double b2;


    int rows;
    int columns;

    int iloscWpisow;


    String[] tablicaWszystkichDat;
    List<String> listaWszystkichDat;


    public StatsCalc(MyChoice myChoice,  MyWeekValuesRepository repository) {

        //wpis.setFunkcja("Time Series FX (Weekly)");
        String fukcja ="Time Series FX (Weekly)";
        System.out.println(fukcja);
        System.out.println(myChoice.getFromSymbol());
        System.out.println(myChoice.getToSymbol());
      //  result = repository.findAll();

        result = repository.findAllByFunkcjaAndFromSymbolAndToSymbolOrderByDataAsc(fukcja, myChoice.getFromSymbol(), myChoice.getToSymbol());
        myWeekValuesMin = result.stream().min(Comparator.comparing(MyWeekValues::getData)).orElseThrow(NoSuchElementException::new);
        myWeekValuesMax = result.stream().max(Comparator.comparing(MyWeekValues::getData)).orElseThrow(NoSuchElementException::new);
        iloscWpisow = result.size();
        listaWszystkichDat = new ArrayList<>();
        int kolumny = 2;
        columns = kolumny;
        rows = iloscWpisow;

        tablicaWynikow = new String[iloscWpisow+1][kolumny];
        tablicaWszystkichDat = new String[iloscWpisow];
        int i = 0;
        tablicaWynikow[i][0] = "data";
        tablicaWynikow[i++][1] = "close rate";
        for(MyWeekValues v:result ){

            tablicaWszystkichDat[i-1] = v.getData().toString();
            listaWszystkichDat.add(v.getData().toString());

            tablicaWynikow[i][0] = v.getData().toString();
            tablicaWynikow[i++][1] = String.valueOf(v.getClose());


        }
        tablicaWynikowZTrendem = this.calculateTrend(tablicaWynikow);


    }
    //-------------------------------------------------------------------
    public String[][] calculateTrend(String[][] tablicaWejsciowa) {

        double sumT = 0;
        double avgT = 0;
        double sumY = 0;
        double avgY = 0;
        for(int i = 1; i<=rows;i++){
            sumT+=i;
            sumY+=Double.parseDouble(tablicaWejsciowa[i][1]);
        }
        avgT = sumT/((double) rows);
        avgY = sumY/((double) rows);

        String[][] tablicaObliczen = new String[rows+1][3];
        columns = 3;

        double sum_of_Ti_Tsr_2 = 0;
        double sum_of_Ti_Tsr_mult_Yi_Ysr = 0;
        for(int i = 1; i<=rows;i++){
            double Ti_Tsr = i  - avgT;
            double Yi_Ysr = Double.parseDouble(tablicaWejsciowa[i][1])-avgY;
            double Ti_Tsr_2 = Ti_Tsr * Ti_Tsr;
            double Ti_Tsr_mult_Yi_Ysr =Ti_Tsr * Yi_Ysr;
            sum_of_Ti_Tsr_2+=Ti_Tsr_2;
            sum_of_Ti_Tsr_mult_Yi_Ysr+=Ti_Tsr_mult_Yi_Ysr;

        }
        a = sum_of_Ti_Tsr_mult_Yi_Ysr / sum_of_Ti_Tsr_2;
        b = avgY - (a*avgT);



        tablicaObliczen[0][0] = tablicaWejsciowa[0][0];
        tablicaObliczen[0][1] = tablicaWejsciowa[0][1];
        tablicaObliczen[0][2] = "close rate trend function";

        for(int i = 1; i<=rows;i++){

            tablicaObliczen[i][0] = tablicaWejsciowa[i][0];
            tablicaObliczen[i][1] = tablicaWejsciowa[i][1];
            tablicaObliczen[i][2] = String.valueOf(b + a * (i));
        }

        String dataOd = myWeekValuesMin.getDataStr();
        String dataDo = myWeekValuesMax.getDataStr();

        String[][] tablicaObliczenOut = new String[rows+1+4][3];
        tablicaObliczenOut[0][0] = "a";
        tablicaObliczenOut[0][1] = String.valueOf(Math.round(a*10000.0)/10000.0);
        tablicaObliczenOut[1][0] = "b";
        tablicaObliczenOut[1][1] = String.valueOf(Math.round(b*10000.0)/10000.0);
        tablicaObliczenOut[2][0] = "dataOd";
        tablicaObliczenOut[2][1] = dataOd;
        tablicaObliczenOut[3][0] = "dataDo";
        tablicaObliczenOut[3][1] = dataDo;

        tablicaObliczenOut[4][0] = tablicaWejsciowa[0][0];
        tablicaObliczenOut[4][1] = tablicaWejsciowa[0][1];
        tablicaObliczenOut[4][2] = "close rate trend function";

        for(int i = 1; i<=rows;i++){

            tablicaObliczenOut[i+4][0] = tablicaWejsciowa[i][0];
            tablicaObliczenOut[i+4][1] = tablicaWejsciowa[i][1];
            tablicaObliczenOut[i+4][2] = String.valueOf(b + a * (i));
        }


        return tablicaObliczenOut;
                //return  tablicaObliczen;

    }
    //-------------------------------------------------------------------
    public String[][] calculateTrendFromTo(String[][] tablicaWejsciowaOrg, String dataOd, String dataDo) {

        int startRow = 0;
        int endRow = 0;

        for(int i = 1; i<=rows;i++){
            String s = tablicaWejsciowaOrg[i][0];
            if (s.equals(dataOd)) startRow = i;
            if( s.equals(dataDo)) endRow = i;

        }
        int newRowCount = endRow-startRow+2;
        String[][] tablicaWejsciowa = new String[newRowCount][2];
        tablicaWejsciowa[0][0]=tablicaWejsciowaOrg[0][0];
        tablicaWejsciowa[0][1]=tablicaWejsciowaOrg[0][1];
        int iteration = 1;
        for(int i = startRow; i<=endRow;i++){
            tablicaWejsciowa[iteration][0]=tablicaWejsciowaOrg[i][0];
            tablicaWejsciowa[iteration++][1]=tablicaWejsciowaOrg[i][1];

        }
        //******************

        double sumT = 0;
        double avgT = 0;
        double sumY = 0;
        double avgY = 0;

        int rows2 = newRowCount-1;

        for(int i = 1; i<=rows2;i++){
            sumT+=i;
            sumY+=Double.parseDouble(tablicaWejsciowa[i][1]);
        }
        avgT = sumT/((double) rows2);
        avgY = sumY/((double) rows2);

        String[][] tablicaObliczen = new String[rows2+1][3];
        columns = 3;

        double sum_of_Ti_Tsr_2 = 0;
        double sum_of_Ti_Tsr_mult_Yi_Ysr = 0;
        for(int i = 1; i<=rows2;i++){
            double Ti_Tsr = i  - avgT;
            double Yi_Ysr = Double.parseDouble(tablicaWejsciowa[i][1])-avgY;
            double Ti_Tsr_2 = Ti_Tsr * Ti_Tsr;
            double Ti_Tsr_mult_Yi_Ysr =Ti_Tsr * Yi_Ysr;
            sum_of_Ti_Tsr_2+=Ti_Tsr_2;
            sum_of_Ti_Tsr_mult_Yi_Ysr+=Ti_Tsr_mult_Yi_Ysr;

        }
        a2 = sum_of_Ti_Tsr_mult_Yi_Ysr / sum_of_Ti_Tsr_2;
        b2 = avgY - (a2*avgT);



        tablicaObliczen[0][0] = tablicaWejsciowa[0][0];
        tablicaObliczen[0][1] = tablicaWejsciowa[0][1];
        tablicaObliczen[0][2] = "close rate trend function";

        for(int i = 1; i<=rows2;i++){

            tablicaObliczen[i][0] = tablicaWejsciowa[i][0];
            tablicaObliczen[i][1] = tablicaWejsciowa[i][1];
            tablicaObliczen[i][2] = String.valueOf(b2 + a2 * (i));
        }

        //String dataOd = myWeekValuesMin.getDataStr();
        //String dataDo = myWeekValuesMax.getDataStr();

        String[][] tablicaObliczenOut = new String[rows2+1+4][3];
        tablicaObliczenOut[0][0] = "a";
        tablicaObliczenOut[0][1] = String.valueOf(Math.round(a2*10000.0)/10000.0);
        tablicaObliczenOut[1][0] = "b";
        tablicaObliczenOut[1][1] = String.valueOf(Math.round(b2*10000.0)/10000.0);
        tablicaObliczenOut[2][0] = "dataOd";
        tablicaObliczenOut[2][1] = dataOd;
        tablicaObliczenOut[3][0] = "dataDo";
        tablicaObliczenOut[3][1] = dataDo;

        tablicaObliczenOut[4][0] = tablicaWejsciowa[0][0];
        tablicaObliczenOut[4][1] = tablicaWejsciowa[0][1];
        tablicaObliczenOut[4][2] = "close rate trend function";

        for(int i = 1; i<=rows2;i++){

            tablicaObliczenOut[i+4][0] = tablicaWejsciowa[i][0];
            tablicaObliczenOut[i+4][1] = tablicaWejsciowa[i][1];
            tablicaObliczenOut[i+4][2] = String.valueOf(b2 + a2 * (i));
        }


        return tablicaObliczenOut;
        //return  tablicaObliczen;

        //********************

    }


    //-------------------------------------------------------------------
}
