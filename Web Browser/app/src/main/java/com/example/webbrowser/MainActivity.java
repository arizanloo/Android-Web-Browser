package com.example.webbrowser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity {
//UI
WebView webview;
SwipeRefreshLayout swipeRefreshLayout;
ProgressBar progressBar;
AlertDialog alert;
//History and SharedPrefs
String  Search;
String[] Memory = new String[1000];
int Memory_index;
String SearchEngine;
SharedPreferences sharedPref;
SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview = findViewById(R.id.web);
        swipeRefreshLayout = findViewById(R.id.refreshlayout);
        progressBar = findViewById(R.id.progressBar);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webview.reload();
            }
        });
        InitializeSharedPref();

        Memory_index = 0;
        LoadMemory();

        webview.setWebViewClient(new CustomWebViewClient());
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Intent intent = getIntent();
        if (intent.getStringExtra("URL") == null) //If intent is null load default values to Webview
            ValidateSearchEngine();
        else
            ValidateURL(intent.getStringExtra("URL")); //if intent contains a String, pass it to ValidateSearchEngine as a URL


        webview.setWebChromeClient(new WebChromeClient()
        {
            //Change value of progressBar on WebVew onProgressChanged
            public void onProgressChanged(WebView view, int progress)
            {
                progressBar.setVisibility(View.VISIBLE); //make progressBar visible if there is any progress

                setTitle("Loading...");

                progressBar.setProgress(progress);

                if(progress == 100) //If progress finished:
                {
                    setTitle(webview.getTitle());

                    if(swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);

                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            //Save Memory when progress finished
            @Override
            public void onReceivedTitle(WebView view, String title) {
                Memory[Memory_index] = view.getOriginalUrl();
                SaveMemory();
                Memory_index++;

                super.onReceivedTitle(view, title);

            }
        });
    }

    @Override
    public void onBackPressed() {
        /*
         * Check if web view Can go back when Back pressed, Go back.
         * Else finish the activity and save memory
         */
        if(webview.canGoBack())
        {
            webview.goBack();
            return;
        }
        else
        {
            SaveMemory();
            Log.e("APP", SearchEngine);
            finish();
        }
        super.onBackPressed();
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        //Initialize search view on menu
        final SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search)
                .getActionView();

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {return true;}

            public boolean onQueryTextSubmit(String query) {
                //String 'query' is the text that is submitted in searchView
                ValidateURL(query);
                searchView.clearFocus();

                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.homemenuitem)//If home menu item selected load home page
        {
            ValidateSearchEngine();
        }
        else if(item.getItemId() == R.id.historymenuitem)//If history menu item selected, start HistoryActivity
        {
            Intent intent = new Intent(MainActivity.this, TabsActivity.class);
            intent.putExtra("Memory", Memory)
                  .putExtra("Memory_index",Memory_index);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.searchenginemenuitem)//If Search Engine menu item selected, Show alert dialog to select the search engine
        {
            SearchEngine_AlertDialog();
        }
        else if(item.getItemId() == R.id.clearhistorymenuitem)//If Clear menu item selected, Call ClearSharedPref() to clear all saved memory
        {
            ClearSharedPref();
        }
        return super.onOptionsItemSelected(item);
    }
    //Show Alert Dialog to choose search engine
    public void SearchEngine_AlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); //Make a new AlertDialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.search_engine_alertdialog, null); //Inflate search_engine_alertdialog for AlertDialog

        //Set parameters for AlertDialog builder
        builder.setView(dialogLayout)
                .setTitle("Select Search Engine:")
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.cancel();
                        SaveMemory();
                    }
                });
        RadioGroup radioGroup = dialogLayout.findViewById(R.id.radiogroug);//Initialize radioGroup from dialogLayout
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //Switch for checked radio button id
                switch (i)
                {
                    case R.id.radioButton_google:
                        SearchEngine = "Google";
                        break;
                    case R.id.radioButton_yahoo:
                        SearchEngine = "Yahoo!";
                        break;
                    case R.id.radioButton_bing:
                        SearchEngine = "Bing";
                        break;
                    case R.id.radioButton_duckduckgo:
                        SearchEngine = "DuckDuckGo";
                        break;
                    case R.id.radioButton_ecosia:
                        SearchEngine = "Ecosia";
                        break;
                    default:
                        SearchEngine = "Google";
                        break;
                }
                SaveMemory();
            }
        });

        alert = builder.create();
        alert.show();
    }
    public void ValidateSearchEngine()
    {
        //Load Webview search engine
        if(SearchEngine.equals("Google"))
            ValidateURL("http://www.google.com/");
        else if (SearchEngine.equals("Yahoo!"))
            ValidateURL("http://www.yahoo.com/");
        else if (SearchEngine.equals("Bing"))
            ValidateURL("http://www.bing.com/");
        else if (SearchEngine.equals("DuckDuckGo"))
            ValidateURL("http://www.duckduckgo.com/");
        else if (SearchEngine.equals("Ecosia"))
            ValidateURL("http://www.ecosia.org/");
    }
    public void ValidateURL(String URL)
    {
        /* Validate Url:
         * if passed String is a Url: Load it
         * if passed String is a search text: make a Url and Load
         */
        if(URL.contains("www.") || URL.contains("."))
        {
            webview.loadUrl(URL);
        }
        else
        {
            if(SearchEngine.equals("Google")) {
                Search = URL.replaceAll(" ", "+");
                URL = "www.google.com/search?q=" + Search;
                webview.loadUrl(URL);
            }
            else if (SearchEngine.equals("Yahoo!"))
            {
                Search = URL.replaceAll(" ", "+");
                URL = "search.yahoo.com/search?p=" + Search;
                webview.loadUrl(URL);
            }
            else if (SearchEngine.equals("Bing"))
            {
                Search = URL.replaceAll(" ", "+");
                URL = "www.bing.com/search?q=" + Search;
                webview.loadUrl(URL);
            }
            else if (SearchEngine.equals("DuckDuckGo"))
            {
                Search = URL.replaceAll(" ", "+");
                URL = "duckduckgo.com/?q=" + Search;
                webview.loadUrl(URL);
            }
            else if (SearchEngine.equals("Ecosia")){
                Search = URL.replaceAll(" ", "%20");
                URL = "www.ecosia.org/search?q=" + Search;
                webview.loadUrl(URL);
            }
        }
    }
    //Initialize memory shared preference
    public void InitializeSharedPref()
    {
        sharedPref = getSharedPreferences("Memory", MODE_PRIVATE);

        editor = sharedPref.edit();
    }
    //Load Memory from sharedPref
    public void LoadMemory()
    {
        Memory_index = sharedPref.getInt("Memory_Index" , -1);
        SearchEngine = sharedPref.getString("Search_Engine", "null");

        if(SearchEngine.equals("null"))
            SearchEngine = "Google";

        for(int i = 0; i <= Memory_index ; i++)
        {
            Memory[i] = sharedPref.getString("Memory" + i, "null");
        }
    }
    //Save memory shared preference
    public void SaveMemory()
    {
        editor.putString("Memory" + Memory_index, Memory[Memory_index]);
        editor.putString("Search_Engine", SearchEngine);
        editor.putInt("Memory_Index", Memory_index);
        editor.apply();
    }
    //Clear all memories and reset Memory_index
    public void ClearSharedPref()
    {
        for(int i = 0; i <= Memory_index ; i++)
        {
            editor.remove("Memory" + i);
        }
        Memory = null;
        Memory_index = 0;
        editor.putInt("Memory_Index", Memory_index);
        editor.commit();
    }
}