package professorangoti.com.appclima.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by angoti on 31/10/2017.
 */

public class RetrofitService {

    private String baseUrl = "https://api.darksky.net/";
    private InterfaceDeServicos api;
    private static RetrofitService instancia;

    private RetrofitService() {
        api = criaRetrofit().create(InterfaceDeServicos.class);
    }

    private Retrofit criaRetrofit() {
        Gson gson = new GsonBuilder().create();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static InterfaceDeServicos getServico() {
        if (instancia == null)
            instancia = new RetrofitService();
        return instancia.api;
    }

}
