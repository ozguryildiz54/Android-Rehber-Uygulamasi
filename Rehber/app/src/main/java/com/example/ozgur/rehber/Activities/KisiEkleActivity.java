package com.example.ozgur.rehber.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ozgur.rehber.Model.Kisi;
import com.example.ozgur.rehber.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KisiEkleActivity extends AppCompatActivity {

    //Referans Nesneleri
    EditText ad_soyad,email,number; // EditText komponentleri
    Spinner cinsiyet; // Spinner nesnesi
    private ArrayAdapter<String> cinsiyetAdapter;
    private String[] cinsiyetDegerleri = {"Erkek","Kadın"};
    private TextWatcher text = null;
    private String numara,mail,isim;
    private Pattern regexPattern;
    private Matcher regMatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Main metodu
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kisi_ekle);

        //EditText komponentlerinin değerleri
        ad_soyad = (EditText) findViewById(R.id.adSoyad);
        email = (EditText) findViewById(R.id.email);
        number = (EditText) findViewById(R.id.numara);

        //Spinner komponentinin değeri
        cinsiyet = (Spinner) findViewById(R.id.spinnerCinsiyet);
        cinsiyetAdapter = new ArrayAdapter<String>(KisiEkleActivity.this,android.R.layout.simple_spinner_item,cinsiyetDegerleri);
        cinsiyetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cinsiyet.setAdapter(cinsiyetAdapter);
    }
    public void vazgec(View v){ // Bulunduğu sayfayı kapatır
        setResult(RESULT_CANCELED);
        finish();
    }

    //Ekle butonuna tıklanıldığında gerçekleşecek işlemler
    public void ekle(View v){
        boolean noB,isimB,mailB; // Doğru formatta girildiğinin kontrolü için tanımlanmıştır.
        final Kisi kisi = new Kisi(); // Kişi sınıfından bir nesne türetilir

        // Kullanıcı verileri değişkenlere kaydedilir
        isim = ad_soyad.getText().toString();
        mail = email.getText().toString();
        numara = number.getText().toString();

        if (isim.length() > 0){ // İsim alanına veri girildiğini kontrol eder
            isimB = true;
            kisi.setAd(ad_soyad.getText().toString());
        }else{ // İsim alanına veri girilmemişse uyarı verir.
            isimB = false;
            Toast.makeText(KisiEkleActivity.this,"İsim boş geçilemez!",Toast.LENGTH_SHORT).show();
        }
        if (mail.length() > 0){ // Mail alanının hem boş hemde uygun veri girildiğinin kontrolünü sağlar.
            regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-Z)]{2,3}$"); // Mail kontrolü bu formatta ki gibidir.
            regMatcher = regexPattern.matcher(mail); // Girilien metnin bu formata uygunluğu kontrol edilir.
            if (regMatcher.matches()) {
                mailB = true;
                kisi.setEmail(email.getText().toString());
            } else {
                mailB = false;
                Toast.makeText(KisiEkleActivity.this, "Mail adresiniz geçerli değil!", Toast.LENGTH_SHORT).show();
            }
        }else{
            mailB = false;
            Toast.makeText(KisiEkleActivity.this,"Mail boş geçilemez!",Toast.LENGTH_SHORT).show();
        }
        if (numara.length() == 11){ // Numara bölümünün 11 haneli olduğunun kontrolünü yapar.
                noB = true;
                kisi.setNumara(number.getText().toString());

        }else{
            noB = false;
            Toast.makeText(KisiEkleActivity.this,"Numara 11 haneli değil!",Toast.LENGTH_SHORT).show();
            }

        kisi.setCinsiyet(cinsiyet.getSelectedItem().toString()); // Cinsiyet bilgisi kisi sınıfına yüklenir.
        if( noB == true && isimB == true && mailB == true ){ // Tüm verilerin uygun girildiğinin kontrolü saptanır.
            kisi.setId(0); // Varsayılan başlangıç değeridir. Sunucu tarafında id numarasının artışı otamatik olarak sağlanmaktadır.
            final RequestQueue queue = Volley.newRequestQueue(this); // Sunucu istediği için Volley kütüphanesi kullanıldı.
            try{
                String[] dizi = kisi.getAd().split(" "); // İsimleri ve soyisimleri parçalayarak bir diziye aktarır.
                String isim = "";
                String soyisimm="Belirtilmemis";
                int sayac = dizi.length; // Dizinin boyutu

                int l=sayac-1; // Dizi indisi sıfırdan başladığından soyisim dizi boyutunun bir eksiği olur.
                soyisimm = dizi[l];

                if(soyisimm !=""){ // Soyisim girilmişse diziden soyisim verisini siler
                    // Bu işlemi yapmamızın sebebi kullanıcıdan isim ve soyismi aynı alan içerisinde aldığımızdan sunucuda ki Json dosyamıza aktarırken isim ve soyisim alanlarına ayrı ayrı aktarabilmek içindir.
                    String[] output = new String[dizi.length - 1];
                    int count = 0;
                    for (String i : dizi) {
                        if (!i.equals(dizi[l])) {
                            output[count++] = i;
                        }
                    }
                    dizi = output;
                    for(int j=0;j<dizi.length;j++) {
                        isim = isim+dizi[j]+" ";

                    }
                    isim = isim.trim();
                }else{
                    soyisimm = "Belirtilmemis.";
                    isim = kisi.getAd();
                }

                String url = "http://10.0.2.2:8080/kisi-ekle"; // Sunucumuzun adresi ve istediğimizin parametresi
                final String finalIsim = isim;
                final String finalSoyisimm = soyisimm;
                StringRequest compareRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                setResult(RESULT_OK);
                                finish();// Sayfa sonlandırılır.
                                Toast.makeText(KisiEkleActivity.this,response,Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {}
                }){
                    @Override
                    protected Map<String, String> getParams(){ // Sunucuya göndereceğimiz istediğin parametreleri
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("id", String.valueOf(kisi.getId()));
                        params.put("isim", finalIsim);
                        params.put("soyisim", finalSoyisimm);
                        params.put("mail", kisi.getEmail());
                        params.put("numara", kisi.getNumara());
                        params.put("cinsiyet", kisi.getCinsiyet());

                        return params;
                    }
                };
                queue.add(compareRequest);

            }catch (Exception e){

            }

        }

    }
}
