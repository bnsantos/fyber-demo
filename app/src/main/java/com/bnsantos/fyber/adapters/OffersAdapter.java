package com.bnsantos.fyber.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bnsantos.fyber.R;
import com.bnsantos.fyber.model.Offer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 21/11/15.
 */
public class OffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_ITEM = 1;
    private static final int VIEW_EMPTY = 2;

    private List<Offer> offerList;

    public OffersAdapter(List<Offer> offerList) {
        this.offerList = offerList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_EMPTY:
                View empty = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_empty, parent, false);
                return new EmptyViewHolder(empty);
            default:
                View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_offer, parent, false);
                return new OfferViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OfferViewHolder) {
            ((OfferViewHolder) holder).bind(offerList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (offerList != null && offerList.size() > 0) {
            return offerList.size();
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (offerList != null && offerList.size() > 0) {
            return VIEW_ITEM;
        } else {
            return VIEW_EMPTY;
        }
    }

    public void addAll(List<Offer> offerList) {
        if (this.offerList == null) {
            this.offerList = new ArrayList<>();
        }
        this.offerList.addAll(offerList);
        notifyDataSetChanged();
    }
}
