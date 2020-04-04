package com.example.searchfilterapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<SearchItem> searchList;
    private List<SearchItem> searchListFiltered;
    private SearchAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, info;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            info = view.findViewById(R.id.info);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onSearchItemSelected(searchListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public SearchAdapter(Context context, List<SearchItem> contactList, SearchAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.searchList = contactList;
        this.searchListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final SearchItem searchitem = searchListFiltered.get(position);
        holder.name.setText(searchitem.getName());
        holder.info.setText(searchitem.getInfo());

        Glide.with(context)
                .load(searchitem.getImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return searchListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    searchListFiltered = searchList;
                } else {
                    List<SearchItem> filteredList = new ArrayList<>();
                    for (SearchItem row : searchList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getInfo().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    searchListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = searchListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                searchListFiltered = (ArrayList<SearchItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface SearchAdapterListener {
        void onSearchItemSelected(SearchItem item);
    }
}
