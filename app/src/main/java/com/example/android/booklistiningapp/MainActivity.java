package com.example.android.booklistiningapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String GOOGLE_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    private static final int BOOK_LOADER_ID = 1;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private boolean isClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isClick = false;

        final TextView state;
        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        mRecyclerView.setVisibility(GONE);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        state = (TextView) findViewById(R.id.state_text_view);
        state.setText(getString(R.string.state_empty_view));
        state.setVisibility(View.VISIBLE);

        final EditText bookName = (EditText) findViewById(R.id.book_name);

        if (!TextUtils.isEmpty(bookName.getText().toString().trim())) {
            updateInfo();
        }


        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.progress_bar);
        loadingSpinner.setVisibility(GONE);

        final ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        ImageView search = (ImageView) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isClick = true;

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                final boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();


                if (isConnected) {

                    state.setVisibility(GONE);
                    state.setText(getString(R.string.state_empty_view));
                    mRecyclerAdapter = new BookRecyclerAdapter(MainActivity.this, new ArrayList<Book>());

                    mRecyclerView.setAdapter(mRecyclerAdapter);

                    if (!TextUtils.isEmpty(bookName.getText().toString().trim())) {
                        updateInfo();
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.toast), Toast.LENGTH_SHORT).show();
                        state.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(GONE);
                    }
                } else {
                    ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.progress_bar);
                    loadingSpinner.setVisibility(GONE);
                    mRecyclerView.setVisibility(GONE);

                    state.setText(getString(R.string.state_no_internet));
                    state.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    public void showDetailsDialog(String description) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Html.fromHtml("<font color = '#FF4081'>Book Description</font>"));
        builder.setMessage(description);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateInfo() {
        EditText bookName = (EditText) findViewById(R.id.book_name);
        String title = bookName.getText().toString();
        title = title.replace(" ", "+");
        String uriString = GOOGLE_REQUEST_URL + title;
        Bundle args = new Bundle();
        args.putString("Uri", uriString);
        android.app.LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(BOOK_LOADER_ID, args, MainActivity.this);
        if (loaderManager.getLoader(BOOK_LOADER_ID).isStarted()) {
            //restart it if there's one
            getLoaderManager().restartLoader(BOOK_LOADER_ID, args, MainActivity.this);
        }
    }

    @Override
    public android.content.Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        mRecyclerView.setVisibility(GONE);

        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.progress_bar);
        loadingSpinner.setVisibility(View.VISIBLE);
        return new BookLoader(this, args.getString("Uri"));
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Book>> loader, List<Book> books) {
        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.progress_bar);
        loadingSpinner.setVisibility(GONE);

        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerAdapter = new BookRecyclerAdapter(MainActivity.this, new ArrayList<Book>());
        if (books != null && !books.isEmpty()) {
            mRecyclerAdapter = new BookRecyclerAdapter(MainActivity.this, books);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        } else {
            if (isClick){
                Toast.makeText(MainActivity.this, "Not Found!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Book>> loader) {
        mRecyclerAdapter = new BookRecyclerAdapter(MainActivity.this, new ArrayList<Book>());
    }

    public static class BookLoader extends AsyncTaskLoader<List<Book>> {

        /**
         * Query URL
         */
        private final String mUrl;

        /**
         * Constructs a new {@link BookLoader}.
         *
         * @param context of the activity
         * @param url     to load data from
         */
        public BookLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        /**
         * This is on a background thread.
         */
        @Override
        public List<Book> loadInBackground() {
            if (mUrl == null) {
                return null;
            }

            List<Book> books = Utils.fetchBookData(mUrl);
            return books;
        }
    }


}
