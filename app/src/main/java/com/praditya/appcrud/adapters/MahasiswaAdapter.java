package com.praditya.appcrud.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.praditya.appcrud.MainActivity;
import com.praditya.appcrud.R;
import com.praditya.appcrud.api.APIRequest;
import com.praditya.appcrud.api.RetrofitClient;
import com.praditya.appcrud.models.Mahasiswa;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.ListViewHolder> {
    private static final String TAG = MahasiswaAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<Mahasiswa> mahasiswaList;
    private OnItemClickCallback onItemClickCallback;

    public MahasiswaAdapter() {
        Log.d(TAG, "Adapter tersambung");
        mahasiswaList = new ArrayList<>();
    }

    public void setMahasiswaList(ArrayList<Mahasiswa> mahasiswaList) {
        this.mahasiswaList = mahasiswaList;
        notifyDataSetChanged();
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public MahasiswaAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: inside");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mahasiswa, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MahasiswaAdapter.ListViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: position " + position);
        Mahasiswa mahasiswa = mahasiswaList.get(position);
        holder.tvName.setText(mahasiswa.getNama());
        Glide.with(holder.itemView.getContext())
                .load(RetrofitClient.getImageUrl(mahasiswa.getFoto()))
                .placeholder(R.drawable.ic_account_circle_black_512dp)
                .into(holder.imgMahasiswa);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.detailMahasiswa(mahasiswaList.get(holder.getAdapterPosition()));
            }
        });
        holder.btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(), holder.btnOptions);
                popupMenu.inflate(R.menu.options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_edit :
                                onItemClickCallback.editMahasiswa(mahasiswaList.get(holder.getAdapterPosition()));
                                break;
                            case R.id.menu_delete :
                                onItemClickCallback.deleteMahasiswa(mahasiswaList.get(holder.getAdapterPosition()));
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mahasiswaList.size());
        return mahasiswaList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_mahasiswa) ImageView imgMahasiswa;
        @BindView(R.id.tv_mahasiswa_name) TextView tvName;
        @BindView(R.id.btn_options) ImageButton btnOptions;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickCallback {
        void editMahasiswa(Mahasiswa mahasiswa);
        void deleteMahasiswa(Mahasiswa mahasiswa);
        void detailMahasiswa(Mahasiswa mahasiswa);
    }
}
