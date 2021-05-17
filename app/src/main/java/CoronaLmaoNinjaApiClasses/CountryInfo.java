package CoronaLmaoNinjaApiClasses;

import com.google.gson.annotations.SerializedName;

public class CountryInfo {

    public int _id;
    public String iso2;
    public String iso3;
    public int lat;
    @SerializedName("long")
    public int longitud;
    public String flag;

    public int get_id() {
        return _id;
    }

    public String getIso2() {
        return iso2;
    }

    public String getIso3() {
        return iso3;
    }

    public int getLat() {
        return lat;
    }

    public int getLongitud() {
        return longitud;
    }

    public String getFlag() {
        return flag;
    }
}
