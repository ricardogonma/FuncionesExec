package com.example.ricar.funcionesexec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    String linea;
    TextView ip, tvConexion;
    String conexion = "No hay conexión a internet";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = findViewById(R.id.ip);
        tvConexion = findViewById(R.id.conexion);
        tvConexion.setText("Comprobando conexión...");
        comandoIfConfif();
        comandoPing();
    }

    private void comandoIfConfif(){

        Runtime rt = Runtime.getRuntime();
        try{
            // Se lanza el proceso
            Process proceso = rt.exec("ifconfig");

            // Flujo de entrada (lee cada caracter)
            InputStream is = proceso.getInputStream();

            // Clase filtro (transforma los caracteres en cadenas)
            InputStreamReader isr = new InputStreamReader(is);

            // Lee el buffer (transforma las cadenas a lineas)
            final BufferedReader br = new BufferedReader(isr);

            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    Boolean escribir = false;
                    try {
                        while ((linea = br.readLine()) != null) {
                            if(!escribir && linea.contains("eth0")){
                                escribir = true;
                            }
                            if(escribir && linea.contains("inet addr")) {
                                String[] lineas = linea.split(" ");
                                String ip = "";
                                for(String l: lineas){
                                    if(l.contains("addr")){
                                        ip = l.replace("addr:","");
                                    }
                                }
                                pintarIp("Tu ip es: " + ip);
                            }
                            /**/
                            //txt.append(linea + "\n");
                        }
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            });

            th.start();

        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    private void comandoPing(){

        Runtime rt = Runtime.getRuntime();
        try{
            // Se lanza el proceso
            Process proceso = rt.exec("ping -c4 8.8.8.8");

            // Flujo de entrada (lee cada caracter)
            InputStream is = proceso.getInputStream();

            // Clase filtro (transforma los caracteres en cadenas)
            InputStreamReader isr = new InputStreamReader(is);

            // Lee el buffer (transforma las cadenas a lineas)
            final BufferedReader br = new BufferedReader(isr);

            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    Boolean escribir = false;
                    try {
                        while ((linea = br.readLine()) != null) {
                            if(linea.contains("4 packets transmitted, 4 received")){
                                conexion = "Hay conexión a internet";
                            }
                        }
                        pintaConexion(conexion);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            });

            th.start();

        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    private void pintarIp(final String linea){
        ip.post(new Runnable() {
            @Override
            public void run() {
                ip.setText(linea);
            }
        });
    }

    private void pintaConexion(final String conexion){
        tvConexion.post(new Runnable() {
            @Override
            public void run() {
                tvConexion.setText(conexion);
            }
        });
    }

}
