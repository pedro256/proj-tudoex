package com.app.tudoex.adapter;

import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tudoex.R;
import com.app.tudoex.models.Anuncio;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {


    private List<Anuncio> anuncios;
    private Context context;

    public AdapterAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio,parent,false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Anuncio anuncio = anuncios.get(position);

        holder.titulo.setText(anuncio.getTitulo());
        holder.valor.setText(anuncio.getValor());
        //pegar primeira imagem anuncio
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);
        Picasso.get().load(urlCapa).into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView valor;
        ImageView foto;

        public MyViewHolder(View itemView){
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTituloAnucio);
            valor = itemView.findViewById(R.id.txtValorAnuncio);
            foto = itemView.findViewById(R.id.imgAnuncio);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();

                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        Log.e("Erro aqui:","Clicou");
                        Anuncio anuncioSelecionado = anuncios.get(pos);
                        anuncioSelecionado.remover();

                        Toast.makeText(v.getContext(), "Você apagou um anúncio" + anuncioSelecionado.getTitulo(), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });


        }
    }
}
