package com.example.proyecto_presin_arterial;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class resultado_presion extends AppCompatActivity {
    private PacienteDbHelper dbHelper;
    private long pacienteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_presion);

        dbHelper = new PacienteDbHelper(this);

        // Obtener el ID del paciente del intent
        pacienteId = getIntent().getLongExtra("PACIENTE_ID", -1);

        EditText etPresion = findViewById(R.id.etPresion);
        Button btnAnalizarPresion = findViewById(R.id.btnAnalizarPresion);
        TextView tvResultado = findViewById(R.id.tvResultado);
        TextView tvRecomendaciones = findViewById(R.id.tvRecomendaciones);
        Button btnLlamarEmergencias = findViewById(R.id.btnLlamarEmergencias);
        Button btnMostrarPacientes = findViewById(R.id.btnMostrarPacientes);

        btnLlamarEmergencias.setVisibility(View.GONE);
        tvRecomendaciones.setVisibility(View.GONE);

        btnLlamarEmergencias.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_DIAL);
            intent.setData(android.net.Uri.parse("tel:911"));
            startActivity(intent);
        });

        btnMostrarPacientes.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, pacientes.class);
            startActivity(intent);
            finish();
        });

        btnAnalizarPresion.setOnClickListener(v -> {
            String presion = etPresion.getText().toString().trim();
            if (presion.isEmpty()) {
                tvResultado.setText("Por favor, ingresa la presión arterial.");
                tvRecomendaciones.setVisibility(View.GONE);
                btnLlamarEmergencias.setVisibility(View.GONE);
                return;
            }

            String resultado = "";
            String recomendaciones = "";
            boolean mostrarBotonEmergencia = false;

            try {
                // Analizar presión arterial ingresada (formato 120/80)
                String[] partes = presion.split("/");
                int sistolica = Integer.parseInt(partes[0].trim());
                int diastolica = Integer.parseInt(partes[1].trim());

                if (sistolica < 90 || diastolica < 60) {
                    resultado = "Presión baja";
                    recomendaciones = "Toma agua, recuéstate y eleva las piernas. Si tienes síntomas graves, llama a emergencias.";
                    mostrarBotonEmergencia = true;
                } else if (sistolica > 140 || diastolica > 90) {
                    resultado = "Presión alta";
                    recomendaciones = "Relájate, siéntate y respira profundo. Evita el estrés y consulta a tu médico. Si tienes síntomas graves, llama a emergencias.";
                    mostrarBotonEmergencia = true;
                } else {
                    resultado = "Presión normal";
                    recomendaciones = "Mantén hábitos saludables y monitorea tu presión regularmente.";
                    mostrarBotonEmergencia = false;
                }

                // Guardar la presión y el resultado en la base de datos
                guardarPresion(presion, resultado);

            } catch (Exception e) {
                resultado = "Formato incorrecto. Usa el formato 120/80";
                recomendaciones = "";
                mostrarBotonEmergencia = false;
            }

            tvResultado.setText(resultado);
            tvRecomendaciones.setText(recomendaciones);
            tvRecomendaciones.setVisibility(View.VISIBLE);
            btnLlamarEmergencias.setVisibility(mostrarBotonEmergencia ? View.VISIBLE : View.GONE);
        });
    }

    private void guardarPresion(String presion, String estado) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PacienteDbHelper.COLUMN_PRESION, presion + " - " + estado);

            // Si tenemos el ID del paciente, actualizamos su registro
            if (pacienteId != -1) {
                String whereClause = PacienteDbHelper.COLUMN_ID + " = ?";
                String[] whereArgs = {String.valueOf(pacienteId)};
                db.update(PacienteDbHelper.TABLE_NAME, values, whereClause, whereArgs);
            }

            Toast.makeText(this, "Presión arterial guardada correctamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar la presión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
