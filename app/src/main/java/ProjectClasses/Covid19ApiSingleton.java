package ProjectClasses;

import Interfaces.CovidApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Covid19ApiSingleton {

    private static Retrofit retrofit = null;
    private static String BASE_URL = "https://api.covid19api.com/";

    public static CovidApi getService(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit.create(CovidApi.class);
    }
}
