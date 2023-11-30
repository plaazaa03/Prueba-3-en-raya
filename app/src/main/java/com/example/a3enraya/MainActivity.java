package com.example.a3enraya;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a3enraya.R;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private boolean turnoJugador = true; // true para el jugador, false para la máquina
    private ArrayList<ImageButton> botonesDisponibles = new ArrayList<>();
    private boolean isGameRunning = false;
    private boolean allowPlayerMoves = false;
    private ImageButton[][] matrizBotones = new ImageButton[3][3];

    private TextView textViewTurno;
    private ImageView imageViewTurno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matrizBotones[0][0] = findViewById(R.id.botonA1);
        matrizBotones[0][1] = findViewById(R.id.botonA2);
        matrizBotones[0][2] = findViewById(R.id.botonA3);
        matrizBotones[1][0] = findViewById(R.id.botonB1);
        matrizBotones[1][1] = findViewById(R.id.botonB2);
        matrizBotones[1][2] = findViewById(R.id.botonB3);
        matrizBotones[2][0] = findViewById(R.id.botonC1);
        matrizBotones[2][1] = findViewById(R.id.botonC2);
        matrizBotones[2][2] = findViewById(R.id.botonC3);

        for (int i = 0; i < matrizBotones.length; i++) {
            for (int j = 0; j < matrizBotones[i].length; j++) {
                final int fila = i;
                final int columna = j;
                matrizBotones[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBotonClicked(fila, columna);
                    }
                });
            }
        }

        ToggleButton toggleButton = findViewById(R.id.toggleButtonStart);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleClicked(v);
            }
        });

        textViewTurno = findViewById(R.id.textViewTurno);
        imageViewTurno = findViewById(R.id.imageViewTurno);
    }

    private void onBotonClicked(int fila, int columna) {
        ImageButton boton = matrizBotones[fila][columna];

        if (isGameRunning && allowPlayerMoves && boton.getDrawable() == null) {
            realizarJugada(boton);
            if (verificarTresEnRaya()) {
                stopGame();
                showToast("¡Tres en raya! El juego ha terminado.");
            } else if (botonesDisponibles.isEmpty()) {
                stopGame();
                showToast("¡Empate! El juego ha terminado.");
            } else {
                turnoMaquina();
                if (verificarTresEnRaya()) {
                    stopGame();
                    showToast("¡Tres en raya! El juego ha terminado.");
                }
            }
        }
    }

    private void onToggleClicked(View v) {
        ToggleButton toggleButton = (ToggleButton) v;
        boolean isChecked = toggleButton.isChecked();

        if (isChecked) {
            // Botón START presionado, iniciar la partida
            startGame();
            showToast("La partida ha comenzado");
        } else {
            // Botón STOP presionado, detener la partida
            stopGame();
            showToast("La partida ha finalizado");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void stopGame() {
        isGameRunning = false;
        allowPlayerMoves = false;
        reiniciarTablero();
        habilitarBotones();
    }

    private void reiniciarTablero() {
        for (int i = 0; i < matrizBotones.length; i++) {
            for (int j = 0; j < matrizBotones[i].length; j++) {
                matrizBotones[i][j].setImageDrawable(null);
            }
        }

        botonesDisponibles.clear();
        for (int i = 0; i < matrizBotones.length; i++) {
            for (int j = 0; j < matrizBotones[i].length; j++) {
                botonesDisponibles.add(matrizBotones[i][j]);
            }
        }
    }

    private void habilitarBotones() {
        for (ImageButton boton : botonesDisponibles) {
            boton.setEnabled(true);
        }
    }

    private void startGame() {
        if (!isGameRunning) {
            isGameRunning = true;
            allowPlayerMoves = true;
            enableButtonClicks(true);
            mostrarTurno(turnoJugador);
        }
    }


    private void mostrarTurno(boolean esTurnoJugador) {
        if (esTurnoJugador) {
            // Es el turno del jugador humano
            textViewTurno.setText("Turno Humano");
            imageViewTurno.setImageResource(R.drawable.humano); // Asegúrate de tener la imagen correcta en res/drawable
        } else {
            // Es el turno de la máquina
            textViewTurno.setText("Turno Máquina");
            imageViewTurno.setImageResource(R.drawable.robot); // Asegúrate de tener la imagen correcta en res/drawable
        }

        // Hacer los elementos visibles
        textViewTurno.setVisibility(View.VISIBLE);
        imageViewTurno.setVisibility(View.VISIBLE);
    }

    private void enableButtonClicks(boolean isEnabled) {
        for (ImageButton button : botonesDisponibles) {
            button.setEnabled(isEnabled);
        }
    }

    private void realizarJugada(ImageButton boton) {
        if (isGameRunning && allowPlayerMoves && boton.getDrawable() == null) {
            if (turnoJugador) {
                // Turno del jugador
                boton.setImageResource(R.drawable.humano);
            } else {
                // Turno de la máquina
                turnoMaquina();
            }

            botonesDisponibles.remove(boton);

            if (verificarTresEnRaya()) {
                stopGame();
                showToast("¡Tres en raya! El juego ha terminado.");
            } else if (botonesDisponibles.isEmpty()) {
                stopGame();
                showToast("¡Empate! El juego ha terminado.");
            } else {
                // Cambiar el turno solo si el juego sigue en curso
                if (isGameRunning) {
                    turnoJugador = !turnoJugador;
                    mostrarTurno(turnoJugador);
                }
            }
        }
    }

    private void turnoMaquina() {
        if (botonesDisponibles.isEmpty()) {
            return;
        }

        Random random = new Random();
        int indiceAleatorio = random.nextInt(botonesDisponibles.size());
        ImageButton botonMaquina = botonesDisponibles.get(indiceAleatorio);

        botonMaquina.setImageResource(R.drawable.robot);

        botonesDisponibles.remove(botonMaquina);
        turnoJugador = true;
    }

    private boolean verificarTresEnRaya() {
        // Verificar filas
        for (int i = 0; i < matrizBotones.length; i++) {
            if (verificarLinea(matrizBotones[i][0], matrizBotones[i][1], matrizBotones[i][2])) {
                return true;
            }
        }

        // Verificar columnas
        for (int i = 0; i < matrizBotones[0].length; i++) {
            if (verificarLinea(matrizBotones[0][i], matrizBotones[1][i], matrizBotones[2][i])) {
                return true;
            }
        }

        // Verificar diagonales
        if (verificarLinea(matrizBotones[0][0], matrizBotones[1][1], matrizBotones[2][2]) ||
                verificarLinea(matrizBotones[0][2], matrizBotones[1][1], matrizBotones[2][0])) {
            return true;
        }

        return false;
    }

    // Método auxiliar para verificar si tres botones tienen la misma imagen
    private boolean verificarLinea(ImageButton boton1, ImageButton boton2, ImageButton boton3) {
        return (boton1.getDrawable() != null) &&
                (boton1.getDrawable().getConstantState() == boton2.getDrawable().getConstantState()) &&
                (boton2.getDrawable().getConstantState() == boton3.getDrawable().getConstantState());
    }

}
