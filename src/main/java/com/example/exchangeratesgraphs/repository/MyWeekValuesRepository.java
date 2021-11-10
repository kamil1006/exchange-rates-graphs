package com.example.exchangeratesgraphs.repository;

import com.example.exchangeratesgraphs.entity.MyWeekValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyWeekValuesRepository extends JpaRepository<MyWeekValues, Long> {

    //findAllByFunkcjaAndFromSymbolAndToSymbolOrderByDataAsc
    List<MyWeekValues> findAllByFunkcjaAndFromSymbolAndToSymbolOrderByDataAsc(String funkcja, String fromSymbol, String toSymbol);
}
