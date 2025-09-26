package com.example.proyecto_presin_arterial;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class PacientesActivity extends AppCompatActivity {
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String, String>> pacientesList = new ArrayList<>();
    private PacienteDbHelper dbHelper;
    private ListView listView;
    private EditText etBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);
        listView = findViewById(R.id.listViewPacientes);
        etBuscar = findViewById(R.id.etBuscarPaciente);
        dbHelper = new PacienteDbHelper(this);

        // Inicializar el botón de descarga
        Button btnDescargar = findViewById(R.id.btnDescargar);
        btnDescargar.setOnClickListener(v -> {
            Toast.makeText(this, "Descargando información...", Toast.LENGTH_SHORT).show();
            // Aquí puedes agregar la lógica de descarga
        });

        cargarPacientes("");
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cargarPacientes(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarPacientes(String filtro) {
        pacientesList.clear();
        Cursor cursor = dbHelper.getReadableDatabase().query(
                PacienteDbHelper.TABLE_NAME,
                null,
                filtro.isEmpty() ? null : PacienteDbHelper.COLUMN_NOMBRE + " LIKE ?",
                filtro.isEmpty() ? null : new String[]{"%" + filtro + "%"},
                null, null, null
        );
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_NOMBRE)));
                map.put("edad", "Edad: " + cursor.getInt(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_EDAD)));
                map.put("genero", "Género: " + cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_GENERO)));
                map.put("contacto", "Contacto: " + cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_CONTACTO)));
                map.put("enfermedades", "Enfermedades: " + cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_ENFERMEDADES)));
                map.put("medicamentos", "Medicamentos: " + cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_MEDICAMENTOS)));
                map.put("presion", "Presión: " + cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_PRESION)));
                pacientesList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter = new SimpleAdapter(
                this,
                pacientesList,
                android.R.layout.simple_list_item_2,
                new String[]{"nombre", "presion"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> paciente = pacientesList.get(position);
            StringBuilder detalles = new StringBuilder();
            detalles.append(paciente.get("nombre")).append("\n");
            detalles.append(paciente.get("edad")).append("\n");
            detalles.append(paciente.get("genero")).append("\n");
            detalles.append(paciente.get("contacto")).append("\n");
            detalles.append(paciente.get("enfermedades")).append("\n");
            detalles.append(paciente.get("medicamentos")).append("\n");
            detalles.append(paciente.get("presion"));
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Datos del paciente")
                    .setMessage(detalles.toString())
                    .setPositiveButton("Cerrar", null)
                    .show();
        });
    }
}
