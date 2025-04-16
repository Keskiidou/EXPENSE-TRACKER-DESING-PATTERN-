    package com.expensetracker;

    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.app.AppCompatDelegate;
    import com.google.android.material.switchmaterial.SwitchMaterial;

    public class MainActivity extends AppCompatActivity {

        private SwitchMaterial darkModeSwitch;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // Load saved theme preference
            SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
            boolean isNightMode = prefs.getBoolean("dark_mode", false);

            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            darkModeSwitch = findViewById(R.id.dark_mode_switch);
            darkModeSwitch.setChecked(isNightMode);

            darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    saveDarkModePref(true);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    saveDarkModePref(false);
                }
                 recreate();
            });
            findViewById(R.id.login_button).setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, Login.class));
            });
            findViewById(R.id.signup_button).setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, Register.class));
            });
        }

        private void saveDarkModePref(boolean isDarkMode) {
            SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putBoolean("dark_mode", isDarkMode);
            editor.apply();
        }
    }
