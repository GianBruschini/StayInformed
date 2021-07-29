package com.gian.stayinformed.presenter

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.gian.stayinformed.interfaces.HomeActivityView
import com.gian.stayinformed.model.ByCountry
import com.gian.stayinformed.model.HomeActivityInteractor
import com.gian.stayinformed.model.Summary
import com.gian.stayinformed.view.HomeActivity

import retrofit2.Response

class HomeActivityPresenter(var homeActivityView: HomeActivityView,
                            var homeActivityInteractor: HomeActivityInteractor): HomeActivityInteractor.onHomeActivityListener {
    fun getCountryDataForSpinner() {
        homeActivityInteractor.fillCountriesSpinner(this)
    }

    override fun onShowProgressDialog() {
        homeActivityView.showProgressDialog()
    }

    override fun onHideProgressDialog() {
        homeActivityView.hideProgressDialog()
    }

    override fun onSetSpinnerListCountries(spinnerArrayListCountries: MutableList<String>) {
        homeActivityView.showListOfCountriesSpinner(spinnerArrayListCountries)
    }

    override fun onFailureResponse() {
        homeActivityView.showResponseFailed()
    }

    override fun onPassCountryData(countrySelected: ByCountry?) {
        homeActivityView.showCountrySelectedData(countrySelected)
    }

    override fun onPassNewCountryData(newData: Response<Summary>, position: Int) {
        homeActivityView.showNewCountryData(newData,position)
    }

    override fun onCountryExistFav() {
        homeActivityView.notifyCountryExistFav()
    }

    override fun onCountryAddedToFav() {
        homeActivityView.notifyCountryAddedFav()
    }

    override fun onShareCountryData(myIntent: Intent) {
        homeActivityView.shareCountryData(myIntent)
    }

    override fun onShowInterstatial() {
        homeActivityView.showInterstatialAd()
    }

    override fun onSetDismissShareTo0() {
        homeActivityView.setDismisshShareTo0()
    }

    override fun onNotInternetConnection() {
        homeActivityView.showDialogNotInternetConnection()
    }

    fun getCountryClickedInfo(country: String) {
        homeActivityInteractor.getInfoAbout(country)
    }

    fun getNewCountryClickedInfo(country: String) {
        homeActivityInteractor.getNewCountryInfo(country)

    }

    fun getFavouritesData() {
        homeActivityInteractor.getFavouriteDataFromDB()
    }

    fun checkSharedPreference(context:HomeActivity) {
        homeActivityInteractor.checkSharedPreference(context)
    }

    fun addCountryToFavouriteList(country: String, actives: String,
                                  confirmed: String, death: String,
                                  newDeath: String, newCases: String) {
        homeActivityInteractor.addCountryToFavouriteDB(country,actives,confirmed,
                death,newDeath,newCases)
    }

    fun initializeRecyclerView(recyclerBuscarPaises: RecyclerView) {
        homeActivityInteractor.initialize(recyclerBuscarPaises)
    }

    fun shareCountryOnSocialMedia(country: String, actives: String,
                                  confirmed: String, death: String,
                                  newDeath: String, newCases: String) {
        homeActivityInteractor.shareCountryOnSocialMedia(country,actives,confirmed,
                death,newDeath,newCases)

    }

    fun checkIfShareDismiss(dismissShare: Int,country:String,
                            confirmed: String,
                            active: String,
                            death: String,
                            newCases: String,
                            newDeath: String) {
        homeActivityInteractor.checkIfShareDismiss(dismissShare,country,
                confirmed,active,death,newCases,newDeath)
    }


}