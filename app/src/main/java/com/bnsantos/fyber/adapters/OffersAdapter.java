package com.bnsantos.fyber.adapters;

import android.content.Context;
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
    private static final int VIEW_LOAD_MORE = 3;

    private List<Offer> offerList;
    private final Context context;

    public OffersAdapter(List<Offer> offerList, Context context) {
        this.offerList = offerList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_EMPTY:
                View empty = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_empty, parent, false);
                return new MyViewHolder(empty);
            case VIEW_LOAD_MORE:
                View loadMore = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_load_more, parent, false);
                return new MyViewHolder(loadMore);
            default:
                View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_offer, parent, false);
                return new OfferViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OfferViewHolder) {
            ((OfferViewHolder) holder).bind(context, offerList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (offerList != null && offerList.size() > 0) {
            return offerList.size() + 1;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (offerList != null && offerList.size() > 0) {
            if (position == offerList.size()) {
                return VIEW_LOAD_MORE;
            } else {
                return VIEW_ITEM;
            }
        } else {
            return VIEW_EMPTY;
        }
    }

    public void addAll(List<Offer> offerList, boolean clearBefore) {
        if (this.offerList == null) {
            this.offerList = new ArrayList<>();
        }
        if (clearBefore) {
            this.offerList.clear();
        }
        this.offerList.addAll(offerList);
        notifyDataSetChanged();
    }

    public Offer getItem(int position) {
        if (this.offerList != null && position >= 0 && position < this.offerList.size()) {
            return this.offerList.get(position);
        } else {
            return null;
        }
    }
}
