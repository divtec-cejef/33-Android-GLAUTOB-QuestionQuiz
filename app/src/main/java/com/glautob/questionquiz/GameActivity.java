package com.glautob.questionquiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.glautob.questionquiz.Controllers.QuestionManager;
import com.glautob.questionquiz.Models.Question;

public class GameActivity extends AppCompatActivity {

    Runnable questionRunnable = null;
    Handler handler;
    private TextView TV_Joueur1;
    private TextView TV_Joueur2;
    private TextView TV_PointsJoueur1;
    private TextView TV_PointsJoueur2;
    private TextView TV_Question_Joueur1;
    private TextView TV_Question_Joueur2;
    private Button BT_Joueur1;
    private Button BT_Joueur2;
    private Button BT_Rejouer;
    private Button BT_Menu;
    private LinearLayout Layout_MenuButtons;
    private QuestionManager questionManager;
    private Question actualQuestion;
    private int questionSpeed;

    /**
     * Création de la fenêtre de jeu
     * @param savedInstanceState Instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getGraphicalItems();

        initialiseActivity();
    }

    /**
     * Au lancement de la fenêtre de jeu
     */
    @Override
    protected void onStart() {
        super.onStart();

        BT_Joueur1.setOnClickListener(new View.OnClickListener() {
            /**
             * Au clic sur le bouton du joueur 1
             * Désactiver les boutons et attribuer les points
             * @param view Vue
             */
            @Override
            public void onClick(View view) {
                setJoueurButtonsEnable(false);
                if (actualQuestion.getResponse() == 1) {
                    TV_PointsJoueur1.setText(String.valueOf(Integer.parseInt(TV_PointsJoueur1.getText().toString()) + 1));
                } else {
                    TV_PointsJoueur1.setText(String.valueOf(Integer.parseInt(TV_PointsJoueur1.getText().toString()) - 1));
                }
            }
        });

        BT_Joueur2.setOnClickListener(new View.OnClickListener() {
            /**
             * Au clic sur le bouton du joueur 2
             * Désactiver les boutons et attribuer les points
             * @param view Vue
             */
            @Override
            public void onClick(View view) {
                setJoueurButtonsEnable(false);
                if (actualQuestion.getResponse() == 1) {
                    TV_PointsJoueur2.setText(String.valueOf(Integer.parseInt(TV_PointsJoueur2.getText().toString()) + 1));
                } else {
                    TV_PointsJoueur2.setText(String.valueOf(Integer.parseInt(TV_PointsJoueur2.getText().toString()) - 1));
                }
            }
        });

        BT_Menu.setOnClickListener(new View.OnClickListener() {
            /**
             * Au clic sur le bouton menu
             * Fermer la fenêtre de jeu
             * @param view Vue
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        BT_Rejouer.setOnClickListener(new View.OnClickListener() {
            /**
             * Au clic sur le bouton rejouer
             * Relancer une nouvelle partie
             * @param view Vue
             */
            @Override
            public void onClick(View view) {
                startNewGame();
            }
        });

        // Lancer une nouvelle partie
        startNewGame();
    }

    /**
     * passe à la question suivante
     */
    private void changeQuestion() {
        actualQuestion = questionManager.getNextQuestion();
        TV_Question_Joueur1.setText(actualQuestion.getQuestion());
        TV_Question_Joueur2.setText(actualQuestion.getQuestion());
        setJoueurButtonsEnable(true);
    }

    /**
     * Active ou désactive les boutons des joueurs
     * @param enable true pour activer, false pour désactiver
     */
    private void setJoueurButtonsEnable(boolean enable) {
        BT_Joueur1.setEnabled(enable);
        BT_Joueur2.setEnabled(enable);
    }

    /**
     * Démarre un nouvel itérateur de questions
     */
    private void startQuestionIterative() {
        handler = new Handler();

        questionRunnable = new Runnable() {
            /**
             * Itérateur de questions
             * s'il reste des questions, affiche la question suivante
             * sinon stop le jeux et affiche le résultat et le menu
             */
            @Override
            public void run() {
                if (!questionManager.questionsLeft()) {
                    // exécute le code de fin de partie
                    setJoueurButtonsEnable(false);
                    Layout_MenuButtons.setVisibility(View.VISIBLE);
                    setWinner();
                    handler.removeCallbacks(this);
                } else {
                    // code pour demander et afficher une question
                    changeQuestion();
                    handler.postDelayed(this, questionSpeed);
                }
            }
        };

        handler.postDelayed(questionRunnable, 1000);
    }

