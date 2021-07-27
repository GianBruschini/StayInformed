package com.gian.stayinformed.view


import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gian.stayinformed.R
import com.gian.stayinformed.classes.CommonUtils
import com.gian.stayinformed.databinding.ActivityHomeBinding
import com.gian.stayinformed.interfaces.HomeActivityView
import com.gian.stayinformed.model.ByCountry
import com.gian.stayinformed.model.HomeActivityInteractor
import com.gian.stayinformed.model.Summary
import com.gian.stayinformed.model.coronaninjaapiclasses.CustomSpinnerAdapter
import com.gian.stayinformed.presenter.HomeActivityPresenter

import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Response

class HomeActivity : AppCompatActivity(), HomeActivityView,View.OnClickListener {
    private lateinit var binding: ActivityHomeBinding
    private val presenter= HomeActivityPresenter(this, HomeActivityInteractor())
    private var loadingDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        presenter.initializeRecyclerView(binding.recyclerBuscarPaises)
        presenter.checkSharedPreference(this)
        presenter.getFavouritesData()
        presenter.getCountryDataForSpinner()
        setLogicToCountrySpinner()


    }

    private fun setLogicToCountrySpinner() {
        binding.buscarPaisesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.getCountryClickedInfo(binding.buscarPaisesSpinner.getItemAtPosition(position).toString())
                presenter.getNewCountryClickedInfo(binding.buscarPaisesSpinner.getItemAtPosition(position).toString())
            }

        }
    }

    private fun initializeViews() {
        binding.card.setBackgroundResource(R.drawable.card_location)
        binding.agregarAfavoritos.setOnClickListener(this)
        binding.imageShare.setOnClickListener(this)
    }

    override fun showProgressDialog() {
        hideProgressDialog()
        loadingDialog = CommonUtils.showLoadingDialog(this)
    }

    override fun hideProgressDialog() {
        loadingDialog?.let {
            if(it.isShowing)it.cancel()

        }
    }

    override fun showListOfCountriesSpinner(spinnerArrayListCountries: MutableList<String>) {
        val customSpinnerAdapter = CustomSpinnerAdapter(this@HomeActivity, android.R.layout.simple_spinner_item, spinnerArrayListCountries)
        binding.buscarPaisesSpinner.adapter = customSpinnerAdapter
    }

    override fun showResponseFailed() {
        Toast.makeText(this,"Failed, try again later",Toast.LENGTH_SHORT).show()
    }

    override fun showCountrySelectedData(countrySelected: ByCountry?) {
        if(countrySelected != null){
            binding.casosActivosText.text = countrySelected?.getActive().toString()
            binding.casosConfirmadosText.text = countrySelected?.getConfirmed().toString()
            binding.casosMuertesText.text = countrySelected?.getDeaths().toString()
        }else{
            binding.casosActivosText.text = "0"
            binding.casosConfirmadosText.text = "0"
            binding.casosMuertesText.text = "0"
        }


    }

    override fun showNewCountryData(newData: Response<Summary>, position: Int) {
        binding.pais.text = binding.buscarPaisesSpinner.selectedItem.toString()
        binding.nuevosCasosText.text = newData.body()!!.countries[position].newConfirmed.toString()
        binding.nuevasMuertesText.text = newData.body()!!.countries[position].newDeaths.toString()
    }

    override fun notifyCountryAddedFav() {
        Toast.makeText(this, "Added to your favourite list!", Toast.LENGTH_SHORT).show()
    }

    override fun notifyCountryExistFav() {
        Toast.makeText(this,
                "you have already added the country: ${binding.buscarPaisesSpinner.selectedItem} to your favorite list",
                Toast.LENGTH_SHORT).show()
    }

    override fun shareCountryData(myIntent: Intent) {
        startActivity(Intent.createChooser(myIntent, "Share"))
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.agregarAfavoritos -> {
                addCountryToFavouriteList()
            }
            R.id.image_share -> {
                presenter.shareCountryOnSocialMedia(binding.buscarPaisesSpinner.selectedItem.toString(),
                        binding.casosActivosText.text.toString(),
                        binding.casosConfirmadosText.text.toString(),
                        binding.casosMuertesText.text.toString(),
                        binding.nuevasMuertesText.text.toString(),
                        binding.nuevosCasosText.text.toString())
            }
        }
    }

    private fun addCountryToFavouriteList() {
        presenter.addCountryToFavouriteList(binding.buscarPaisesSpinner.selectedItem.toString(),
        binding.casosActivosText.text.toString(),
        binding.casosConfirmadosText.text.toString(),
        binding.casosMuertesText.text.toString(),
        binding.nuevasMuertesText.text.toString(),
        binding.nuevosCasosText.text.toString())
    }


}