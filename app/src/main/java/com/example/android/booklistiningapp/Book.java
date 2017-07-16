package com.example.android.booklistiningapp;

import java.util.ArrayList;

/**
 * Created by admin on 2017-07-16.
 */

public class Book {
    private String mTitle;

    private ArrayList<String> mAuthor;

    private double mRating;

    private String mDescription;

    public Book(String title, ArrayList<String> author, double rating, String description){
        mTitle = title;
        mAuthor = author;
        mRating = rating;
        mDescription = description;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        String authors = checkAuthors();
        return authors;
    }

    public double getRating() {
        return mRating;
    }

    public String getDescription() {
        return mDescription;
    }

    public String checkAuthors() {
        String authors = mAuthor.get(0);
        if (mAuthor.size()>1) {
            for (int i=1; i<mAuthor.size(); i++) {
                authors += "\n" + mAuthor.get(i);
            }
        }
        return authors;
    }
}
