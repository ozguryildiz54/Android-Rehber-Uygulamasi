package com.example.ozgur.rehber.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ozgur.rehber.Adapter.ListAdapter;
import com.example.ozgur.rehber.Model.Kisi;
import com.example.ozgur.rehber.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Referans nesnelerinin tanımlamaları.
    RecyclerView listview; // Kişileri ana ekranda listelemek için tanımlandı.
    private String isim;    // Ana ekranda kişi isimlerini aramak için tanımlandı.
    EditText ara;   // Kisilerin aranabileceği kişi isminin alınması için tanımlandı.
    private TextWatcher text = null; // Arama yapılırken her harfe basıldığında sonuçların güncellenmesi için tanımlındı
    List<Kisi> kisiler; // Ana ekranda kişileri listeleyebilmek için bu liste kullanılır.
    boolean kontrolEt = false;
    // Main fonksiyonudur
    // Activity oluşturulduğunda yapılacaklar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kisiler = new ArrayList<Kisi>();

        // Referans nesnelerini ilişkili komponentler ile bağladık
        listview = (RecyclerView) findViewById(R.id.listRecyle);
        listview.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration ıtemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        listview.addItemDecoration(ıtemDecoration);
        ara = (EditText) findViewById(R.id.editText) ; // EditText girdisi ara nesnesine aktarılır.
        listeYukle(); // Sayfa ilk açıldığı anda kişi listesi ana ekrana yuklenir
        text = new TextWatcher() { // Klavyeden alınan her veri ile kişi arama sonuçları güncellemek için tanımlandı.
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isim = ara.getText().toString(); // Kullanıcının yazdığı kişi adı isim değişkenine atanır.
                List<Kisi> listele = kisiAra(kisiler,isim); // kisiAra metodunun sonuçları listele nesnesine atanır.
                goruntule(listele); // Ana ekranda arama sonuçları listelenir.
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        ara.addTextChangedListener(text); // Metin değişikliğini dinler
    }

    private void goruntule(List<Kisi> kisiler){ // Arama sonuclarını ekranda ki listede goruntuler

        ListAdapter adapter = new ListAdapter(kisiler,this); // ListAdapter sınıfınfan bir nesne türetilerek kisi listesi o nesneye gönderilir.
        listview.setAdapter(adapter); // oluşturulan nesne ile liste ekranda listlenir.
    }

    @Override
    protected void onStart() { // Bulunduğumuz sayfa duraksayıp tekrar çalıştığında aşağıda ki işlemler gerçekleşir.
        super.onStart();
        Log.i("Main", "onStart");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // Bu metot bir sayfadan ayrılıp ayrıldığımız sayfaya geri döndüğümüzde ayrıldığımız sayfadan veri yüklememizi sağlar.
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) { // Kişi ekleme sayfası
            if (resultCode == RESULT_OK) {
                Log.i("Main", "Kişi eklendi");
                listeYukle();
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("Main", "Kişi ekleme iptal edildi");
            }
        }else if(requestCode == 2) { // Detaylar sayfası
            if(resultCode == RESULT_OK) {
                Log.i("Main","Kişi düzenlendi");
                kisiler.clear();
                listeYukle();
            }

            else if(resultCode == RESULT_CANCELED) {
                Log.i("Main","Kişi düzenlenmedi");
            }
        }
    }

    public void onResume(){ // Sayfadan ayrılıp geri döndüğümüzde önce onStart sonra bu metot çalışır.
        super.onResume();
        Log.i("Main", "onResume");
    }

    public void listeYukle(){ // Sunucuya tüm kişileri yüklemesi için istek gönderir ve sonucu da kişiler listesine aktararak ekranda görüntüler.
            RequestQueue queue = Volley.newRequestQueue(this); // Java da http protokolünü için volley kütüphanesi tercih edildi.
            String url = "http://10.0.2.2:8080/kisi-yukle"; // Sunucumuzun tam adresi ve sunucuya gönderilen isteğimizin parametresi
            kisiler.clear(); // Kişileri listelemeden önce listeden önce ki tüm verilir.
            final ListAdapter adapter = new ListAdapter(kisiler,this);    // ListAdapter sınıfından bir nesne türetildi ve kisiler listesi parametre olarak gönderildi.
            // ListAdapter sınıfı sunucudan gelen verileri Kisi sınıfına yükler.

            StringRequest compareRequest = new StringRequest(Request.Method.GET, url, // Sunucuya istedğimiz bu blokta ki tanımlama ile gönderiyoruz.
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONArray jarray = new JSONArray(response); // Sunucumuzdan dönen cevap response nesnesinde saklanır ve biz bu nesne ile sunucunun gönderdiği kişileri ulaşacağız.
                                // Bunun için bu nesne string tipinde olduğundan Json array tipine dönüştürüyoruz.

                                for(int i =0; i<jarray.length();i++){ // Cevap olarak gelen tüm kişilerin bilgisine ulaşıp hepsini listeye aktaracağız.

                                    // Tüm kişilerimizin verileri aşağıda ki gibi değişkenlere aktarılır.
                                    int id = Integer.parseInt(jarray.getJSONObject(i).getString("id"));
                                    String isim = jarray.getJSONObject(i).getString("isim").toString();
                                    isim=isim+" "+ jarray.getJSONObject(i).getString("soyisim").toString();
                                    String mail = jarray.getJSONObject(i).getString("mail").toString();
                                    String numara = jarray.getJSONObject(i).getString("numara").toString();
                                    String cinsiyet = jarray.getJSONObject(i).getString("cinsiyet").toString();

                                    Kisi kisi = new Kisi(); // Kisiler sınıfıdan bir referans nesnesi türettik
                                    // Kişilerin verileri Kisi sınıfına aktarılır.
                                    kisi.setId(id);
                                    kisi.setAd(isim);
                                    kisi.setCinsiyet(cinsiyet);
                                    kisi.setEmail(mail);
                                    kisi.setNumara(numara);
                                    kisiler.add(kisi); // Tüm kisi bilgileri listeye eklenir.
                                }
                                listview.setAdapter(adapter);   // Kişiler listemizi adapter ile RecylerView içerisine aktarmak için tanımlandı.

                                if(ara.getText().length()>0){ // Arama çubuğunda bir şeyler yazılıysa aşağıda ki blok çalışır.
                                    List<Kisi> listele = kisiAra(kisiler,isim); // kisiAra metodunun sonuçları listele nesnesine atanır.
                                    goruntule(listele); // Ana ekranda arama sonuçları listelenir.
                                    /*ListAdapter adapter = new ListAdapter(kisiAra(kisiler,isim),getApplicationContext());
                                    listview.setAdapter(adapter);*/
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
            queue.add(compareRequest); // Bu isteğimizi sıraya ekler ve her bu isteği gerçekleştirmek istediğimizde bu kuyruk sayesinde bu isteklerin takibi yapılır ve gerçekleştirilir.
    }

    public List<Kisi> kisiAra(List<Kisi> kisiler,String arama){ // Bu metot ana ekranda ki kişi listesinden editText üzerinden girilen kişi isimlerini arayarak uygun sonuçları döndürür ancak sonunçları listelemez.
        List<Kisi> sonuclar = new ArrayList<Kisi>(); // Kisi tipinde bir liste oluşturulur.
        for(int i=0;i<kisiler.size();i++){ // Listede ki kişi sayısınca bir döngü açılır. Çünkü listenin her elamanında arama yapacağız.
            Kisi kisi = kisiler.get(i); // Listenin her satırı kisi nesnesine aktarılır.
            kisi.getAd().trim(); // Tüm kisilerin arama yapmadan önce isimlerinin başında ki ve sonunda ki boşluklar silinir.
            String[] isim = kisi.getAd().split(" "); // Her kişinin isim ve soyisimleri parçalanarak diziye aktarılır.
            for(int j = 0; j<isim.length; j++){ // Her kişinin her ismi ve soyismi için arama yapılır.
                int index = isim[j].indexOf(arama); // indexOf metodu ile ilgili isimde kullanıcının aradığı harf varsa sonuç sıfır döndürülür.
                if(index == 0){ // index sonucu sıfır ise eşleşme var demektir. Yani aranılan kişi listededir.
                    sonuclar.add(kisi); // Bulduğumuz kişi listeye eklenir.
                    break; // Kişinin adı ve soyadı her hangi biri bulunduğu anda arama sonlandırılır.
                }
            }
        }

        return sonuclar; // Eşleşme olmassa boş liste. Aksi durumda eşleşen kişiler listeye eklenmiş olarak geri döndürülür.
    }

    private void ara() { // Bu metot da sunucu üzerinden arama yapmaktadır.
        kisiler.clear(); // Aramadan önce kişi listesi temizlenir.
        final RequestQueue queue = Volley.newRequestQueue(this); // Http için bir kütüphane
        final ListAdapter adapter = new ListAdapter(kisiler,this); // Listeyi ekranda görüntülemek için kullanılan bir sınıf
        String url = "http://10.0.2.2:8080/kisi-ara?isim="+isim; // Sunucumuzun adresi ve istek parametremiz
        StringRequest compareRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Sunucumuzun bizim isteğimize cevap sunduğu kısım
                        try { // Her ne olursa olsun program akışını bozmamak için en az bir sefer çalışacak blok
                                if(response.equals("Eslesme yok")) // Eğer arama sonucunda bir eşleşme yoksa boş liste döndürülerek ekranda hiçbir kişi listlenmez.
                                {
                                    kisiler.clear(); // Kişi listesi temizlenir.
                                    listview.setAdapter(adapter); // Ekranda boş liste döndürülür.
                                }else // Eğer eşleşme var ise aşağıda ki blok çalıştırılır.
                                {
                                    JSONArray jarray = new JSONArray(response); // Gelen string veri Json tipinde bir diziye aktarılır. Çünkü tüm elamanlara erişerek listeye aktarabilmemiz için.
                                    for(int i=0;i<jarray.length();i++) // Listede ki elemanın boyutunda bir döngü oluşturarak her elamanın verilerine erişeceğiz.
                                    {
                                        // Burada sırayla dizi de ki tüm kişilerin kişisel verilerine erişilir.
                                        String id = jarray.getJSONObject(i).getString("id").toString();
                                        String isim = jarray.getJSONObject(i).getString("isim").toString();
                                        isim = isim + " " + jarray.getJSONObject(i).getString("soyisim").toString();
                                        String mail = jarray.getJSONObject(i).getString("mail").toString();
                                        String numara = jarray.getJSONObject(i).getString("numara").toString();
                                        String cinsiyet = jarray.getJSONObject(i).getString("cinsiyet").toString();

                                        // Kişilerimizin verilerini kişi sınıfına aktarıyoruz. Çünkü listemiz kişi tipinde.
                                        Kisi kisi = new Kisi();
                                        kisi.setId(Integer.parseInt(id));
                                        kisi.setAd(isim);
                                        kisi.setCinsiyet(cinsiyet);
                                        kisi.setEmail(mail);
                                        kisi.setNumara(numara);

                                        kisiler.add(kisi); // Her kişi listeye eklenir
                                        listview.setAdapter(adapter); // Liste ise her seferinde ekranda gösterilir.
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
        queue.add(compareRequest); // İsteğimiz kuyruğa eklenir ve sırası geldiğinde çalışır.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){ // Sağ üstte ki artı ve üç noktanın olduğu menünün çalıştırılmasını sağlar.
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){ // Bu blok ise o menülerden hangisine tıkladığımızı ve tıkladığımızda neler olacağını belirler.

        int id = item.getItemId();
        if(id==R.id.ayarlar){ // Ayarlar menüsüne tıklanıldığında yapılacak işlemler
            return true;
        }else if(id == R.id.profil){ // Profil menüsüne tıklanıldığında yapılacak işlemler

            return true;
        }else if(id == R.id.ekleMenu){ // Kişi ekleme menüsüne tıklanıldığında yapılacak işlemler
            Intent intent = new Intent(MainActivity.this,KisiEkleActivity.class); // Kişi ekleme sayfası çalıştırılacak parametre olarak gösterilir.
            startActivityForResult(intent, 1); // Detaylar sayfasının açılmasını ve o sayfadan veri alambilmemizi sağlar.*/
            return true;
        }else{
            return true;
        }

    }


}
