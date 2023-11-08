package com.example.monteCarlo;

import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TableValue
{
    private SimpleStringProperty field;
    private BigDecimal result;

    public  TableValue(double result){
        this.result = new BigDecimal(result);
        this.result = this.result.setScale(4, RoundingMode.DOWN);
    }

    public TableValue(String field)
    {
        this.field = new SimpleStringProperty(field);
    }

    public BigDecimal getResult(){ return result;}
    public  void setResult(BigDecimal result){ this.result = result;}


    public String getField(){ return field.get();}
    public void setField(String value){ field.set(value);}

}
