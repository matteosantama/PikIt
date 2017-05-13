package hu.ait.matteo.pikit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

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
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView groupTitle;
        public TextView memberCount;

        public ViewHolder(View itemView) {
            super(itemView);
            groupTitle = (TextView) itemView.findViewById(R.id.groupTitle);
            memberCount = (TextView) itemView.findViewById(R.id.memberCount);
        }
    }
}
