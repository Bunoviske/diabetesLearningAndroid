package com.example.bruno.diabeteslearning.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bruno.diabeteslearning.Models.UserProfile;
import com.example.bruno.diabeteslearning.R;


public class PreferencesActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mEmailEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mNameEditText = findViewById(R.id.edit_text_name);
        mEmailEditText = findViewById(R.id.edit_text_email);

    }

    //SAVE BUTTON CLICK LISTENER
    public void checkPreferenceData(View view){
        UserProfile userProfile = new UserProfile(mNameEditText.getText().toString(), mEmailEditText.getText().toString());


        if(!userProfile.name.isEmpty() && !userProfile.email.isEmpty()){
            saveUserProfilePreferences(userProfile);
            Toast.makeText(this, "Salvo com Sucesso",
                    Toast.LENGTH_SHORT).show();
            finish();

        } else if (userProfile.name.isEmpty() && !userProfile.email.isEmpty()){
            Toast.makeText(this, "Campo Nome em branco",
                    Toast.LENGTH_SHORT).show();
            mNameEditText.requestFocus();

        } else if (!userProfile.name.isEmpty() && userProfile.email.isEmpty()){
            Toast.makeText(this, "Campo Email em branco",
                    Toast.LENGTH_SHORT).show();
            mEmailEditText.requestFocus();
        }
    }

    public void saveUserProfilePreferences(UserProfile userProfile){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pref_name_key), userProfile.name);
        editor.putString(getString(R.string.pref_email_key), userProfile.email);
        editor.apply();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
