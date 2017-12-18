package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.flow.flowlocationassignment.R;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import helper.Constants;

/**
 * Created by UsmanKhan on 12/14/17.
 * This class is an adapter for Trips. It is using arraylist from a server to parse data as per requirement.
 */


public class TrackingItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final int VIEW_TYPE_ITEM = 0;
    static Context context;
    private ArrayList<ParseObject> tripItemsArrayList;
    private OnItemClickListener onItemClickListener;
    SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss a");
    private Constants constantsInstance = Constants.getInstance();

    public TrackingItemsAdapter(Context context1) {
        tripItemsArrayList = new ArrayList<>();
        context = context1;
    }


    public void addAll(List<ParseObject> tripList) {

        tripItemsArrayList.clear();

        for (ParseObject parseObjectTrackItems : tripList) {
            tripItemsArrayList.add(parseObjectTrackItems);
        }

        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {

        return VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_detail_list, parent, false);

        return new TripsViewHolder(view, onItemClickListener);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final ParseObject parseObject = tripItemsArrayList.get(position);

        if (holder instanceof TripsViewHolder) {

            if (tripItemsArrayList.size() > 0) {
                try {

                    if (parseObject.has("tripName")) {
                        ((TripsViewHolder) holder).txtTrackItem.setText("" + parseObject.getString("tripName"));
                    }

                        ((TripsViewHolder) holder).txtCreatedAt.setText(dateFormatGmt.format(constantsInstance.getDateTimeUTC(parseObject.getCreatedAt())));

                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return tripItemsArrayList == null ? 0 : tripItemsArrayList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public Filter getFilter() {
        return null;
    }


    /**
     * TripsViewHolder class for modelViewHolder pattern.
     */

    static class TripsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtTrackItem, txtCreatedAt;
        OnItemClickListener onItemClickListener;


        public TripsViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            txtTrackItem = itemView.findViewById(R.id.txtTrackItem);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);

            itemView.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}