    /**
     * Démarre un nouveau compte à rebours
     */
    private void startCountDownTimer() {
        new CountDownTimer(6000, 1000) {
            /**
             * A chaque seconde du compte à rebours
             * Affiche le temps avant le départ à l'utilisateur
             * @param millisUntilFinished Temps restant
             */
            public void onTick(long millisUntilFinished) {
                // Afficher le compteur à l'utilisateur
                String timeLeft = Long.toString(millisUntilFinished / 1000 + 1);
                TV_Question_Joueur1.setText(timeLeft);
                TV_Question_Joueur2.setText(timeLeft);
            }

            /**
             * Au fin du compte à rebours
             * Affiche le départ à l'utilisateur
             * et démarre l'itérateur de questions
             */
            public void onFinish() {
                // Afficher le départ à l'utilisateur
                TV_Question_Joueur1.setText(R.string.game_lancement_jeu);
                TV_Question_Joueur2.setText(R.string.game_lancement_jeu);
                startQuestionIterative();
            }
        }.start();
    }

    /**
     * Démarre une nouvelle partie
     */
    private void startNewGame() {
        questionManager.newQuestionList(this);
        resetInterface();
        startCountDownTimer();
    }

    /**
     * Remet l'interface à zéro
     */
    private void resetInterface() {
        TV_PointsJoueur1.setText("0");
        TV_PointsJoueur2.setText("0");
        TV_Question_Joueur1.setText("");
        TV_Question_Joueur2.setText("");
        Layout_MenuButtons.setVisibility(View.INVISIBLE);
        setJoueurButtonsEnable(false);
    }

    /**
     * Affiche le nom des joueurs
     */
    private void setPlayersName() {
        Intent gameActivity = getIntent();

        TV_Joueur1.setText(gameActivity.getStringExtra("nom_joueur1"));
        TV_Joueur2.setText(gameActivity.getStringExtra("nom_joueur2"));
    }

    /**
     * Récupère les éléments graphiques
     */
    private void getGraphicalItems() {
        TV_Joueur1 = findViewById(R.id.game_joueur1_tv);
        TV_Joueur2 = findViewById(R.id.game_joueur2_tv);
        TV_PointsJoueur1 = findViewById(R.id.game_score_joueur1_tv);
        TV_PointsJoueur2 = findViewById(R.id.game_score_joueur2_tv);
        TV_Question_Joueur1 = findViewById(R.id.game_question_joueur1_tv);
        TV_Question_Joueur2 = findViewById(R.id.game_question_joueur2_tv);
        BT_Joueur1 = findViewById(R.id.game_joueur1_bt);
        BT_Joueur2 = findViewById(R.id.game_joueur2_bt);
        BT_Rejouer = findViewById(R.id.game_rejouer_bt);
        BT_Menu = findViewById(R.id.game_menu_bt);
        Layout_MenuButtons = findViewById(R.id.game_menu_buttons_layout);
    }

    /**
     * Initialise l'activité
     */
    private void initialiseActivity() {
        setPlayersName();
        questionManager = new QuestionManager(this);

        // Récupération des préférences
        // https://www.tutorialspoint.com/android/android_shared_preferences.htm
        SharedPreferences sharedPref = getSharedPreferences(this.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        // Récupération de la vitesse des questions
        questionSpeed = sharedPref.getInt("questionSpeed", 2000);
    }

    /**
     * Détermine le gagnant et l'affiche
     */
    private void setWinner() {
        int ptsJoueur1 = Integer.parseInt(TV_PointsJoueur1.getText().toString());
        int ptsJoueur2 = Integer.parseInt(TV_PointsJoueur2.getText().toString());
        String result = "";
        if (ptsJoueur1 > ptsJoueur2) {
            result = TV_Joueur1.getText().toString() + getString(R.string.game_msg_winner);
        } else if (ptsJoueur1 < ptsJoueur2) {
            result = TV_Joueur2.getText().toString() + getString(R.string.game_msg_winner);
        } else {
            result = getString(R.string.game_msg_equality);
        }
        TV_Question_Joueur2.setText(result);
        TV_Question_Joueur1.setText(result);
    }

}