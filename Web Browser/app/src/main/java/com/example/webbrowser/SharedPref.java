package com.example.webbrowser;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.Context.MODE_PRIVATE;

public class SharedPref extends AppCompatActivity {

    String[] Memory = new String[1000];
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public void LoadSharedPref()
    {
        sharedPref = getSharedPreferences("Memory", MODE_PRIVATE);

        editor = sharedPref.edit();

    }
    public int LoadIndex()
    {
        return sharedPref.getInt("Memory_Index" , -1);
    }
    public String[] LoadMemory(int Memory_index)
    {

        for(int i = 0; i <= Memory_index ; i++)
        {
            Memory[i] = sharedPref.getString("Memory" + i, "null");
        }
        return Memory;
    }
    public void SaveMemory(int index, String[] Memory)
    {
        editor.putString("Memory" + index, Memory[index]);


        editor.putInt("Memory_Index", index);
        editor.apply();
    }
}
