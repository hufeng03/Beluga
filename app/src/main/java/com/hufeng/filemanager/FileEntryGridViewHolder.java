package com.hufeng.filemanager;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.filemanager.ui.BelugaActionController;
import com.hufeng.filemanager.utils.TimeUtil;
import com.hufeng.filemanager.view.SquareLazyLoadImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Feng Hu on 15-02-02.
 * <p/>
 * TODO: Add a class header comment.
 */
public class FileEntryGridViewHolder extends BelugaEntryViewHolder {


    @InjectView(R.id.icon) SquareLazyLoadImageView icon;
    @InjectView(R.id.expand) ImageView expand;
    @InjectView(R.id.mask) View mask;
    @InjectView(R.id.check) ImageView check;
    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.description) TextView description;

    private FileEntry entry;
    private BelugaActionController actionController;
    private EntryClickListener listener;

    public FileEntryGridViewHolder(View itemView, BelugaActionController actionController,
                                   EntryClickListener listener) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        icon.setOnClickListener(this);
        icon.setOnLongClickListener(this);
        expand.setOnClickListener(this);
        mask.setOnClickListener(this);
        mask.setOnLongClickListener(this);
        this.actionController = actionController;
        this.listener = listener;
    }

    @Override
    public void bindEntry(BelugaEntry entry) {
        if (!(entry instanceof FileEntry)) {
            //throw new Exception("AppEntryViewHolder can only bind AppEntry");
        }
        this.entry = (FileEntry)entry;
        this.name.setText(entry.getName());
        this.description.setText(TimeUtil.getDayString(this.entry.getTime()));
        icon.requestDisplayImage(this.entry.path);
        boolean isChoosen = actionController.isEntrySelected(this.entry);
        this.itemView.setActivated(isChoosen);
        this.check.setVisibility(isChoosen?View.VISIBLE:View.GONE);
        this.expand.setVisibility(isChoosen?View.VISIBLE:View.GONE);
    }

    @Override
    public void bindEntry(Cursor cursor) {
        FileEntry entry = new FileEntry(cursor);
        bindEntry(entry);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.expand) {
            this.listener.onEntryClickedToOpen(v, entry);
        } else {
            if (actionController.isActionModeShowing()) {
                actionController.toggleEntrySelection(entry);
            } else {
                this.listener.onEntryClickedToOpen(v, entry);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        actionController.toggleEntrySelection(entry);
        return true;
    }
}
