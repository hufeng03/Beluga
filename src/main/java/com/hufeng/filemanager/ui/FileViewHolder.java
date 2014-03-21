package com.hufeng.filemanager.ui;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hufeng.filemanager.R;
import com.hufeng.playimage.MyLazyLoadImageView;

public class FileViewHolder {

    public interface FileViewClicked {
        public void onFileStatusClicked(String path, long position);
    }

    public FileViewHolder(View view){
        icon = (MyLazyLoadImageView) view.findViewById(R.id.icon);
        name = (TextView) view.findViewById(R.id.name);
        info  =(TextView) view.findViewById(R.id.info);
        status = (TextView) view.findViewById(R.id.status);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        clickListener = null;
    }

    public FileViewHolder(View view, final FileViewClicked callback) {
        icon = (MyLazyLoadImageView) view.findViewById(R.id.icon);
        name = (TextView) view.findViewById(R.id.name);
        info  =(TextView) view.findViewById(R.id.info);
        status = (TextView) view.findViewById(R.id.status);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        clickListener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onFileStatusClicked(path, position);
                }
            }
        };
        status.setOnClickListener(clickListener);
    }

    final private View.OnClickListener clickListener;

    public MyLazyLoadImageView icon;
    public TextView name;
    public TextView info;
    public TextView status;
    public ProgressBar progress;

    public long position;
    public String path;
    public int display;
}
