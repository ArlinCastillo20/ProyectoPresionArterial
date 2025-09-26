package com.example.proyecto_presin_arterial;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configuración del botón Comenzar
        Button btnComenzar = findViewById(R.id.button);
        btnComenzar.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(MainActivity.this, com.example.proyecto_presin_arterial.formulario.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error al abrir el formulario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        // Configuración del botón Lista de Pacientes
        Button btnListaPacientes = findViewById(R.id.button_lista_pacientes);
        btnListaPacientes.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(MainActivity.this, pacientes.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error al abrir la lista de pacientes: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }
}
