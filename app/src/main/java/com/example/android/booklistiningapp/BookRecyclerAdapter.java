package com.example.android.booklistiningapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by admin on 2017-07-16.
 */

public class BookRecyclerAdapter extends RecyclerView.Adapter<BookRecyclerAdapter.ViewHolder> {

    List<Book> mBooks;
    MainActivity mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected TextView author;


        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_text_view);
            author = (TextView) itemView.findViewById(R.id.author_text_view);

        }
    }

    public BookRecyclerAdapter(MainActivity context, List<Book> books) {
        this.mBooks = books;
        this.mContext = context;
    }

    @Override
    public BookRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    @Override
    public void onBindViewHolder(BookRecyclerAdapter.ViewHolder holder, int position) {
        final Book currentBook = mBooks.get(position);

        holder.title.setText(currentBook.getTitle());

        holder.author.setText(currentBook.getAuthor());


        holder.title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mContext.showDetailsDialog(currentBook.getDescription());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }
}
