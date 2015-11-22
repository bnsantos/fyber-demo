package com.bnsantos.fyber.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnsantos.fyber.R;
import com.bnsantos.fyber.model.Offer;
import com.bnsantos.fyber.model.Thumbnail;
import com.squareup.picasso.Picasso;

/**
 * Created by bruno on 21/11/15.
 */
public class OfferViewHolder extends RecyclerView.ViewHolder {
    private final TextView title;
    private final TextView teaser;
    private final ImageView thumbnail;
    public OfferViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        teaser = (TextView) itemView.findViewById(R.id.teaser);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
    }

    public void bind(Context context, Offer offer) {
        title.setText(offer.getTitle());
        teaser.setText(offer.getTeaser());
        Thumbnail thumb = offer.getThumbnail();
        if (thumb != null) {
            if (thumb.getHires() != null) {
                Picasso.with(context).load(thumb.getHires()).fit().into(thumbnail);
            } else if (thumb.getLowres() != null) {
                Picasso.with(context).load(thumb.getLowres()).fit().into(thumbnail);
            } else {
                thumbnail.setImageResource(R.drawable.fyber);
            }
        } else {
            thumbnail.setImageResource(R.drawable.fyber);
        }
    }
}
