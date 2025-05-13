package com.example.listadecontatos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.listadecontatos.databinding.CadastrarContatoBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastrarContato extends AppCompatActivity implements View.OnClickListener {
    private CadastrarContatoBinding binding;
    private EditText nameConato;
    private EditText phoneContato;
    private int contatoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CadastrarContatoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton imgBtnVoltar = binding.imgBtnBack;
        imgBtnVoltar.setOnClickListener(this);

        Button btnSalvar = binding.btnSalvar;
        btnSalvar.setOnClickListener(this);

        // Recuperando os dados do contato para atualizar
        if (getIntent().hasExtra("contato")) {
            Contato contato = (Contato) getIntent().getSerializableExtra("contato");

            binding.editName.setText(contato.getName());
            binding.editPhone.setText(contato.getPhone());
            contatoId =  contato.getId();

            TextView tituloToolbar = findViewById(R.id.txtTituloToolBar);
            tituloToolbar.setText("Editar contato");
            btnSalvar.setText("Atualizar");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBtnBack){
            finish();
        }
        else if(v.getId() == R.id.btnSalvar){
            nameConato = binding.editName;
            phoneContato = binding.editPhone;

            Contato contato = new Contato(nameConato.getText().toString(),
                                              phoneContato.getText().toString(),
                                       false);

            if(contatoId == 0){
                adicionarContato(contato);
            }
            else{
                editarContato(contato);
            }
        }
    }

    private void adicionarContato(Contato contato) {

        ApiContatos api = Constantes.RETROFIT.create(ApiContatos.class);

        Call<Contato> adicionarContato = api.criarContato(contato);
        adicionarContato.enqueue(new Callback<Contato>() {
            @Override
            public void onResponse(Call<Contato> call, Response<Contato> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CadastrarContato.this, "Contato Salvo com sucesso !", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(CadastrarContato.this, "Erro ao salvar contato", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Contato> call, Throwable throwable) {
                Toast.makeText(CadastrarContato.this, "Erro de comunicação API", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void editarContato(Contato contato) {

        ApiContatos api = Constantes.RETROFIT.create(ApiContatos.class);

        Call<Void> editarContato = api.atualizarContato(contatoId, contato);
        editarContato.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CadastrarContato.this, "Contato atualizado com sucesso !", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(CadastrarContato.this, "Houve um erro ao atualizar o contato", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Toast.makeText(CadastrarContato.this, "Erro ao comunicar com a Api", Toast.LENGTH_LONG).show();
            }
        });
    }
}
