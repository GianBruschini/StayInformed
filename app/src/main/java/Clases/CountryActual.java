package Clases;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CountryActual {

    @SerializedName("ID")
    public String iD;
    @SerializedName("Country")
    public String country;
    @SerializedName("CountryCode")
    public String countryCode;
    @SerializedName("Slug")
    public String slug;
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
    @SerializedName("Premium")
    public Premium premium;


    public String getiD() {
        return iD;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getSlug() {
        return slug;
    }

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

    public Premium getPremium() {
        return premium;
    }
}
