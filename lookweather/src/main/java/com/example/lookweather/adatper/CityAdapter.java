package com.example.lookweather.adatper;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lookweather.R;

import java.util.ArrayList;

/**
 * Created by hugo on 2016/2/19 0019.
 */
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private Context mContext;
    private ArrayList<String> dataList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    /**
     * Instantiates a new City adapter.
     *
     * @param context  the context
     * @param dataList the data list
     */
    public CityAdapter(Context context, ArrayList<String> dataList) {
        mContext = context;
        this.dataList = dataList;
    }


    @Override public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CityViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_city, parent, false));
    }


    @Override public void onBindViewHolder(final CityViewHolder holder, final int position) {
        holder.itemCity.setText(dataList.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
    }


    @Override public int getItemCount() {
        return dataList.size();
    }


    /**
     * Sets on item click listener.
     *
     * @param listener the listener
     */
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    /**
     * The interface On recycler view item click listener.
     */
    public interface OnRecyclerViewItemClickListener {
        /**
         * On item click.
         *
         * @param view the view
         * @param pos  the pos
         */
        void onItemClick(View view, int pos);
    }

    /**
     * The type City view holder.
     */
    class CityViewHolder extends RecyclerView.ViewHolder {
        private TextView itemCity;
        private CardView cardView;


        /**
         * Instantiates a new City view holder.
         *
         * @param itemView the item view
         */
        public CityViewHolder(View itemView) {
            super(itemView);
            itemCity = (TextView) itemView.findViewById(R.id.item_city);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}
