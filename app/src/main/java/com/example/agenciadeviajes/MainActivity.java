package com.example.agenciadeviajes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText jetCodigoV, jetNombreU;
    RadioButton jrbprimeraC, jrbEjecutiva, jrbEconomica;
    Boolean respuesta;
    CheckBox jcbactivo;
    String Codigo, Nombre, vuelos, activo, Ident_Doc;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        jetCodigoV = findViewById(R.id.etCodigoV);
        jetNombreU = findViewById(R.id.etNombreU);
        jrbprimeraC = findViewById(R.id.rbPrimeraC);
        jrbEjecutiva = findViewById(R.id.rbEjecutiva);
        jrbEconomica = findViewById(R.id.rbEconomica);
        jcbactivo = findViewById(R.id.cbactivo);
    }

    public void Adicionar(View view) {
        Codigo = jetCodigoV.getText().toString();
        Nombre = jetNombreU.getText().toString();

        if (Codigo.isEmpty() || Nombre.isEmpty()) {
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetCodigoV.requestFocus();
        } else {
            if (jrbprimeraC.isChecked())
                vuelos = "primera clase";
            else if (jrbEjecutiva.isChecked())
                vuelos = "clase ejecutiva";

            else
                vuelos = "clase economica";

            Map<String, Object> pelicula = new HashMap<>();
            pelicula.put("Codigo", Codigo);
            pelicula.put("Nombre", Nombre);
            pelicula.put("vuelos", vuelos);
            pelicula.put("activo", "si");

// Add a new document with a generated ID
            db.collection("Agencia de viajes")
                    .add(pelicula)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(MainActivity.this, "vuelo registrado", Toast.LENGTH_SHORT).show();
                            Limpiar(view);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //  Log.w(TAG, "Error adding document", e);
                            Toast.makeText(MainActivity.this, "Error al adicionar", Toast.LENGTH_SHORT).show();
                        }
                    });


        }

    }

    public void Consultar(View view) {
        Buscar_Pelicula();

    }

    public void Buscar_Pelicula() {
        respuesta = false;
        Codigo = jetCodigoV.getText().toString();
        if (Codigo.isEmpty()) {
            Toast.makeText(this, "El codigo es necesario", Toast.LENGTH_SHORT).show();
            jetCodigoV.requestFocus();
        } else {
            db.collection("Agencia de viajes")
                    .whereEqualTo("Codigo",Codigo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    respuesta=true;
                                    if (document.getString("activo").equals("no")){
                                        Toast.makeText(MainActivity.this, "El documento existe pero no está activo", Toast.LENGTH_SHORT).show();
                                        Limpiar();
                                    }
                                    else{
                                        Ident_Doc=document.getId();
                                        jetNombreU.setText(document.getString("Nombre"));
                                        jetCodigoV.setText(document.getString("Codigo"));
                                        if (document.getString("vuelos").equals("primera clase"))
                                            jrbprimeraC.setChecked(true);
                                        else
                                        if (document.getString("vuelos").equals("clase ejecutiva"))
                                            jrbEjecutiva.setChecked(true);
                                        else
                                            jrbEconomica.setChecked(true);
                                        if (document.getString("activo").equals("si"))
                                            jcbactivo.setChecked(true);
                                        else
                                            jcbactivo.setChecked(false);
                                        //  Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                }
                            }
                        }
                    });

        }
    }



    public void Anular (View view){
        Codigo=jetCodigoV.getText().toString();
        Nombre=jetNombreU.getText().toString();

        if (Codigo.isEmpty()) {
            Toast.makeText(this, "El codigo es requerido", Toast.LENGTH_SHORT).show();
            jetCodigoV.requestFocus();

        }
        else{
            if (respuesta==true){
                if (jrbprimeraC.isChecked())
                    vuelos="primera clase";
                else if(jrbEjecutiva.isChecked())
                    vuelos="clase ejecutiva";
                else
                    vuelos="clase economica";

                Map<String, Object> vuelosa = new HashMap<>();
                vuelosa.put("Codigo", Codigo);
                vuelosa.put("Nombre", Nombre);
                vuelosa.put("vuelos", vuelos);
                vuelosa.put("activo", "no");

                db.collection("Agencia de viajes").document(Ident_Doc)
                        .set(vuelosa)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "vuelo anulada", Toast.LENGTH_SHORT).show();
                                Limpiar();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error al anular", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
            else{
                Toast.makeText(this, "Debe primero consultar", Toast.LENGTH_SHORT).show();
                jetCodigoV.requestFocus();
            }
        }

    }

    private void Limpiar(){
        jetCodigoV.setText("");
        jetNombreU.setText("");
        jrbprimeraC.setChecked(true);
        //jcbactivo.setChecked(false);
        jetCodigoV.requestFocus();
        respuesta=false;
    }



    public void Limpiar(View view){
        jetCodigoV.setText("");
        jetNombreU.setText("");
        jrbprimeraC.setChecked(true);
        //jcbactivo.setChecked(false);
        jetCodigoV.requestFocus();
        respuesta=false;
    }
}
