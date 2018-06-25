package io.github.bctnry.tsubasareader;


import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.github.bctnry.tsubasareader.DAL.Generic;
import io.github.bctnry.tsubasareader.Model.FeedSource;


import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter {
    private List<File> fileList;
    public FileAdapter(File startingDirectory) {
        this.fileList = Arrays.asList(startingDirectory.listFiles());
    }
    public void update(File newDirectory) {
        this.fileList = Arrays.asList(newDirectory.listFiles());
    }
    public File getItemByPosition(int position) {
        return fileList.get(position);
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {
        public TextView tvType, tvPath;
        public FileViewHolder(View itemView) {
            super(itemView);
            this.tvType = (TextView)itemView.findViewById(R.id.type_item_file);
            this.tvPath = (TextView)itemView.findViewById(R.id.path_item_file);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new FileAdapter.FileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        File file = this.fileList.get(position);
        FileViewHolder fileViewHolder = (FileViewHolder)holder;
        fileViewHolder.tvType.setText(file.isFile()?"File":"Directory");
        fileViewHolder.tvPath.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        return this.fileList.size();
    }
}
