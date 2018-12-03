package professorangoti.com.appclima.service;


import professorangoti.com.appclima.model.DadosClima;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface InterfaceDeServicos {
    @GET("forecast/8eeafa93fa171bb970bfac9b03caa3a3/{latitude},{longitude}?exclude=minutely,hourly,daily,flags,alerts")
    Call<DadosClima> consulta(@Path("latitude") double latitude,
                              @Path("longitude") double longitude);
}

