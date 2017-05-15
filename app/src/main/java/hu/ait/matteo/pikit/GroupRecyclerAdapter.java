package hu.ait.matteo.pikit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
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
import java.util.List;

import hu.ait.matteo.pikit.data.Group;

/**
 * Created by matteosantamaria on 5/13/17.
 */

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder> {

    private Context context;
    private String uID;
//    private List<String> myGroupIDs;
    private List<Group> groupList;

    public GroupRecyclerAdapter(Context context, String uID) {
        this.context = context;
        this.uID = uID;
        groupList = new ArrayList<Group>();
//        myGroupIDs = new ArrayList<String>();

//        initGroupListener();
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

                    String groupID = groupList.get(position).getUniqueID();
                    intent.putExtra("groupID", groupID);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }


//    public void initGroupListener() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("test", dataSnapshot.toString());
//                for (DataSnapshot data: dataSnapshot.getChildren()) {
//                    Log.d("TEST", data.toString());
//                    Log.d("TESTIDS", myGroupIDs.toString());
//                    if (myGroupIDs.contains(data.getKey().toString())) {
//                        Log.d("TEST2", "here");
//                        Group group = data.getValue(Group.class);
//                        Log.d("TEST", group.getName());
//                        groupList.add(group);
//                    }
//                }
//                Log.d("TEST!!!!", groupList.toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void addGroup(Group group) {
        groupList.add(group);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView groupTitle;
        public TextView memberCount;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            groupTitle = (TextView) itemView.findViewById(R.id.groupTitle);
            memberCount = (TextView) itemView.findViewById(R.id.memberCount);
        }
    }
}
