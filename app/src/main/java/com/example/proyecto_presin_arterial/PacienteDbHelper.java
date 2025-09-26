package com.example.proyecto_presin_arterial;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PacienteDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "pacientes.db";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "paciente";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_EDAD = "edad";
    public static final String COLUMN_GENERO = "genero";
    public static final String COLUMN_CONTACTO = "contacto";
    public static final String COLUMN_ENFERMEDADES = "enfermedades";
    public static final String COLUMN_MEDICAMENTOS = "medicamentos";
    public static final String COLUMN_PRESION = "presion_arterial";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT, " +
                    COLUMN_EDAD + " INTEGER, " +
                    COLUMN_GENERO + " TEXT, " +
                    COLUMN_CONTACTO + " TEXT, " +
                    COLUMN_ENFERMEDADES + " TEXT, " +
                    COLUMN_MEDICAMENTOS + " TEXT, " +
                    COLUMN_PRESION + " TEXT)";

    public PacienteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_PRESION + " TEXT");
        }
    }

    public long insertarPaciente(String nombre, int edad, String genero, String contacto,
                               String enfermedades, String medicamentos, String presion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_EDAD, edad);
        values.put(COLUMN_GENERO, genero);
        values.put(COLUMN_CONTACTO, contacto);
        values.put(COLUMN_ENFERMEDADES, enfermedades);
        values.put(COLUMN_MEDICAMENTOS, medicamentos);
        values.put(COLUMN_PRESION, presion);

        return db.insert(TABLE_NAME, null, values);
    }

    // Método para verificar si un paciente ya existe
    public boolean existePaciente(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_NOMBRE + " = ?";
        android.database.Cursor cursor = db.rawQuery(query, new String[]{nombre});

        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        cursor.close();
        return false;
    }
}
