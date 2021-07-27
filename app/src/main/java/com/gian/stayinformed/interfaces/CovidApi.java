package com.gian.stayinformed.interfaces;


import com.gian.stayinformed.model.ByCountry;
import com.gian.stayinformed.model.Countries;
import com.gian.stayinformed.model.Summary;
import com.gian.stayinformed.model.coronaninjaapiclasses.CountriesFlags;

import java.util.LinkedList;
import java.util.List;


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
