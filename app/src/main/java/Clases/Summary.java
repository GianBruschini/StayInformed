package Clases;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Summary {

    @SerializedName("ID")
    public String iD;
    @SerializedName("Message")
    public String message;
    @SerializedName("Global")
    public Global global;
    @SerializedName("Countries")
    public List<CountryActual> countries;
    @SerializedName("Date")
    public Date date;


    public String getiD() {
        return iD;
    }

    public String getMessage() {
        return message;
    }

    public Global getGlobal() {
        return global;
    }

    public List<CountryActual> getCountries() {
        return countries;
    }

    public Date getDate() {
        return date;
    }
}
