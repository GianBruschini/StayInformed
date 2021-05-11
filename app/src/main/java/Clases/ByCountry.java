package Clases;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ByCountry {
    @SerializedName("Country")
    public String country;
    @SerializedName("CountryCode")
    public String countryCode;
    @SerializedName("Lat")
    public String lat;
    @SerializedName("Lon")
    public String lon;
    @SerializedName("Confirmed")
    public int confirmed;
    @SerializedName("Deaths")
    public int deaths;
    @SerializedName("Recovered")
    public int recovered;
    @SerializedName("Active")
    public int active;
    @SerializedName("Date")
    public Date date;
    @SerializedName("LocationID")
    public String locationID;


    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getRecovered() {
        return recovered;
    }

    public int getActive() {
        return active;
    }

    public Date getDate() {
        return date;
    }

    public String getLocationID() {
        return locationID;
    }
}
