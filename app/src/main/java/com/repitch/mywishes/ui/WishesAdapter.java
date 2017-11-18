package com.repitch.mywishes.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.repitch.mywishes.R;
import com.repitch.mywishes.db.entity.Wish;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by repitch on 18.11.2017.
 */
public class WishesAdapter extends RecyclerView.Adapter<WishesAdapter.ViewHolder> {

    private List<Wish> wishes = new ArrayList<>();
    private WishClickListener listener;

    public WishesAdapter(WishClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wish, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Wish wish = wishes.get(position);

        holder.itemView.setOnClickListener(v -> listener.onWishClicked(wish));

        Glide.with(holder.itemView.getContext())
                .load(wish.getImageUrl())
                .into(holder.image);
        holder.name.setText(wish.getName());
        holder.cost.setText(Long.toString(wish.getCost()));
    }

    @Override
    public int getItemCount() {
        return wishes.size();
    }

    public void setWishes(List<Wish> wishes) {
        this.wishes.clear();
        this.wishes.addAll(wishes);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView name;
        final TextView cost;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.wish_image);
            name = itemView.findViewById(R.id.wish_name);
            cost = itemView.findViewById(R.id.wish_cost);
        }
    }

    public interface WishClickListener {
        void onWishClicked(Wish wish);
    }
}
