package com.example.syntaxanalysis;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TableFrequency
{
    private BigDecimal frequency;
    private SimpleStringProperty field;

    public TableFrequency(double frequency)
    {
        this.frequency = new BigDecimal(frequency);
        this.frequency = this.frequency.setScale(2, RoundingMode.DOWN);
    }

    public TableFrequency(String field)
    {
        this.field = new SimpleStringProperty(field);
    }

    public BigDecimal getFrequency(){ return frequency;}
    public void set(BigDecimal value){ frequency = value;}

    public String getField(){ return field.get();}
    public void setField(String value){ field.set(value);}

}
