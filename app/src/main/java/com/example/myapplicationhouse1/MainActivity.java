package com.example.myapplicationhouse1;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("Personas");

    String datos;

    ArrayList<ObjetoPersona> listaPersonas = new ArrayList<ObjetoPersona>();

    public FirebaseAuth mAuth;

    EditText editName;
    EditText editSurname;
    Intent Pagina2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //editName=findViewById(R.id.editTextNombre);
        editSurname=findViewById(R.id.editTextSurname);
        Button botonLeer=findViewById(R.id.buttonLeer);
        Button botonGuardar=findViewById(R.id.buttonGuardar);
        Button botonLogin=findViewById(R.id.buttonLogin);
        Button botonSingIn=findViewById(R.id.buttonSingIn);
        Button botonAnonimo=findViewById(R.id.buttonAnonimo);
        TextView textAreaLectura=findViewById(R.id.textViewLeer);

        //share preferences
        SharedPreferences prefs = getSharedPreferences ("MisPreferencias", Context.MODE_PRIVATE); //Inicia shared preferences
        String correoSug = prefs.getString("email", "");
        String[] sugerencias= {correoSug};

        AutoCompleteTextView editName = findViewById(R.id.editTextNombre);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        if(!(sugerencias[0].equals(""))) // Si el ultimo email no es ""( es decir el predeterminado, porque no se ha introducido uno), se introduce el email
            adapter.add(sugerencias[0]);
        editName.setAdapter(adapter);




        Pagina2 = new Intent(this, ActivityPagina2.class);

        mAuth = FirebaseAuth.getInstance();




        botonLeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                        datos="";
                        Log.v(TAG, "Este es un mensaje de registro con nivel INFO rstio drdr");
                        for (QueryDocumentSnapshot document: querySnapshot){
                            ObjetoPersona personaLeer = new ObjetoPersona();

                            personaLeer.setNombreObjeto(document.getString("nombre"));
                            personaLeer.setApellidoObjeto(document.getString("surname"));

                            listaPersonas.add(personaLeer);


                        }

                        for(int i=0;i<listaPersonas.size();i++){
                            datos+= "Nombre: "+listaPersonas.get(i).getNombreObjeto()+"\n"+"Apellido: "+listaPersonas.get(i).getApellidoObjeto()+"\n\n";
                        }

                        textAreaLectura.setText(datos);
                        listaPersonas.clear();
                    }

                });

            }
        });






        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Aquí colocas el código que se ejecutará cuando se haga clic en el botón.
                if(!editName.getText().toString().equals("") && !editSurname.getText().toString().equals("")){ //si la reseña no esta vacia, recoje los datos
                    ObjetoPersona personaGuardar = new ObjetoPersona();
                    personaGuardar.setNombreObjeto(editName.getText().toString());
                    personaGuardar.setApellidoObjeto(editSurname.getText().toString());


                    db.collection("Personas")//Entra en la coleccion, crea el documento y pone la informacion dentro
                            .document("Persona" + new Date())
                            .set(personaGuardar)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    editName.setText("");
                                    editSurname.setText("");


                                }

                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Error al guardar los datos
                                }
                            });
                }
                else{
                    Log.v(TAG, "Alguno de los campos esta vacio");
                }
            }
        });









        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    // Obtén el correo electrónico y la contraseña ingresados por el usuario
                    String email = editName.getText().toString();
                    String password = editSurname.getText().toString();

                    // Intenta autenticar al usuario utilizando Firebase Authentication
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.v(TAG, "login correcto, cargando...");
                                        Pagina2.putExtra("variableMensaje", "LoGIN CORECTO"); // Envia el correo al menu principal
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SharedPreferences.Editor editor = prefs.edit(); // Crea el editor
                                                editor.putString("email", email); // Introduce el email
                                                editor.commit(); // Guarda los cambios

                                                try {
                                                    // Pausa el hilo durante 3 segundos (3000 milisegundos)
                                                    Thread.sleep(3000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                startActivity(Pagina2);
                                                // Después de la pausa, puedes realizar más operaciones en este hilo
                                            }
                                        }).start();
                                        //Si alguno de los campos esta vacio
                                    } else if (TextUtils.isEmpty(editName.toString()) || TextUtils.isEmpty(editSurname.toString())) {
                                        Log.v(TAG, "ERROR: alguno de los campos esta vacio");
                                    } else {
                                        editName.setText("");
                                        editSurname.setText("");
                                        Log.v(TAG, "ERROR: contraseña o correo incorrectos");
                                    }
                                }
                            });
                }catch (Exception e){
                    Log.v(TAG, "ERROR: alguno de los campos esta vacio"); //Muestra en PopUp señalando el error
                }
            }
        });








        botonAnonimo.setOnClickListener(new View.OnClickListener() { // Crea una instancia anonima
            @Override
            public void onClick(View v) {
                editName.setText("");
                editSurname.setText("");
                signInAnonymously(mAuth); // Inicia sesion de forma anonima
                Log.v(TAG, "Has elegido el modo anónimo, redirigiendo a la página principal");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000); // Pausa el hilo durante 3 segundos (3000 milisegundos)
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Pagina2.putExtra("variableMensaje", "SING IN ANONIMO CORECTO");
                        startActivity(Pagina2);
                        // Después de la pausa, puedes realizar más operaciones en este hilo
                    }
                }).start();
            }
        });









        botonSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario(Pagina2,editName,editSurname); //Registra el usuario
            }
        });





    }

    private void registrarUsuario(Intent Pagina2,EditText correo,EditText contraseña) {
        try{
            // Crear un nuevo usuario en Firebase Authentication
            mAuth.createUserWithEmailAndPassword(correo.getText().toString(), contraseña.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Registro exitoso, el usuario está autenticado
                                FirebaseUser user = mAuth.getCurrentUser();
                                Pagina2.putExtra("variableMensaje", "SING IN CORECTTTTDR");
                                startActivity(Pagina2);
                                // Puedes realizar acciones adicionales aquí, como redirigir al usuario a la página principal.

                            } else {
                                // El registro falló, muestra un mensaje de error al usuario.
                                Log.v(TAG, "ERROR: registro erroneo");
                                correo.setText("");
                                contraseña.setText("");

                                //Toast.makeText(Registro.this, errorRegistro, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (Exception e){
            correo.setText(""); // Borra lo escito y salta el mensaje de error
            contraseña.setText("");
            Log.v(TAG, "ERROR: alguno de los campos esta vacio");
        }
    }







    private void signInAnonymously(FirebaseAuth mAuth){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // La autenticación anónima fue exitosa
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                //mostrarCargando();
                                // El usuario anónimo se ha autenticado correctamente
                            }
                        } else {
                            // La autenticación anónima falló
                            Log.v(TAG, "ERROR: registro anonimo erroneo");

                        }
                    }
                });
    }







}