package com.example.listadecontatos;


import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listadecontatos.databinding.ContatoItemBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;

public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.ContatoViewHolder>{
    private final ArrayList<Contato> listaDeContatos;
    private OnContatoClickListener listener;
    private final Context context;
    private boolean listFavorite;

    public ContatoAdapter(ArrayList<Contato> listaDeContatos, Context context, OnContatoClickListener listener, boolean listFavorite) {
        this.listaDeContatos = listaDeContatos;
        this.context = context;
        this.listener = listener;
        this.listFavorite = listFavorite;
    }


    @NonNull
    @Override
    public ContatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContatoItemBinding contato;
        contato = ContatoItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ContatoViewHolder(contato);
    }

    @Override
    public void onBindViewHolder(@NonNull ContatoViewHolder holder, int position) {
        Contato contato = listaDeContatos.get(position);
        holder.binding.txtNomeContato.setText(contato.getName());
        holder.binding.txtNumero.setText(contato.getPhone());
        // Ajuste a altura conforme necessário
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT; // ou um valor específico em pixels
        holder.itemView.setLayoutParams(params);

        if (contato.isFavorite()) {
            holder.binding.imgBtnFavoritar.setImageResource(R.drawable.favorite_star_true);
        } else {
            holder.binding.imgBtnFavoritar.setImageResource(R.drawable.favorite_star_false);
        }

        holder.binding.imgBtnFavoritar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(holder.getAdapterPosition());
                listener.onFavoritoClick(contato);
                if(listFavorite){
                    int position = holder.getAdapterPosition();
                    listaDeContatos.remove(position);
                }
            }
        });

        holder.binding.imgBtnChamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroTelefone = contato.getPhone();
                if (numeroTelefone != null && !numeroTelefone.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + "+55" + numeroTelefone));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Número de telefone inválido.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.binding.imgBtnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent telaAtualizar = new Intent(context, CadastrarContato.class);
                telaAtualizar.putExtra("contato", contato);
                context.startActivity(telaAtualizar);
            }
        });

        holder.binding.imgBtnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRemoverClick(contato.getId());
                    listaDeContatos.remove(position);
                    notifyItemRemoved(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaDeContatos.size();
    }

    public static class ContatoViewHolder extends RecyclerView.ViewHolder{
        ContatoItemBinding binding;
        public ContatoViewHolder(ContatoItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnContatoClickListener {
        void onRemoverClick(int contatoId);
        void onFavoritoClick(Contato contato);
    }

    public void removerItem(int position) {
        if (position >= 0 && position < listaDeContatos.size()) {
            Contato contatoRemovido = listaDeContatos.get(position);
            listaDeContatos.remove(position);
            notifyItemRemoved(position);
            listener.onRemoverClick(contatoRemovido.getId());
        }
    }
}
