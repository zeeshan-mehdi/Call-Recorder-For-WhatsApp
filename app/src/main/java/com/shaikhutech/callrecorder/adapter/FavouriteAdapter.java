package com.shaikhutech.callrecorder.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
import com.shaikhutech.callrecorder.R;
import com.shaikhutech.callrecorder.pojo_classes.Contacts;


public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder7> {
    private static ArrayList<Contacts> contacts=new ArrayList<>();
    private final int VIEW1 = 0, VIEW2 = 1;
    Context ctx;
    static OnitemClickListener listener;
    public FavouriteAdapter(){

    }
    @Override
    public  FavouriteAdapter.MyViewHolder7 onCreateViewHolder(ViewGroup parent, int viewType) {
        FavouriteAdapter.MyViewHolder7 viewHolder;
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW1:
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_layout_row,parent,false);
                viewHolder = new  FavouriteAdapter.MyViewHolder7(view);
                ctx=view.getContext();
                break;
            case VIEW2:
                View v2 = inflater.inflate(R.layout.favourite_layout_row,parent, false);
                viewHolder = new  FavouriteAdapter.MyViewHolder7(v2);
                ctx=v2.getContext();
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new  FavouriteAdapter.MyViewHolder7(v);
                ctx=v.getContext();
                break;
        }
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(FavouriteAdapter.MyViewHolder7 holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW1:
                holder.name.setText(contacts.get(position).getName());
                holder.number.setText(contacts.get(position).getNumber());
                Picasso.with(ctx)
                        .load(contacts.get(position).getPhotoUri()).placeholder(R.drawable.profile)
                        .into(holder.profileimage);
                break;
            case VIEW2:
                holder.number.setText(contacts.get(position).getNumber());
                holder.name.setText("unsaved");
                holder.profileimage.setImageResource(R.drawable.unknown);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }


    public static class MyViewHolder7 extends RecyclerView.ViewHolder{
        CircleImageView profileimage;
        TextView name;
        TextView number;

        public MyViewHolder7(View itemView) {
            super(itemView);
            profileimage=(CircleImageView)itemView.findViewById(R.id.profile_image);
            name=(TextView)itemView.findViewById(R.id.textView2);
            number=(TextView)itemView.findViewById(R.id.textView3);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(view,getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(contacts.get(position).getName()!=null){
            return VIEW1;
        }else{
            return VIEW2;
        }
    }
    public void setContacts(ArrayList<Contacts> contacts){
        FavouriteAdapter.contacts=contacts;

    }
    public  void setListener(FavouriteAdapter.OnitemClickListener listener) {
        FavouriteAdapter.listener = listener;
    }
    public interface OnitemClickListener{
        public void onClick(View v,int position);
    }
}
