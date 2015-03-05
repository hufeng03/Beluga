package com.hufeng.filemanager;

import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.filemanager.ui.BelugaActionController;
import com.hufeng.filemanager.utils.SizeUtil;
import com.hufeng.filemanager.utils.TimeUtil;
import com.hufeng.playimage.BelugaLazyLoadImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Feng Hu on 15-01-25.
 * <p/>
 * TODO: Add a class header comment.
 */
public class FileEntryListViewHolder extends BelugaEntryViewHolder{

    @InjectView(R.id.icon)
    BelugaLazyLoadImageView icon;
    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.description) TextView description;
    @InjectView(R.id.status) TextView status;
    @InjectView(R.id.check) ImageView check;
    @InjectView(R.id.overflow) ImageView overflow;

    private FileEntry entry;
    private BelugaActionController actionController;
    private EntryClickListener listener;

    public FileEntryListViewHolder(View itemView, BelugaActionController actionController,
                                   EntryClickListener listener) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        icon.setOnClickListener(this);
        icon.setOnLongClickListener(this);
        this.actionController = actionController;
        this.listener = listener;
    }

    @Override
    public void bindEntry(BelugaEntry entry) {
        if (!(entry instanceof FileEntry)) {
            //throw new Exception("AppEntryViewHolder can only bind AppEntry");
        }
        this.entry = (FileEntry)entry;
        name.setText(this.entry.getName());
        icon.requestDisplayImage(this.entry.path);
        if (this.entry.isDirectory) {
            int childCount = this.entry.childFileCount + this.entry.childFolderCount;
            if (childCount == 0) {
                status.setText(R.string.none_child_file);
            } else if (childCount == 1) {
                status.setText(R.string.single_child_file);
            } else {
                status.setText(context.getString(R.string.multiple_child_file, childCount));
            }
        } else {
            status.setText(SizeUtil.normalize(this.entry.getSize()));
        }
        description.setText(TimeUtil.getDateString(this.entry.getTime()));
        boolean isChosen = actionController.isEntrySelected(this.entry);
        this.itemView.setActivated(isChosen);
        this.overflow.setOnClickListener(this);
        final BelugaActionController.OPERATION_MODE operationMode = this.actionController.getOperationMode();
        final boolean isPasteMode =  operationMode == BelugaActionController.OPERATION_MODE.COPY_PASTE
                || operationMode == BelugaActionController.OPERATION_MODE.CUT_PASTE;
        this.overflow.setVisibility(isPasteMode? View.GONE:View.VISIBLE);
        this.status.setVisibility(isPasteMode? View.GONE:View.VISIBLE);
    }

    @Override
    public void bindEntry(Cursor cursor) {
        FileEntry entry = new FileEntry(cursor);
        bindEntry(entry);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.overflow:
                showContextMenu(v);
                break;
            case R.id.icon:
                actionController.toggleEntrySelection(entry);
                break;
            default:
                this.listener.onEntryClickedToOpen(v, entry);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        actionController.toggleEntrySelection(entry);
        return true;
    }

    private void showContextMenu(final View view) {
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(context, R.style.Widget_AppCompat_Light_PopupMenu);
        PopupMenu popupMenu = new PopupMenu(contextWrapper, view);
        popupMenu.inflate(R.menu.file_overflow);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share:
                        BelugaActionDelegate.share(context, entry);
                        break;
                    case R.id.copy:
                        BelugaActionDelegate.copy(context, entry);
                        break;
                    case R.id.cut:
                        BelugaActionDelegate.cut(context, entry);
                        break;
                    case R.id.delete:
                        BelugaActionDelegate.delete((FragmentActivity) context, entry);
                        break;
                    case R.id.rename:
                        BelugaActionDelegate.rename((FragmentActivity) context, entry);
                        break;
                    case R.id.details:
                        BelugaActionDelegate.details((FragmentActivity) context, entry);
                        break;
                    case R.id.add_favorite:
                        actionController.performFavorite(entry);
                        break;
                    case R.id.remove_favorite:
                        actionController.performUndoFavorite(entry);
                        break;
                }
                return true;
            }
        });
        ((ImageView)view).setImageResource(R.drawable.beluga_overflow_menu_open);
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                ((ImageView) view).setImageResource(R.drawable.beluga_overflow_menu);
            }
        });
        if (FileAction.isFavorite(entry.path)) {
            popupMenu.getMenu().findItem(R.id.add_favorite).setEnabled(false);
            popupMenu.getMenu().findItem(R.id.add_favorite).setVisible(false);
        } else {
            popupMenu.getMenu().findItem(R.id.remove_favorite).setEnabled(false);
            popupMenu.getMenu().findItem(R.id.remove_favorite).setVisible(false);
        }
        popupMenu.show();
    }


}
