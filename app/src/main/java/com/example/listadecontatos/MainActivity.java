package com.example.listadecontatos;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listadecontatos.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ContatoAdapter.OnContatoClickListener {
    private ActivityMainBinding binding;
    private ContatoAdapter contatoAdapter;
    private final ArrayList<Contato> listaDeContatos = new ArrayList<>();
    private final ArrayList<Contato> contatosFavoritos = new ArrayList<>();
    private boolean favorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton imgAddContato = binding.imgBtnAddContact;
        imgAddContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent telaAddContato = new Intent(MainActivity.this, CadastrarContato.class);
                startActivity(telaAddContato);
            }
        });

        ImageButton imgBtnFavorite = binding.imgBtnFavorite;
        imgBtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorites = !favorites;
                if(favorites){
                    imgBtnFavorite.setImageResource(R.drawable.favorite_white_true);
                    contatosFavoritos.clear();
                    for(Contato contato: listaDeContatos){
                        if(contato.isFavorite()){
                            contatosFavoritos.add(contato);
                        }
                    }
                    RecyclerView recyclerContatos = binding.recyclerView;
                    recyclerContatos.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerContatos.setHasFixedSize(true);

                    contatoAdapter = new ContatoAdapter(contatosFavoritos, MainActivity.this, MainActivity.this);
                    recyclerContatos.setAdapter(contatoAdapter);
                }
                else{
                    imgBtnFavorite.setImageResource(R.drawable.favorite_white_false);
                    buscarContatos();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        buscarContatos();
    }

    private void buscarContatos() {

        ApiContatos api = Constantes.RETROFIT.create(ApiContatos.class);
        listaDeContatos.clear();

        Call<List<Contato>> getContatosServer = api.getContatos();
        getContatosServer.enqueue(new Callback<List<Contato>>() {
            @Override
            public void onResponse(Call<List<Contato>> call, Response<List<Contato>> response) {
                if (response.isSuccessful()) {
                    List<Contato> contatos = response.body();
                    if (contatos != null) {
                        listaDeContatos.addAll(contatos);
                    } else {
                        Toast.makeText(MainActivity.this, "Erro ao buscar os dados", Toast.LENGTH_SHORT).show();
                    }

                    RecyclerView recyclerContatos = binding.recyclerView;
                    recyclerContatos.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerContatos.setHasFixedSize(true);

                    contatoAdapter = new ContatoAdapter(listaDeContatos, MainActivity.this, MainActivity.this);
                    recyclerContatos.setAdapter(contatoAdapter);

                }
            }

            @Override
            public void onFailure(Call<List<Contato>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Erro ao buscar contatos", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRemoverClick(int contatoId) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Exclusão de contato")
                .setMessage("Deseja realmente excluir o contato ?")
                .setNegativeButton("Excluir", ((dialog, which) -> {
                    excluirContato(contatoId);
                }))
                .setNeutralButton("Cancelar", ((dialog, which) -> {
                    buscarContatos();
                }))
                .show();
    }

    public void excluirContato(int contatoId) {

        ApiContatos api = Constantes.RETROFIT.create(ApiContatos.class);

        Call<Void> excluirContato = api.deletarContato(contatoId);
        excluirContato.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Contato excluído com sucesso !", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Houve um erro ao excluir o contato", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Erro ao comunicar com a Api", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onFavoritoClick(Contato contato) {
        atualizarFavorito(contato);
    }

    public void atualizarFavorito(Contato contato) {
        boolean novoEstado = !contato.isFavorite();
        contato.setFavorite(novoEstado);

        // Faça a chamada à API para atualizar o estado no servidor
        ApiContatos api = Constantes.RETROFIT.create(ApiContatos.class);
        Call<Void> call = api.atualizarContato(contato.getId(), contato);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if(contato.isFavorite()){
                        Toast.makeText(MainActivity.this, "Contato favoritado", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Contato desfavoritado", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Reverter o estado em caso de falha
                    contato.setFavorite(!novoEstado);
                    Toast.makeText(MainActivity.this, "Erro ao favoritar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Erro ao conectar com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
