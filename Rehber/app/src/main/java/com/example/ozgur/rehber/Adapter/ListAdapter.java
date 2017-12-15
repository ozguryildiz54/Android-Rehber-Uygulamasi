package com.example.ozgur.rehber.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ozgur.rehber.Activities.DetaylarActivity;
import com.example.ozgur.rehber.Model.Kisi;
import com.example.ozgur.rehber.R;

import java.util.List;

/**
 * Created by Özgür on 3.09.2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListeHolder> {

    private List<Kisi> mKisiler;
    private Context mContext;
    private Activity activity;

    public ListAdapter(List<Kisi> kisiler, Context context){ // Bu sınıfın yapılandırıcı metodu
        mKisiler = kisiler;
        mContext = context; // Gelen sayfa bilgisini alır.
    }

    @Override
    public ListeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View v = layoutInflater.inflate(R.layout.activity_listele,parent,false);
        return new ListeHolder(v,mContext);
    }

    @Override
    public void onBindViewHolder(ListeHolder holder, int position) {
        Kisi kisi = mKisiler.get(position);
        holder.bindHolder(kisi);
    }

    @Override
    public int getItemCount() {
        return mKisiler.size();
    }
}

class ListeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView ad;
    private TextView mail;
    private TextView no;
    private ImageView cinsiyet;
    private Kisi mkisi;
    private Context mContext;

    public ListeHolder(View itemView,Context context) {
        super(itemView);
        itemView.setOnClickListener(this);

        // EditText komponentleri
        ad = (TextView) itemView.findViewById(R.id.adSoyad);
        mail = (TextView) itemView.findViewById(R.id.mail);
        no = (TextView) itemView.findViewById(R.id.no);
        cinsiyet = (ImageView) itemView.findViewById(R.id.image);
        mContext = context;
    }

    public void bindHolder(Kisi kisi) {
        ad.setText(kisi.getAd());
        mail.setText(kisi.getEmail());
        no.setText(kisi.getNumara());

        mkisi = kisi;

        if(kisi.getCinsiyet().equals("Erkek")){
            cinsiyet.setImageResource(R.drawable.man);
        }
        else{
            cinsiyet.setImageResource(R.drawable.women);
        }
    }

    @Override
    public void onClick(View view) { // Liste üzerinde tıklanıldığında yapılacak işlemler.
        Intent i = new Intent(mContext, DetaylarActivity.class);
        i.putExtra("id",mkisi.getId()); // tıklanıldığında id bilgisi "id" parametresi ile gönderilir.
        ((Activity) mContext).startActivityForResult(i,2); // Detaylar sayfası açılır. Parametre olarak verilen 2 değeri ise detaylar sayfasından geldiğimizi anlamak için yazılmıştır.
        // Bunun kontrolü ise main sınıfında activityForResult bloğunda yapılır

    }
}

