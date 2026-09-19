package com.example.applistas;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class BuscadorPersonaje extends AppCompatActivity {

    RequestQueue requestQueue;
    final String URL = "https://dragonball-api.com/api/characters/";

    EditText edtIdPersonaje, edtNombre, edtKi, edtRaza, edtGenero;
    Button btnBuscarPersonaje;

    private void loadUI(){
        edtIdPersonaje = findViewById(R.id.edtIdPersonaje);
        btnBuscarPersonaje = findViewById(R.id.btnBuscarPersonaje);
        edtNombre = findViewById(R.id.edtNombre);
        edtGenero = findViewById(R.id.edtGenero);
        edtRaza = findViewById(R.id.edtRaza);
        edtKi = findViewById(R.id.edtKi);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscador_personaje);

        this.loadUI();

        //Eventos
        btnBuscarPersonaje.setOnClickListener(v -> {getDataCharacter();});
    }

    private void getDataCharacter() {
        //Comunicaion Dragon Ball API
        if(edtIdPersonaje.getText().toString().isEmpty()){
            edtIdPersonaje.setError("Escriba un ID");
            edtIdPersonaje.requestFocus();
            return;
        }

        String endPoint = URL + edtIdPersonaje.getText().toString();

        //Abrir canal de comunicacion

        requestQueue = Volley.newRequestQueue(this);

        //¿Que tipo de dato me devuelve el API?
        //Volley las solicitudes tienen 5 partes;
        //Volley, URL, JSONEnviado, Resultado,(JSONObject), Error
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                endPoint,
                null,
                this::showData,
                this::errorWS
        );

        //Enviamos la solicitud
        requestQueue.add(jsonObjectRequest);
    }

    private void showData(JSONObject jsonObject) {
        Log.d("ResultadoWS", jsonObject.toString());

        try {
            // 1. Extraer los datos de forma segura usando optString en lugar de getString
            // optString evita que la app se caiga (crash) si el campo no viene en el JSON
            String nombre = jsonObject.optString("name", "No disponible");
            String ki = jsonObject.optString("ki", "0");
            String raza = jsonObject.optString("race", "Desconocida");
            String genero = jsonObject.optString("gender", "No especificado");

            // 2. Asignar los valores a la interfaz de usuario (UI)
            edtNombre.setText(nombre);
            edtKi.setText(ki);
            edtRaza.setText(raza);
            edtGenero.setText(genero);

        } catch (Exception e) {
            Log.e("ResultadoWS", "Error al procesar los datos del personaje", e);
            Toast.makeText(this, "Error al cargar la información", Toast.LENGTH_SHORT).show();
        }
    }

    private void errorWS(VolleyError volleyError) {

        Log.e("ErrorWS", volleyError.toString());

        //Para gestionar errores, necesitamos de un objeto
        NetworkResponse response = volleyError.networkResponse;

        //Si exitste una respuesta
        if(response != null && response.data != null){
            //Cual es el codigo de error
            int statusCode = response.statusCode;

            //No lo encontramos
            if(statusCode == 400){
                String dataError = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(dataError);
                    Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    Log.e("ErrorWS", dataError);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}