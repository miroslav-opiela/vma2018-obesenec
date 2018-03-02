package sk.upjs.vma.obesenec;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String BUNDLE_KEY = "game";

    private HangmanGame hangmanGame;

    private EditText editText;
    private TextView textView;
    private ImageView imageViewGallows;

    private int[] gallowsImages = {
            R.drawable.gallows0,
            R.drawable.gallows1,
            R.drawable.gallows2,
            R.drawable.gallows3,
            R.drawable.gallows4,
            R.drawable.gallows5,
            R.drawable.gallows6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textViewGuessedWord);
        editText = findViewById(R.id.editTextGuessedLetter);
        imageViewGallows = findViewById(R.id.imageViewGallows);

        Log.d("TEST", "onCreate");

        // ak nemame nic ulozene v bundle, nacitava sa nova hra
        if (savedInstanceState == null) {
            hangmanGame = new HangmanGame();
            updateGuessedWord();
        } else {
            // ak mame v bundle ulozeny stav hry, nacitame ho
            hangmanGame = (HangmanGame) savedInstanceState.getSerializable(BUNDLE_KEY);
            updateGuessedWord();
            updateGallows();
        }

        // zistenie aktualnej verzie androidu pocas behu
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.d("TEST", "mam staru verziu");
        } else {
            Log.d("TEST", "mam novu verziu");
        }

        // dalsie info o aktualnom zariadeni
        // Log.d("TEST", Build.DISPLAY);
        // Log.d("TEST", Build.DEVICE);
        // Log.d("TEST", Build.MANUFACTURER);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // ulozenie stavu hry
        outState.putSerializable(BUNDLE_KEY, hangmanGame);
    }

    private void updateGuessedWord() {
        textView.setText(hangmanGame.getGuessedCharacters());
        editText.setText("");
    }

    private void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void alertVictory() {
        ColorFilter filter = new LightingColorFilter(Color.GREEN, Color.BLACK);
        imageViewGallows.setColorFilter(filter);

        alert("Congratulations.");
    }

    private void alertFailedGame() {
        ColorFilter filter = new LightingColorFilter(Color.RED, Color.BLACK);
        imageViewGallows.setColorFilter(filter);

        alert("GAME OVER.");
    }

    private void updateGallows() {
        imageViewGallows.setImageResource(
                gallowsImages[HangmanGame.DEFAULT_ATTEMPTS_LEFT
                        - hangmanGame.getAttemptsLeft()]);
    }

    private void restartGame() {
        hangmanGame = new HangmanGame();
        imageViewGallows.setImageResource(gallowsImages[0]);
        imageViewGallows.setColorFilter(null);
        updateGuessedWord();
    }

    public void gallowsClick(View view) {
        // ak je uz hra ukoncena a ma sa spustit nova hra
        if (hangmanGame.isWon() || hangmanGame.getAttemptsLeft() == 0) {
            restartGame();
            return;
        }

        // nacitanie pismena z edit textu
        CharSequence text = editText.getText();
        if (text == null || text.length() == 0) {
            alert("Insert a letter!");
            return;
        }

        // hadanie pismena
        boolean isGuessed = hangmanGame.guess(Character.toLowerCase(text.charAt(0)));
        updateGuessedWord();

        if (isGuessed) {
            // ak sa uhadlo pismeno a tym aj cele slovo, tak hra konci vitazstvom
            if (hangmanGame.isWon()) {
                alertVictory();
            }
        } else {
            // ak sa pismeno neuhadlo, updatne sa obrazok sibenice
            updateGallows();
            // ak uz nie su dalsie pokusy, hra konci
            if (hangmanGame.getAttemptsLeft() == 0) {
                alertFailedGame();
            }
        }
    }


}
