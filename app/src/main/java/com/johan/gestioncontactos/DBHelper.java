package com.johan.gestioncontactos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.ArrayList;
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "contactos.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE contactos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "pais TEXT," +
                        "nombre TEXT," +
                        "telefono TEXT," +
                        "nota TEXT," +
                        "imagen TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS contactos");

        onCreate(db);
    }

    public boolean insertarContacto(Contacto contacto){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("pais", contacto.getPais());
        values.put("nombre", contacto.getNombre());
        values.put("telefono", contacto.getTelefono());
        values.put("nota", contacto.getNota());
        values.put("imagen", contacto.getImagen());

        long resultado = db.insert(
                "contactos",
                null,
                values);

        return resultado != -1;
    }
    public ArrayList<Contacto> obtenerContactos(){

        ArrayList<Contacto> lista = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM contactos",
                null);

        while(cursor.moveToNext()){

            Contacto contacto = new Contacto();

            contacto.setId(cursor.getInt(0));
            contacto.setPais(cursor.getString(1));
            contacto.setNombre(cursor.getString(2));
            contacto.setTelefono(cursor.getString(3));
            contacto.setNota(cursor.getString(4));
            contacto.setImagen(cursor.getString(5));

            lista.add(contacto);
        }

        cursor.close();

        return lista;
    }
    public void eliminarContacto(int id){

        SQLiteDatabase db =
                this.getWritableDatabase();

        db.delete(
                "contactos",
                "id=?",
                new String[]{
                        String.valueOf(id)
                });
    }
    public boolean actualizarContacto(Contacto c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", c.getNombre());
        values.put("telefono", c.getTelefono());
        values.put("pais", c.getPais());
        values.put("imagen", c.getImagen());
        values.put("nota", c.getNota());

        int filas = db.update("contactos", values, "id = ?",
                new String[]{String.valueOf(c.getId())});
        db.close();
        return filas > 0;
    }
}


