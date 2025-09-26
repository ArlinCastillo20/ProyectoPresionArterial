package com.example.proyecto_presin_arterial;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.Button;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class pacientes extends AppCompatActivity {
    private PacienteDbHelper dbHelper;
    private ListView listView;
    private ArrayList<HashMap<String, String>> listaPacientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pacientes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new PacienteDbHelper(this);
        listView = findViewById(R.id.listViewPacientes);
        listaPacientes = new ArrayList<>();

        Button btnDescargar = findViewById(R.id.btnDescargar);
        btnDescargar.setOnClickListener(v -> descargarInformacion());

        cargarListaPacientes();
    }

    private void cargarListaPacientes() {
        listaPacientes.clear();
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query(PacienteDbHelper.TABLE_NAME,
                     null, null, null, null, null, null)) {

            if (cursor.getCount() == 0) {
                Toast.makeText(this, "No hay pacientes registrados", Toast.LENGTH_SHORT).show();
                return;
            }

            while (cursor.moveToNext()) {
                HashMap<String, String> paciente = new HashMap<>();
                paciente.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_NOMBRE)));
                paciente.put("edad", cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_EDAD)));
                paciente.put("genero", cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_GENERO)));
                paciente.put("contacto", cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_CONTACTO)));
                paciente.put("enfermedades", cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_ENFERMEDADES)));
                paciente.put("medicamentos", cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_MEDICAMENTOS)));
                paciente.put("presion", cursor.getString(cursor.getColumnIndexOrThrow(PacienteDbHelper.COLUMN_PRESION)));
                listaPacientes.add(paciente);
            }

            SimpleAdapter adapter = new SimpleAdapter(
                this,
                listaPacientes,
                android.R.layout.simple_list_item_2,
                new String[]{"nombre", "presion"},
                new int[]{android.R.id.text1, android.R.id.text2}
            );

            listView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar pacientes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void descargarInformacion() {
        if (listaPacientes.isEmpty()) {
            Toast.makeText(this, "No hay datos para descargar", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!directorio.exists()) {
                boolean creado = directorio.mkdirs();
                if (!creado) {
                    Toast.makeText(this, "No se pudo crear el directorio", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File archivo = new File(directorio, "historial_pacientes_" + timeStamp + ".txt");

            FileWriter writer = new FileWriter(archivo);
            writer.write("=== HISTORIAL DE PACIENTES ===\n");
            writer.write("Fecha de generación: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n\n");

            for (HashMap<String, String> paciente : listaPacientes) {
                writer.write("----------------------------------------\n");
                writer.write("DATOS DEL PACIENTE:\n");
                writer.write("Nombre: " + paciente.get("nombre") + "\n");
                writer.write("Edad: " + paciente.get("edad") + " años\n");
                writer.write("Género: " + paciente.get("genero") + "\n");
                writer.write("Contacto: " + paciente.get("contacto") + "\n");
                writer.write("Enfermedades: " + paciente.get("enfermedades") + "\n");
                writer.write("Medicamentos: " + paciente.get("medicamentos") + "\n");
                String presion = paciente.get("presion");
                writer.write("Presión Arterial: " + (presion != null && !presion.isEmpty() ? presion : "No registrada") + "\n");
                writer.write("----------------------------------------\n\n");
            }

            writer.close();

            Toast.makeText(this, "Archivo guardado en:\n" + archivo.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(this, "Error al escribir archivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (SecurityException e) {
            Toast.makeText(this, "Error de permisos: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}