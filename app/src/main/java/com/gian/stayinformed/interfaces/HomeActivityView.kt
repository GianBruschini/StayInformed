package com.gian.stayinformed.interfaces

import android.content.Intent
import com.gian.stayinformed.model.ByCountry
import com.gian.stayinformed.model.Summary

import retrofit2.Response

interface HomeActivityView {
    fun showProgressDialog()
    fun hideProgressDialog()
    fun showListOfCountriesSpinner(spinnerArrayListCountries: MutableList<String>)
    fun showResponseFailed()
    fun showCountrySelectedData(countrySelected: ByCountry?)
    fun showNewCountryData(newData: Response<Summary>, position: Int)
    fun notifyCountryAddedFav()
    fun notifyCountryExistFav()
    fun shareCountryData(myIntent: Intent)


}