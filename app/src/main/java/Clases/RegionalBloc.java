package Clases;

import java.util.List;

public class RegionalBloc {

    public String acronym;
    public String name;
    public List<Object> otherAcronyms;
    public List<String> otherNames;

    public String getAcronym() {
        return acronym;
    }

    public String getName() {
        return name;
    }

    public List<Object> getOtherAcronyms() {
        return otherAcronyms;
    }

    public List<String> getOtherNames() {
        return otherNames;
    }
}
