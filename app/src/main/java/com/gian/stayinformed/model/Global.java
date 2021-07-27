package com.gian.stayinformed.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Global {

    @SerializedName("NewConfirmed")
    public int newConfirmed;
    @SerializedName("TotalConfirmed")
    public int totalConfirmed;
    @SerializedName("NewDeaths")
    public int newDeaths;
    @SerializedName("TotalDeaths")
    public int totalDeaths;
    @SerializedName("NewRecovered")
    public int newRecovered;
    @SerializedName("TotalRecovered")
    public int totalRecovered;
    @SerializedName("Date")
    public Date date;

    public int getNewConfirmed() {
        return newConfirmed;
    }

    public int getTotalConfirmed() {
        return totalConfirmed;
    }

    public int getNewDeaths() {
        return newDeaths;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public int getNewRecovered() {
        return newRecovered;
    }

    public int getTotalRecovered() {
        return totalRecovered;
    }

    public Date getDate() {
        return date;
    }
}
