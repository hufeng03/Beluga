package com.hufeng.filemanager;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hufeng.filemanager.data.BelugaFileEntry;
import com.hufeng.filemanager.data.BelugaZipElementEntry;
import com.hufeng.filemanager.helper.BelugaHighlightHelper;
import com.hufeng.filemanager.helper.BelugaTimeHelper;
import com.hufeng.filemanager.ui.BelugaActionController;
import com.hufeng.filemanager.view.SquareLazyLoadImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Feng Hu on 15-02-02.
 * <p/>
 * TODO: Add a class header comment.
 */
public class ZipElementEntryGridViewHolder extends BelugaEntryViewHolder {

    @InjectView(R.id.icon) SquareLazyLoadImageView icon;
    @InjectView(R.id.expand) ImageView expand;
    @InjectView(R.id.mask) View mask;
    @InjectView(R.id.check) ImageView check;
    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.description) TextView description;

    private BelugaZipElementEntry entry;
    private BelugaActionController actionController;
    private EntryClickListener listener;

    public ZipElementEntryGridViewHolder(View itemView, BelugaActionController actionController,
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
    public void bindEntry(BelugaEntry entry, String highlightString) {
        if (!(entry instanceof BelugaZipElementEntry)) {
            //throw new Exception("AppEntryViewHolder can only bind AppEntry");
        }
        this.entry = (BelugaZipElementEntry)entry;
//        this.name.setText(entry.getName());
        BelugaHighlightHelper.setTextWithHighlight(name, this.entry.getName(), highlightString);
        this.description.setText(BelugaTimeHelper.getDayString(this.entry.getTime()));
        icon.requestDisplayImageForZipElement(this.entry);
//        boolean isChosen = actionController.isEntrySelected(this.entry);
        boolean isChosen = false;
        this.itemView.setActivated(isChosen);
        this.check.setVisibility(isChosen?View.VISIBLE:View.GONE);
        this.expand.setVisibility(isChosen?View.VISIBLE:View.GONE);
    }

    @Override
    public void bindEntry(Cursor cursor, String highlightString) {
        BelugaFileEntry entry = new BelugaFileEntry(cursor);
        bindEntry(entry, highlightString);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.expand) {
            this.listener.onEntryClickedToOpen(v, entry);
        } else {
            if (actionController.isActionModeShowing()) {
//                actionController.toggleEntrySelection(entry);
            } else {
                this.listener.onEntryClickedToOpen(v, entry);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
//        actionController.toggleEntrySelection(entry);
        return true;
    }
}