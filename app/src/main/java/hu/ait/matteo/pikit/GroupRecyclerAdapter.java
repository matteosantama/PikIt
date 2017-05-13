package hu.ait.matteo.pikit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.ait.matteo.pikit.data.Group;

/**
 * Created by matteosantamaria on 5/13/17.
 */

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder> {

    private Context context;
    private String uID;
    private List<String> groupIDList;
    private List<Group> groupList;

    public GroupRecyclerAdapter(Context context, String uID) {
        this.context = context;
        this.uID = uID;
        groupIDList = new ArrayList<String>();
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
        Log.d("test", "bind called");
        holder.groupTitle.setText(groupIDList.get(position));
    }

    @Override
    public int getItemCount() {
        return groupIDList.size();
//        return 0;
    }

    public void addGroupID(String groupID) {
        groupIDList.add(groupID);
        notifyDataSetChanged();
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
