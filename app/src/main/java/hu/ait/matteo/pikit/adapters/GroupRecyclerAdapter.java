package hu.ait.matteo.pikit.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import hu.ait.matteo.pikit.GroupDetail;
import hu.ait.matteo.pikit.R;
import hu.ait.matteo.pikit.data.Group;

/**
 * Created by matteosantamaria on 5/13/17.
 */

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder> {

    private Context context;
    private String uID;
    private List<Group> groupList;

    public GroupRecyclerAdapter(Context context, String uID) {
        this.context = context;
        this.uID = uID;
        groupList = new ArrayList<Group>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (groupList.size() != 0) {
            holder.groupTitle.setText(groupList.get(position).getName());
            holder.memberCount.setText(groupList.get(position).getMembers().size() + " member(s)");

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,GroupDetail.class);
                    Bundle extras = new Bundle();

                    extras.putString("groupID", groupList.get(position).getUniqueID());
                    extras.putString("groupName", groupList.get(position).getName());
                    extras.putString("userID", uID);

                    intent.putExtras(extras);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }
            });

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            holder.tvDate.setText(dateFormat.format(groupList.get(position).getDate()));
        }
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }


    public void addGroup(Group group) {
        groupList.add(group);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView groupTitle;
        public TextView memberCount;
        public TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            groupTitle = (TextView) itemView.findViewById(R.id.groupTitle);
            memberCount = (TextView) itemView.findViewById(R.id.memberCount);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }
}
