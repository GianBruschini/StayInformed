package Interfaces;


import java.util.LinkedList;
import java.util.List;

import covid19apiClasses.ByCountry;
import covid19apiClasses.Countries;
import CoronaLmaoNinjaApiClasses.CountriesFlags;
import covid19apiClasses.Summary;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface CovidApi {


    @GET("countries")
    Call<LinkedList<Countries>> getCountries();

    @GET
    Call<List<ByCountry>> getByCountry(@Url String url);

    @GET
    Call<Summary> getSummary(@Url String url);

    @GET

    Call <CountriesFlags> getCountriesFlags(@Url String url);













}
