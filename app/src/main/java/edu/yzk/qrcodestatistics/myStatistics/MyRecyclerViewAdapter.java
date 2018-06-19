package edu.yzk.qrcodestatistics.myStatistics;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.yzk.qrcodestatistics.R;

/**
 * Created by 青青-子衿 on 2018/1/15.
 */


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    private List<UserStatisticsModel> list;
    private OnItemClickListener mOnItemClickListener;

    public MyRecyclerViewAdapter(List<UserStatisticsModel> list) {
        this.list = list;
    }

    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        MyRecyclerViewAdapter.ViewHolder viewHolder = new MyRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.statisticsId.setText(list.get(position).getStatisticsId());
        holder.statisticsName.setText(list.get(position).getStatisticsName());
        final int position2 = position;

        if( mOnItemClickListener!= null){
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position2);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener{
        void onClick( int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this.mOnItemClickListener=onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView statisticsId;
        TextView statisticsName;

        ViewHolder(View itemView) {
            super(itemView);
            statisticsId = itemView.findViewById(R.id.tv_statistics_id);
            statisticsName = itemView.findViewById(R.id.tv_statistics_name);
        }
    }
}