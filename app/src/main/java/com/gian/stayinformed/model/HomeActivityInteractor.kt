package com.gian.stayinformed.model

import Adapters.PaisesFavAdapter
import ProjectClasses.Covid19ApiSingleton
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gian.stayinformed.view.HomeActivity
import com.google.firebase.database.*

import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class HomeActivityInteractor {
    private val recipeServiceCovid19Api = Covid19ApiSingleton.getService()
    private lateinit var listener:onHomeActivityListener
    private lateinit var key:String
    private lateinit var keyPaises:String
    private lateinit var context: HomeActivity
    private val mlist: MutableList<PaisesFavoritosData> = mutableListOf()
    private lateinit var recyclerView:RecyclerView
    private var adapter: PaisesFavAdapter? = null

    interface onHomeActivityListener{
            fun onShowProgressDialog()
            fun onHideProgressDialog()
            fun onSetSpinnerListCountries(spinnerArrayListCountries: MutableList<String>)
            fun onFailureResponse()
            fun onPassCountryData(countrySelected: ByCountry?)
            fun onPassNewCountryData(newData: Response<Summary>, position: Int)
            fun onCountryExistFav()
            fun onCountryAddedToFav()
            fun onShareCountryData(myIntent: Intent)


    }

    fun fillCountriesSpinner(listener: onHomeActivityListener) {
        this.listener = listener
        listener.onShowProgressDialog()
        val callCountries = recipeServiceCovid19Api.countries
        callCountries.enqueue(object : Callback<LinkedList<Countries>> {
            @RequiresApi(api = Build.VERSION_CODES.N)
            override fun onResponse(call: Call<LinkedList<Countries>>, response: Response<LinkedList<Countries>>) {
                setearSpinner(response)
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            private fun setearSpinner(response: Response<LinkedList<Countries>>) {
                val spinnerArrayListCountries: MutableList<String> = ArrayList()
                for (i in response.body()!!.indices) {
                    spinnerArrayListCountries.add(response.body()!![i].country)
                }
                spinnerArrayListCountries.sortWith(Comparator { obj: String, s: String? -> obj.compareTo(s!!, ignoreCase = true) })
                listener.onSetSpinnerListCountries(spinnerArrayListCountries)

            }

            override fun onFailure(call: Call<LinkedList<Countries>>, t: Throwable) {
                listener.onFailureResponse()
            }
        })

    }

    fun getInfoAbout(country: String) {
        val callByCountry = recipeServiceCovid19Api.getByCountry("https://api.covid19api.com/live/country/" +
                country + "/" + "status" + "/" + "confirmed")

        callByCountry.enqueue(object : Callback<List<ByCountry?>?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<List<ByCountry?>?>, response: Response<List<ByCountry?>?>) {
                if (response.isSuccessful) {
                    if (response.body()!!.isNotEmpty()) {
                        var countrySelected: ByCountry? = ByCountry()
                        countrySelected = response.body()!![response.body()!!.size - 1]
                        listener.onPassCountryData(countrySelected)
                    } else {
                        listener.onPassCountryData(null)
                    }
                }
            }

            override fun onFailure(call: Call<List<ByCountry?>?>, t: Throwable) {

            }
        })

    }

    fun getNewCountryInfo(country: String) {

        val callSummary = recipeServiceCovid19Api.getSummary("https://api.covid19api.com/summary")

        callSummary.enqueue(object : Callback<Summary> {
            override fun onResponse(call: Call<Summary>, response: Response<Summary>) {
                if (response.isSuccessful) {
                    for (i in response.body()!!.getCountries().indices) {
                        if (response.body()!!.getCountries()[i].getCountry()
                                == country) {
                            listener.onPassNewCountryData(response, i)

                        }
                    }
                }
            }


            override fun onFailure(call: Call<Summary>, t: Throwable) {}
        })
    }

    fun getFavouriteDataFromDB() {
        val database = FirebaseDatabase.getInstance().reference.child("PaisesFavoritos").child(key)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val paisesFavoritosData = dataSnapshot.getValue(PaisesFavoritosData::class.java)
                    getMyFlagFavorite(paisesFavoritosData!!.nombre,
                            paisesFavoritosData.casosActivos, paisesFavoritosData.casosConfirmados,
                            paisesFavoritosData.muertes, paisesFavoritosData.nuevosCasos,
                            paisesFavoritosData.nuevasMuertes
                    )
                    generarRecycler(paisesFavoritosData.nombre)
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun checkSharedPreference(context: HomeActivity) {
        this.context = context
        val prefs: SharedPreferences = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
        if (!prefs.contains("key")) {
            key = FirebaseDatabase.getInstance().getReference("Paises").push().key.toString()
            val editor: SharedPreferences.Editor = context.getSharedPreferences("shared", Context.MODE_PRIVATE).edit()
            editor.putString("key", key)
            editor.apply()
        } else {
            val prefsGet: SharedPreferences = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
            key = prefsGet.getString("key", "").toString()
        }
    }

    fun addCountryToFavouriteDB(country: String, actives: String, confirmed: String, death: String, newDeath: String, newCases: String) {
        keyPaises = FirebaseDatabase.getInstance().getReference("key").push().key.toString()
        val ref = FirebaseDatabase.getInstance().reference.child("PaisesFavoritos").child(key)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var existe = false
                for (snap in snapshot.children) {
                    if (snap.child("nombre").value.toString() == country) {
                        existe = true
                    }
                }
                if (!existe) {
                    val addCountryFav: HashMap<String, Any> = HashMap()
                    addCountryFav["nombre"] = country
                    addCountryFav["casosActivos"] = actives
                    addCountryFav["casosConfirmados"] = confirmed
                    addCountryFav["muertes"] = death
                    addCountryFav["nuevosCasos"] = newCases
                    addCountryFav["nuevasMuertes"] = newDeath
                    FirebaseDatabase.getInstance().reference.child("PaisesFavoritos").child(key).child(keyPaises).updateChildren(addCountryFav)
                    getMyFlagFavorite(country, actives, confirmed, death, newCases, newDeath)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getMyFlagFavorite(countryName: String, actives: String, confirmed: String, death: String, newCases: String, newDeath: String) {
        val URL = "https://corona.lmao.ninja/v2/countries"
        val request = StringRequest(Request.Method.GET, URL,
                { response ->
                    var theFlag: String
                    try {
                        val jsonArray = JSONArray(response)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            if (countryName == jsonObject.getString("country")) {
                                val jsonObject1 = jsonObject.getJSONObject("countryInfo")
                                theFlag = jsonObject1.getString("flag")
                                mlist.add(PaisesFavoritosData(actives, confirmed,
                                        death, countryName, newDeath, newCases, theFlag))
                                generarRecycler(countryName)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }) { }


        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(request)

    }

    private fun generarRecycler(countryName: String) {
        adapter = PaisesFavAdapter(context, mlist)
        val linearLayoutManager = LinearLayoutManager(context, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        listener.onHideProgressDialog()
        adapter!!.setOnItemClickListener(object : PaisesFavAdapter.OnItemClickListener {
            override fun onitemClick(position: Int) {}
            override fun onDelateClick(position: Int) {
                removeItem(position)
            }

            override fun onShareClick(position: Int) {
                val myIntent = Intent(Intent.ACTION_SEND)
                myIntent.type = "text/plain"
                val shareBody = """
             Country: ${mlist[position].nombre}
             Confirmed: ${mlist[position].casosConfirmados}
             Active: ${mlist[position].casosActivos}
             Deaths: ${mlist[position].muertes}
             New cases: ${mlist[position].nuevosCasos}
             New deaths: ${mlist[position].nuevasMuertes}
             """.trimIndent()
                val shareSubject: String = countryName
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                listener.onShareCountryData(myIntent)
            }

            private fun removeItem(position: Int) {
                eliminarItemDeFirebase(position)
                mlist.removeAt(position)
                adapter!!.notifyItemRemoved(position)
            }

            private fun eliminarItemDeFirebase(position: Int) {
                val ref = FirebaseDatabase.getInstance().reference
                val applesQuery = ref.child("PaisesFavoritos").child(key).orderByChild("nombre").equalTo(mlist[position].nombre)
                applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (appleSnapshot in dataSnapshot.children) {
                            appleSnapshot.ref.removeValue()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        })

    }

    fun initialize(recyclerBuscarPaises: RecyclerView) {
        this.recyclerView = recyclerBuscarPaises
    }

    fun shareCountryOnSocialMedia(country: String, actives: String, confirmed: String, death: String, newDeath: String, newCases: String) {
        val myIntent = Intent(Intent.ACTION_SEND)
        myIntent.type = "text/plain"
        val shareBody = """
            Country: ${country}
            Confirmed: ${actives}
            Active: ${confirmed}
            Deaths: ${death}
            New cases: ${newDeath}
            New deaths: ${newCases}
            """.trimIndent()
        val shareSubject: String = country
        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(myIntent, "Share"))
    }


}