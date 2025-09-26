package com.example.proyecto_presin_arterial;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class formulario extends AppCompatActivity {
    private PacienteDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new PacienteDbHelper(this);

        EditText etNombre = findViewById(R.id.etNombre);
        EditText etEdad = findViewById(R.id.etEdad);
        RadioGroup rgGenero = findViewById(R.id.rgGenero);
        EditText etContacto = findViewById(R.id.etContacto);
        EditText etEnfermedades = findViewById(R.id.etEnfermedades);
        EditText etMedicamentos = findViewById(R.id.etMedicamentos);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> {
            // Obtener los valores de los campos
            String nombre = etNombre.getText().toString().trim();
            String edadStr = etEdad.getText().toString().trim();
            String contacto = etContacto.getText().toString().trim();
            String enfermedades = etEnfermedades.getText().toString().trim();
            String medicamentos = etMedicamentos.getText().toString().trim();

            // Obtener el género seleccionado
            int selectedGeneroId = rgGenero.getCheckedRadioButtonId();
            String genero = "";
            if (selectedGeneroId != -1) {
                RadioButton rbGenero = findViewById(selectedGeneroId);
                genero = rbGenero.getText().toString().trim();
            }

            // Validación de campos
            if (nombre.isEmpty() || edadStr.isEmpty() || genero.isEmpty() ||
                contacto.isEmpty() || enfermedades.isEmpty() || medicamentos.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int edad = Integer.parseInt(edadStr);

                // Guardar en la base de datos
                long resultado = dbHelper.insertarPaciente(
                    nombre,
                    edad,
                    genero,
                    contacto,
                    enfermedades,
                    medicamentos,
                    "" // La presión se agregará después
                );

                if (resultado != -1) {
                    Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();

                    // Guardar timestamp del último registro
                    getSharedPreferences("registro_paciente", MODE_PRIVATE)
                        .edit()
                        .putLong("ultimo_registro", System.currentTimeMillis())
                        .apply();

                    // Ir a la pantalla de resultado_presion
                    Intent intent = new Intent(this, resultado_presion.class);
                    intent.putExtra("PACIENTE_ID", resultado);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "La edad debe ser un número válido", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
