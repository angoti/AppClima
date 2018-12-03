package professorangoti.com.appclima;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import professorangoti.com.appclima.model.DadosClima;
import professorangoti.com.appclima.service.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ImageView imagem;
    TextView timezone;
    private EditText mLatitudeEditText;
    private EditText mLongitudeEditText;
    private EditText mNomeCidade;
    private TextView mDadosJson;
    private ProgressBar mProgreesBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagem = (ImageView) findViewById(R.id.imageView);
        timezone = (TextView) findViewById(R.id.timezoneTextView);
        mProgreesBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgreesBar.setVisibility(View.INVISIBLE);
    }

    public void consulta(View v) {
        mProgreesBar.setVisibility(View.VISIBLE);
        mLatitudeEditText = (EditText) findViewById(R.id.latitudeEditText);
        mLongitudeEditText = (EditText) findViewById(R.id.longitudeEditText);
        mNomeCidade = (EditText) findViewById(R.id.nomeCidadeEditText);
        mDadosJson = (TextView) findViewById(R.id.respostaJSONTextView);
        double latitude = 0, longitude = 0;
        if (mNomeCidade.getText().toString().length() == 0 &&
                mLatitudeEditText.getText().length() != 0 &&
                mLongitudeEditText.getText().length() != 0) {
            latitude = Double.parseDouble(mLatitudeEditText.getText().toString());
            longitude = Double.parseDouble(mLongitudeEditText.getText().toString());
        } else if ((mLatitudeEditText.getText().length() == 0 | mLongitudeEditText.getText().length() == 0) &&
                mNomeCidade.getText().length() > 0) {
            LatLng ll = getLatLng(mNomeCidade.getText().toString());
            latitude = ll.getLatitude();
            longitude = ll.getLongitude();
            if (latitude == 0.0 || longitude == 0) {
                mProgreesBar.setVisibility(View.INVISIBLE);
                timezone.setText("Cidade não encontrada");
                mDadosJson.setText("");
                imagem.setImageResource(R.drawable.naoencontrado);
                return;
            }
        } else {
            Toast.makeText(this, "Preencha os campos corretamente", Toast.LENGTH_SHORT).show();
            mProgreesBar.setVisibility(View.INVISIBLE);
            return;
        }
        limpaCampos();
        final double latitudefinal = latitude;
        final double longitudefinal = longitude;
        Call<DadosClima> chamada = RetrofitService.getServico().consulta(latitude, longitude);
        chamada.enqueue(new Callback<DadosClima>()

        {
            @Override
            public void onResponse(Call<DadosClima> call, Response<DadosClima> response) {
                DadosClima dados = response.body();
                mProgreesBar.setVisibility(View.INVISIBLE);
                mDadosJson.append(new Gson().toJson(response.body()));
                String icon = dados.getCurrently().getIcon();

                switch (icon) {
                    case "clear-day":
                        imagem.setImageResource(R.drawable.clearday);
                        break;
                    case "clear-night":
                        imagem.setImageResource(R.drawable.clearnight);
                        break;
                    case "rain":
                        imagem.setImageResource(R.drawable.rain);
                        break;
                    case "snow":
                        imagem.setImageResource(R.drawable.snow);
                        break;
                    case "sleet":
                        imagem.setImageResource(R.drawable.sleet);
                        break;
                    case "wind":
                        imagem.setImageResource(R.drawable.wind);
                        break;
                    case "fog":
                        imagem.setImageResource(R.drawable.fog);
                        break;
                    case "cloudy":
                        imagem.setImageResource(R.drawable.cloudy);
                        break;
                    case "partly-cloudy-day":
                        imagem.setImageResource(R.drawable.partlycloudday);
                        break;
                    case "partly-cloudy-night":
                        imagem.setImageResource(R.drawable.partlycloudynight);
                        break;
                    default:
                        imagem.setImageResource(R.drawable.naoencontrado);
                        break;
                }
                timezone.setText(getNomeCidade(latitudefinal, longitudefinal));
            }

            @Override
            public void onFailure(Call<DadosClima> call, Throwable t) {
                Log.i("teste", t.getMessage());
            }
        });
    }

    private void limpaCampos() {
        mLatitudeEditText.getText().clear();
        mLongitudeEditText.getText().clear();
        mNomeCidade.getText().clear();
    }

    String getNomeCidade(double lat, double lng) {
        String retorno;
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            retorno = addresses.get(0).getLocality();
        } else {
            retorno = "não encontrado";
        }
        return retorno;
    }

    LatLng getLatLng(String location) {
        LatLng retorno = new LatLng(0, 0);
        if (Geocoder.isPresent()) try {
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = gc.getFromLocationName(location, 5);
            if (addresses.size() == 0) return retorno;
            List<LatLng> ll = new ArrayList<LatLng>(addresses.size());
            for (Address a : addresses) {
                if (a.hasLatitude() && a.hasLongitude()) {
                    ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                }
            }
            if (ll.size() != 0)
                retorno = ll.get(0);
        } catch (IOException e) {
            Log.e("MainActivity", "Falha na busca latlong: " + e.getMessage());
        }
        return retorno;
    }

    private class LatLng {
        double latitude;
        double longitude;

        public LatLng(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
