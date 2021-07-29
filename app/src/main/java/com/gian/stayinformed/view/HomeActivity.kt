package com.gian.stayinformed.view


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.gian.stayinformed.R
import com.gian.stayinformed.classes.CommonUtils
import com.gian.stayinformed.databinding.ActivityHomeBinding
import com.gian.stayinformed.interfaces.HomeActivityView
import com.gian.stayinformed.model.ByCountry
import com.gian.stayinformed.model.HomeActivityInteractor
import com.gian.stayinformed.model.Summary
import com.gian.stayinformed.model.coronaninjaapiclasses.CustomSpinnerAdapter
import com.gian.stayinformed.presenter.HomeActivityPresenter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Response
import kotlin.properties.Delegates


class HomeActivity : AppCompatActivity(), HomeActivityView,View.OnClickListener {
    private lateinit var binding: ActivityHomeBinding
    private val presenter= HomeActivityPresenter(this, HomeActivityInteractor())
    private var loadingDialog: Dialog? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var  clicks by Delegates.notNull<Int>()
    private var dismissShare = 0
    private var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog?.setContentView(R.layout.exit_dialog)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initializeInterstitial();
        initializeViews()
        presenter.initializeRecyclerView(binding.recyclerBuscarPaises)
        presenter.checkSharedPreference(this)
        presenter.getFavouritesData()
        presenter.getCountryDataForSpinner()
        setLogicToCountrySpinner()
    }


    private fun initializeInterstitial() {
        MobileAds.initialize(this) { createPersonalizedAd() }
    }

    private fun createPersonalizedAd() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        createInterstisialAd(adRequest)
    }

    private fun createInterstisialAd(adRequest: AdRequest) {
        //Original ca-app-pub-4185358034958198/9153246696
        //Prueba ca-app-pub-3940256099942544/1033173712
        InterstitialAd.load(this, "ca-app-pub-4185358034958198/9153246696", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd
                Log.i("---AdMob", "onAdLoaded")
                mInterstitialAd!!.setFullScreenContentCallback(object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        presenter.checkIfShareDismiss(dismissShare,
                                binding.buscarPaisesSpinner.selectedItem.toString(),
                                binding.casosConfirmadosText.text.toString(),
                                binding.casosActivosText.text.toString(),
                                binding.casosMuertesText.text.toString(),
                                binding.nuevosCasosText.text.toString(),
                                binding.nuevasMuertesText.text.toString())

                        createPersonalizedAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {

                        Log.d("TAG", "The ad failed to show.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        mInterstitialAd = null
                        Log.d("TAG", "The ad was shown.")
                    }
                })
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error
                Log.i("---AdMob", loadAdError.message)
                mInterstitialAd = null
            }
        })
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
        Toast.makeText(this, "Failed, try again later", Toast.LENGTH_SHORT).show()
    }

    override fun showCountrySelectedData(countrySelected: ByCountry?) {
        if(countrySelected != null){
            binding.casosActivosText.text = countrySelected.getActive().toString()
            binding.casosConfirmadosText.text = countrySelected.getConfirmed().toString()
            binding.casosMuertesText.text = countrySelected.getDeaths().toString()
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

    override fun showInterstatialAd() {

        mInterstitialAd!!.show(this);
        clicks=0;

    }

    override fun setDismisshShareTo0() {
        dismissShare = 0
    }

    override fun showDialogNotInternetConnection() {
        dialog = Dialog(this)
        dialog?.setContentView(R.layout.exit_dialog)
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()
        val image = dialog!!.findViewById<ImageView>(R.id.exit)
        image.setOnClickListener {
            finish()
        }

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.agregarAfavoritos -> {
                addCountryToFavouriteList()
            }
            R.id.image_share -> {
                if (mInterstitialAd!=null) {
                    mInterstitialAd!!.show(this);
                } else {
                    presenter.shareCountryOnSocialMedia(binding.buscarPaisesSpinner.selectedItem.toString(),
                            binding.casosActivosText.text.toString(),
                            binding.casosConfirmadosText.text.toString(),
                            binding.casosMuertesText.text.toString(),
                            binding.nuevasMuertesText.text.toString(),
                            binding.nuevosCasosText.text.toString())
                }
                dismissShare = 1;

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