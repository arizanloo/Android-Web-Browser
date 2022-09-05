package com.example.webbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


public class TabsActivity extends AppCompatActivity {
    String[] Memory = new String[1000];

    LinearLayout list;

    int Memory_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        Intent intent = getIntent();
        Memory = intent.getStringArrayExtra("Memory");
        Memory_index = intent.getIntExtra("Memory_index", 0);

        list = findViewById(R.id.linear_layout_tags);


        Initialize_List();
    }
   public void Initialize_List() {
       for(int i = 0; i <= Memory_index ; i++)
       {
           if(Memory == null) // If there is no memory (History cleared)
           {
               Toast.makeText(this, "History list is empty", Toast.LENGTH_SHORT).show();
               break;
           }
           if(Memory[i] != null)
           {
               final Button button = new Button(TabsActivity.this);
               button.setId(i);

                /*
                * Initialize the text of buttons
                * if the string length is more than 30 replace it with "..."
                 */
               if (Memory[i].length() > 30)
               {
                   String ButtonUrl = Memory[i].substring(0, 30) + "...";
                   button.setText(ButtonUrl);
               } else
                   button.setText(Memory[i]);

               //Initialize the onClick listener for each button
               button.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent intent = new Intent(TabsActivity.this, MainActivity.class);
                       intent.putExtra("URL", button.getText().toString());
                       startActivity(intent);
                       finish();
                   }
               });

               button.setWidth(LinearLayout.LayoutParams.MATCH_PARENT); // Set button width to MATCH_PARENT
               list.addView(button, 0); // Add button to list
           }
       }
   }
}

