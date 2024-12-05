package com.example.agendaav2;

import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar a base de dados
        db = openOrCreateDatabase("agenda.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS contatos (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, fone TEXT);");

        // Configurar interface inicial
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        telaPrincipal();
    }

    private void telaPrincipal() {
        setContentView(R.layout.activity_main);

        EditText etNome = findViewById(R.id.etNomeCad);
        EditText etFone = findViewById(R.id.etFoneCad);
        Button btGravar = findViewById(R.id.btGravarCad);
        Button btConsultar = findViewById(R.id.btConsultarCad);
        Button btFechar = findViewById(R.id.btFechar);

        btGravar.setOnClickListener(v -> {
            String nome = etNome.getText().toString();
            String fone = etFone.getText().toString();

            if (nome.isEmpty() || fone.isEmpty()) {
                msg("Preencha todos os campos!");
            } else {
                db.execSQL("INSERT INTO contatos (nome, fone) VALUES (?, ?);", new Object[]{nome, fone});
                msg("Contato salvo com sucesso!");
                etNome.setText("");
                etFone.setText("");
            }
        });

        btConsultar.setOnClickListener(v -> telaconsulta());

        btFechar.setOnClickListener(v -> finish());
    }

    private void telaconsulta() {
        setContentView(R.layout.consulta);

        TextView tvNome = findViewById(R.id.tvNomeCon);
        TextView tvFone = findViewById(R.id.tvFoneCon);
        Button btAnterior = findViewById(R.id.btAnteCon);
        Button btProximo = findViewById(R.id.btProxCon);
        Button btVoltar = findViewById(R.id.btVoltarCon);

        Cursor cursor = db.rawQuery("SELECT * FROM contatos;", null);
        if (cursor.moveToFirst()) {
            tvNome.setText(cursor.getString(cursor.getColumnIndex("nome")));
            tvFone.setText(cursor.getString(cursor.getColumnIndex("fone")));
        }

        btAnterior.setOnClickListener(v -> {
            if (cursor.moveToPrevious()) {
                tvNome.setText(cursor.getString(cursor.getColumnIndex("nome")));
                tvFone.setText(cursor.getString(cursor.getColumnIndex("fone")));
            } else {
                msg("Nenhum registro anterior!");
            }
        });

        btProximo.setOnClickListener(v -> {
            if (cursor.moveToNext()) {
                tvNome.setText(cursor.getString(cursor.getColumnIndex("nome")));
                tvFone.setText(cursor.getString(cursor.getColumnIndex("fone")));
            } else {
                msg("Nenhum próximo registro!");
            }
        });

        btVoltar.setOnClickListener(v -> {
            cursor.close();
            telaPrincipal();
        });
    }

    private void msg(String txt) {
        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
        adb.setMessage(txt);
        adb.setNeutralButton("OK", null);
        adb.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            msg("Configurações não implementadas!");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
