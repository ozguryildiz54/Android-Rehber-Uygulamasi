package com.example.ozgur.rehber.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class DetaylarActivity extends AppCompatActivity {

    int id; // id değeri saklanır

    //Referans Nesneleri
    private EditText ad_soyad, email, number;
    private Button btn,iptal,vazgec;
    private Spinner cinsiyet;
    private ArrayAdapter<String> cinsiyetAdapter;
    private String[] cinsiyetDegerleri = {"Erkek", "Kadın"};

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Main metodu
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaylar);

        //EditText komponentlerinin değerleri
        ad_soyad = (EditText) findViewById(R.id.adSoyad);
        email = (EditText) findViewById(R.id.email);
        number = (EditText) findViewById(R.id.numara);
        btn = (Button) findViewById(R.id.kaydet_btn);
        iptal = (Button) findViewById(R.id.iptal);
        vazgec = (Button) findViewById(R.id.vazgec);

        //Spinner komponentinin değeri
        cinsiyet = (Spinner) findViewById(R.id.spinnerCinsiyet);
        cinsiyetAdapter = new ArrayAdapter<String>(DetaylarActivity.this, android.R.layout.simple_spinner_item, cinsiyetDegerleri);
        cinsiyetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cinsiyet.setAdapter(cinsiyetAdapter);

        Bundle extras = getIntent().getExtras(); // ListAdapter ile tıkladığımız kişinin id bilgisine bu satır ile ulaşacağız
        id = extras.getInt("id"); // id değerine ulaşmamız için "id" parametresi bizim için bir şifre gibidir.

        final RequestQueue queue = Volley.newRequestQueue(this); // Http protokolümüz için Volley kütüphanesi kullanıldı.
        final Kisi kisi = new Kisi(); // Kisi sınıfından nesne türeterek kişi bilgilerini o sınıfı yükleyeceğiz
        String url = "http://10.0.2.2:8080/kisi-yukle"; // Sunucumuzun tam adresi ve sunucuya gönderilen isteğimizin parametresi
        Log.i("DetaylarActivity", "id2:"+String.valueOf(id)); // id değerimizi bu satır ile rahatlıkla görebiliriz.

        StringRequest compareRequest = new StringRequest(Request.Method.GET, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jarray = new JSONArray(response); // Sunucundan gelen kişi listesini bir JsonArray nesnesine çeviriyoruz.
                    for(int i =0; i<jarray.length();i++){ // Dizimizin boyutu kadar bir döngü oluşturduk

                        int idd = Integer.parseInt(jarray.getJSONObject(i).getString("id")); // Tüm kişilerin id değerini aldık
                        if(id == idd){ // Tüm kişilerin id değerini tıkladığımız kişinin id değeri ile karşılaştırıyoruz.

                            // Eşleşme olduğunda ise o kişinin tüm verilerini değişkenlere aktarıyoruz.
                            String isim = jarray.getJSONObject(i).getString("isim").toString();
                            isim = isim + " " + jarray.getJSONObject(i).getString("soyisim").toString();
                            String mail = jarray.getJSONObject(i).getString("mail").toString();
                            String numara = jarray.getJSONObject(i).getString("numara").toString();
                            String cinsiyett = jarray.getJSONObject(i).getString("cinsiyet").toString();

                            // Tüm verileri alınıp değişkenlere yüklendi. Şimdi bu değişkenlerden ekranda ki komponentlere aktarıyoruz.
                            ad_soyad.setText(isim);
                            email.setText(mail);
                            number.setText(numara);

                            // Sayfa açıldığında düzenle butonuna basmadan önceki sayfanın görünümü
                            ad_soyad.setEnabled(false); // İsim ve soyismin yazılı olduğu komponente veri girilmez. Ancak metin görüntülenir.
                            ad_soyad.setTypeface(null, Typeface.BOLD_ITALIC); // Yazı tipi italiktir.
                            ad_soyad.setTextColor(Color.BLACK); // Yazı boyutu dikkat çekmek için kalındır.

                            email.setEnabled(false); // Email alanı da isim alanı gibidir.
                            email.setTypeface(null, Typeface.BOLD_ITALIC);

                            number.setEnabled(false); // Numara alanı da isim alanı gibidir.
                            number.setTypeface(null, Typeface.BOLD_ITALIC);

                            cinsiyet.setEnabled(false); // Cinsiyet alanı da pasifdir. Seçim yapılmaz ancak veri görüntülenir.

                            // Yalnızca vazgeç butonu aktifdir. Diğer butonlar gizlenmiştir.
                            btn.setVisibility(View.GONE);
                            iptal.setVisibility(View.GONE);
                            vazgec.setVisibility(View.VISIBLE);

                            if (kisi.getCinsiyet().equals("Erkek")) { // Kişinin cinsiyetine göre spinner nesnesini tetikler.
                                cinsiyet.setSelection(0);
                            } else {
                                cinsiyet.setSelection(1);
                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        queue.add(compareRequest); // İsteğimizi kuyruğa ekler.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Sağ üstteki menümüzü ekranda oluşturur.
        getMenuInflater().inflate(R.menu.detaylar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Sağ üst menüden hangisini seçtiğim ile alakalı kontrolleri yapar.

        int id = item.getItemId();
        if (id == R.id.ara) { // Cep telefonun kişiyi aramamı sağlar.
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number.getText().toString())); // Arama yapma kodu

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return false;
            }
            startActivity(intent);
            return true;
        } else if (id == R.id.sil) { // Silme işlemini gerçekleştirir.
            sil();
            return true;
        } else if (id == R.id.duzenle) { // Kişiyi düzenyebilmemizi sağlar. Bunun içinde ekran üzerinde ki komponentleri aktif eder.
            ad_soyad.setEnabled(true); // İsim alanı aktif olur.
            ad_soyad.setTypeface(null, Typeface.NORMAL); // Yazı tipi normal varsayılan font olur.
            ad_soyad.setTextColor(Color.BLACK); // Yazı rengi siyah olur.

            email.setEnabled(true); // Email alanı aktif olur.
            email.setTypeface(null, Typeface.NORMAL); // Yazı tipi normal olur.

            number.setEnabled(true); // Numara alanı aktif olur.
            number.setTypeface(null, Typeface.NORMAL); // Yazi tipi normal yani varsayılan font olur

            cinsiyet.setEnabled(true); // Cinsiyet alanı aktif olur. Yani veri seçimi yapılabilir.
            btn.setVisibility(View.VISIBLE);
            iptal.setVisibility(View.VISIBLE);
            vazgec.setVisibility(View.GONE); // Vazgeç butonu gizlenir. Diğer butonlar aktif olur.
            return true;
        } else {
            return true;
        }

    }
    public void vazgec(View v){ // Bu metot ile sayfa sonlandırılır.
        setResult(RESULT_CANCELED);
        finish();
    }
    public void kaydet(View v){ // Bu metot ile yapılan değişiklerimiz kaydedilir.
        Kisi kisi = new Kisi(); // Kişi sınıfından bir nesne türetilir.
        final RequestQueue queue = Volley.newRequestQueue(this); // Http protokolümüz için Volley kütüphanesi kullanıldı.
        try{
            String parcala = String.valueOf(ad_soyad.getText()); // İsim bilgisi alındı.
            String[] dizi = parcala.split(" "); // İsim ve soyisim parçalanarak diziye aktarıldı.
            String isim = "";
            String soyisimm="Belirtilmemis";
            int sayac = dizi.length; // Dizi boyutu aktarılır.
            int l=sayac-1; // Soyismin bulunduğu indis dizi boyutunun bir eksiği kadardır. Çünkü dizi indisi sıfırdan başlar.
            soyisimm = dizi[l]; // Soyisim diziden değişkenimize kopyalanır.
            if(soyisimm !=""){ // Soyisim değeri varsa yani kullanıcı soyisim girmişse bu diziden silinir. Çünkü zaten soyisimi diziye kopyalamıştık. Dizinin kalan değerleri sadece kullanıcının isimlerinden oluşur.
                // Bu işlemi yapmamızın sebebi kullanıcının birden fazla isminin olduğu durumlarda hepsine ulaşabilmemizi sağlar.

                // Burada diziden soyisim verisi silinir.
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

            String url = "http://10.0.2.2:8080/kisi-guncelle"; // Sunucumuzun tam adresi ve gönderilen istediğin parametresi
            final String finalIsim = isim;
            final String finalSoyisimm = soyisimm;
            StringRequest compareRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) { // Sunucunun cevap gönderdiği response bloğu
                            // Cevap geldiğinde ekranda ki komponentleri tekrar pasif hale getirerek kullanıcının veri girişi engellenir. Daha sonrada sayfa sonlandırılır.
                            ad_soyad.setEnabled(false);
                            ad_soyad.setTypeface(null, Typeface.BOLD_ITALIC);
                            ad_soyad.setTextColor(Color.BLACK);

                            email.setEnabled(false);
                            email.setTypeface(null, Typeface.BOLD_ITALIC);

                            number.setEnabled(false);
                            number.setTypeface(null, Typeface.BOLD_ITALIC);

                            cinsiyet.setEnabled(false);
                            btn.setVisibility(View.GONE);
                            iptal.setVisibility(View.GONE);
                            vazgec.setVisibility(View.VISIBLE);
                            /*Intent intent = new Intent(DetaylarActivity.this,MainActivity.class);
                            startActivity(intent);*/
                            setResult(RESULT_OK);
                            finish();
                            Toast.makeText(DetaylarActivity.this,response,Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {}
            }){
                @Override
                protected Map<String, String> getParams(){ // Sunucuya gönderdiğimiz istediğin parametreleri
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("id", String.valueOf(id));
                    params.put("isim", finalIsim);
                    params.put("soyisim", finalSoyisimm);
                    params.put("mail", String.valueOf(email.getText()));
                    params.put("numara", String.valueOf(number.getText()));
                    params.put("cinsiyet", cinsiyet.getSelectedItem().toString());

                    return params;
                }
            };
            queue.add(compareRequest);

        }catch (Exception e){

        }
    }
    public void iptal(View v){ // Bulunduğumuz ekrandan ayrılmadan sadece işlemi iptal etmemizi sağlar. Bunu yaparkende ekran üzerinde ki komponentleri pasif hale getirir.
        ad_soyad.setEnabled(false);
        ad_soyad.setTypeface(null, Typeface.BOLD_ITALIC);
        ad_soyad.setTextColor(Color.BLACK);

        email.setEnabled(false);
        email.setTypeface(null, Typeface.BOLD_ITALIC);

        number.setEnabled(false);
        number.setTypeface(null, Typeface.BOLD_ITALIC);

        cinsiyet.setEnabled(false);
        btn.setVisibility(View.GONE);
        iptal.setVisibility(View.GONE);
        vazgec.setVisibility(View.VISIBLE);

    }
    public void sil(){ // Kişiyi silme işlemini gerçekleştirir.

        final RequestQueue queue = Volley.newRequestQueue(this);
        AlertDialog.Builder uyari = new AlertDialog.Builder(DetaylarActivity.this); // Kullanıcıya uyarı mesajı vermemizi sağlar.
        uyari.setTitle("Uyarı"); // Uyarının başlığı
        uyari.setMessage("Kaydı silmek istedinizden eminmisiniz?"); // Uyarı mesajı
        uyari.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) { // Seçenenkler ; ben even ve hayır seçeneği tanımladım.

                // Bu blok evet seçeneğimi seçtiğimde gerçekleşecek işlemleri içerir.

                // Sunucuya silme işlemi için bir istek gönderilir.
                String url = "http://10.0.2.2:8080/kisi-sil?id="+id;
                StringRequest compareRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                setResult(RESULT_OK);
                                finish(); // Cevap geldiğinde sayfa sonlandırılır.
                                Toast.makeText(DetaylarActivity.this,response,Toast.LENGTH_SHORT).show();

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {}
                });
                queue.add(compareRequest); // İsteğimizi kuyruğa ekler.
            }
        });

        uyari.setNegativeButton("Hayır", new DialogInterface.OnClickListener() { // Hayır butonuna tıkladığımızda yapılacak işlemleri içerir.
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Hayır butonuna tıkladığında her hangi bir değişiklik yapılmasını istemediğimden bu bloğa bir şey yazmadım. Bu sayede bu seçeneği seçtiğimde uygulamamda hiçbir değişiklik olmayacaktır.
            }
        });
        Dialog dialog = uyari.create(); // Dialog pencerine uyarı penceresi aktarılır.
        dialog.show(); // Dialog penceresi çalıştırılır.
    }
}
