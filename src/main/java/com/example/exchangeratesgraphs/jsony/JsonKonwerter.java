package com.example.exchangeratesgraphs.jsony;


import com.example.exchangeratesgraphs.entity.MyChoice;
import com.example.exchangeratesgraphs.entity.MyCurrencies;
import com.example.exchangeratesgraphs.entity.MyWeekValues;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

@Service
public class JsonKonwerter {

    //--------------------------------------------------------------------------


    //--------------------------------------------------------------------------
    public static String getJSON(String url) {
        HttpsURLConnection con = null;
        try {
            URL u = new URL(url);
            con = (HttpsURLConnection) u.openConnection();

            con.connect();


            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();


        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
    //--------------------------------------------------------------------------
    public  static List<MyCurrencies> jsonToListCurrencies(String json){

        List<MyCurrencies> myCurrencyList = new ArrayList<>();

        //JSONArray obj;
        JSONObject obj;

        try{

            //obj = new JSONArray(json);
            obj = new JSONObject(json);
            //System.out.println(obj);
            Iterator<String> keys = obj.keys();

            while(keys.hasNext()) {
                String key = keys.next();
                if (obj.get(key) instanceof JSONObject) {
                    // do something with jsonObject here
                }
                // System.out.println("valuta: "+key+" nazwa:"+obj.get(key));
                myCurrencyList.add(new MyCurrencies(key, obj.get(key).toString()));

            }

        }catch (Exception e){
            return null;
        }
        return myCurrencyList;
    }
    //--------------------------------------------------------------------------
    public static Map<LocalDate,MyWeekValues> jsonWeeklyToMap(String json2, MyChoice myChoice){
        Map<LocalDate,MyWeekValues> mapa = new HashMap<>();
        //JSONArray obj2;
        JSONObject obj2;

        System.out.println("@@@@@@@");

        try {

            //obj = new JSONArray(json);
            obj2 = new JSONObject(json2);
           // System.out.println(obj2);
            Iterator<String> keys2 = obj2.keys();

            while (keys2.hasNext()) {
                String key = keys2.next();
                Object obj3 = obj2.get(key);



                if (obj3 instanceof JSONObject && key.equals("Time Series FX (Weekly)")) {

                     //System.out.println(obj3);
                    Iterator<String> it3 = ((JSONObject) obj3).keys();

                    while (it3.hasNext()) {
                        Object key3 = it3.next();

                       // String value3 = (String) ((JSONObject) obj3).get(key3.toString());
                       // System.out.println(key3 + " ======== " + value3);
                       // mapa.put(key3.toString(),value3);
                      //  System.out.println(key3);                       //data
                        MyWeekValues wpis = new MyWeekValues();
                        LocalDate datka = LocalDate.parse(key3.toString());
                        wpis.setData(datka);
                        wpis.setDataStr(key3.toString());
                        wpis.setFunkcja("Time Series FX (Weekly)");
                        wpis.setFromSymbol(myChoice.getFromSymbol());
                        wpis.setToSymbol(myChoice.getToSymbol());

                       JSONObject obj4 = (JSONObject) obj3;
                        Object value3 = obj4.get(String.valueOf(key3));
                        //System.out.println(value3); //dane dzienne
                        Iterator<String> it4 = ((JSONObject) value3).keys();
                        while (it4.hasNext()) {
                            // low open high close
                            Object key4 = it4.next();
                            //exchange rate
                            Object value4 = ((JSONObject) value3).get(String.valueOf(key4));
                           //  System.out.println(key4 + " ======== " + value4);
                            String s = key4.toString();
                            switch (s.substring(0,1)){
                                case "1":
                                    wpis.setOpen(Double.parseDouble(value4.toString()));
                                    break;
                                case "2":
                                    wpis.setHigh(Double.parseDouble(value4.toString()));
                                    break;
                                case "3":
                                    wpis.setLow(Double.parseDouble(value4.toString()));
                                    break;
                                case "4":
                                    wpis.setClose(Double.parseDouble(value4.toString()));
                                    break;


                            }

                        }
                        mapa.put(datka,wpis);
                    }


                }


            }
        }catch (JSONException e) {
            e.printStackTrace();
        }







        return mapa;
    }
    //--------------------------------------------------------------------------

}
