package com.example.bruno.diabeteslearning.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bruno.diabeteslearning.Database.Firebase;
import com.example.bruno.diabeteslearning.Database.LocalFirebaseListener;
import com.example.bruno.diabeteslearning.Models.UserProfile;
import com.example.bruno.diabeteslearning.R;


public class PreferencesActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private static String TAG = PreferencesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mNameEditText = findViewById(R.id.edit_text_name);
        mEmailEditText = findViewById(R.id.edit_text_email);

    }
    @Override
    public void onBackPressed() {
        //do nothing
    }

    //SAVE BUTTON CLICK LISTENER
    public void checkPreferenceData(View view) {
        UserProfile userProfile = new UserProfile(mNameEditText.getText().toString(), mEmailEditText.getText().toString());

        if (!userProfile.name.isEmpty() && !userProfile.email.isEmpty()) {

            if (isOnline()) {
                if (Firebase.getInstance().isLocalFirebaseOn()) {
                    saveProfile();
                } else {
                    configProgressDialog();
                }
            } else {
                Toast.makeText(this, "Conecte-se na internet " +
                                "para sincronizar com a nuvem",
                        Toast.LENGTH_LONG).show();
            }

        } else if (userProfile.name.isEmpty() && !userProfile.email.isEmpty()) {
            Toast.makeText(this, "Campo Nome em branco",
                    Toast.LENGTH_SHORT).show();
            mNameEditText.requestFocus();

        } else if (!userProfile.name.isEmpty() && userProfile.email.isEmpty()) {
            Toast.makeText(this, "Campo Email em branco",
                    Toast.LENGTH_SHORT).show();
            mEmailEditText.requestFocus();
        }
        else{
            Toast.makeText(this, "Preencha os campos acima",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void configProgressDialog() {
        final ProgressDialog dialog = new ProgressDialog(PreferencesActivity.this);
        dialog.setMessage("Sincronizando com a nuvem");
        dialog.show();
        Firebase.getInstance().setLocalFirebaseListener(new LocalFirebaseListener() {
            @Override
            public void onLocalFirebaseLoaded(boolean sucess) {
                if (sucess) {
                    dialog.dismiss();
                    saveProfile();
                }
                else{
                    dialog.dismiss();
                    Log.e(TAG, "Falha ao carregar firebase localmente");
                }
            }
        });
    }

    private void saveProfile() {
        UserProfile userProfile = new UserProfile(mNameEditText.getText().toString(), mEmailEditText.getText().toString());
        saveUserProfilePreferences(userProfile);
        Toast.makeText(this, "Salvo com Sucesso",
                Toast.LENGTH_SHORT).show();

        setResult(Activity.RESULT_OK, null);
        finish();
    }

    private void saveUserProfilePreferences(UserProfile userProfile) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pref_name_key), userProfile.name);
        editor.putString(getString(R.string.pref_email_key), userProfile.email);
        editor.apply();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else
            return false;
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
