package com.belugamobile.filemanager;

import android.database.Cursor;
import android.support.v7.widget.PopupMenu;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.data.BelugaZipElementEntry;
import com.belugamobile.filemanager.helper.BelugaHighlightHelper;
import com.belugamobile.filemanager.helper.BelugaTimeHelper;
import com.belugamobile.filemanager.ui.BelugaActionController;
import com.belugamobile.filemanager.utils.SizeUtil;
import com.belugamobile.playimage.BelugaLazyLoadImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Feng Hu on 15-01-25.
 * <p/>
 * TODO: Add a class header comment.
 */
public class ZipElementEntryListViewHolder extends BelugaEntryViewHolder{

    @InjectView(R.id.icon)
    BelugaLazyLoadImageView icon;
    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.description) TextView description;
    @InjectView(R.id.status) TextView status;
    @InjectView(R.id.check) ImageView check;
    @InjectView(R.id.overflow) ImageView overflow;

    private BelugaZipElementEntry entry;
    private BelugaActionController actionController;
    private EntryClickListener listener;

    public ZipElementEntryListViewHolder(View itemView, BelugaActionController actionController,
                                         EntryClickListener listener) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        icon.setOnClickListener(this);
        icon.setOnLongClickListener(this);
        this.actionController = actionController;
        this.listener = listener;
    }

    @Override
    public void bindEntry(BelugaEntry entry, String highlightString) {
        if (!(entry instanceof BelugaZipElementEntry)) {
            //throw new Exception("AppEntryViewHolder can only bind AppEntry");
        }
        this.entry = (BelugaZipElementEntry)entry;
//        name.setText(this.entry.getName());
        BelugaHighlightHelper.setTextWithHighlight(name, this.entry.getName(), highlightString);
        icon.requestDisplayImageForZipElement(this.entry);
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
        description.setText(BelugaTimeHelper.getDateString(this.entry.getTime()));
//        boolean isChosen = actionController.isEntrySelected(this.entry);
        boolean isChosen = false;
        this.itemView.setActivated(isChosen);
        this.overflow.setOnClickListener(this);
        final BelugaActionController.OPERATION_MODE operationMode = this.actionController.getOperationMode();
        final boolean isPasteMode =  operationMode == BelugaActionController.OPERATION_MODE.COPY_PASTE
                || operationMode == BelugaActionController.OPERATION_MODE.CUT_PASTE;
        this.overflow.setVisibility(/*isPasteMode?*/ View.GONE/*:View.VISIBLE*/);
        this.status.setVisibility(isPasteMode? View.GONE:View.VISIBLE);
    }

    @Override
    public void bindEntry(Cursor cursor, String highlightString) {
        BelugaFileEntry entry = new BelugaFileEntry(cursor);
        bindEntry(entry, highlightString);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.overflow:
                showContextMenu(v);
                break;
            case R.id.icon:
//                actionController.toggleEntrySelection(entry);
                break;
            default:
                this.listener.onEntryClickedToOpen(v, entry);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
//        actionController.toggleEntrySelection(entry);
        return true;
    }

    private void showContextMenu(final View view) {
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(context, R.style.Widget_AppCompat_Light_PopupMenu);
        PopupMenu popupMenu = new PopupMenu(contextWrapper, view);
        popupMenu.inflate(R.menu.file_item_overflow_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share:
//                        BelugaActionDelegate.share(context, entry);
                        break;
                    case R.id.copy:
//                        BelugaActionDelegate.copy(context, entry);
                        break;
                    case R.id.cut:
//                        BelugaActionDelegate.cut(context, entry);
                        break;
                    case R.id.delete:
//                        BelugaActionDelegate.delete((FragmentActivity) context, entry);
                        break;
                    case R.id.rename:
//                        BelugaActionDelegate.rename((FragmentActivity) context, entry);
                        break;
                    case R.id.details:
//                        BelugaActionDelegate.details((FragmentActivity) context, entry);
                        break;
                    case R.id.add_favorite:
//                        actionController.performFavorite(entry);
                        break;
                    case R.id.remove_favorite:
//                        actionController.performUndoFavorite(entry);
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
//        if (entry.isFavorite) {
//            popupMenu.getMenu().findItem(R.id.add_favorite).setEnabled(false);
//            popupMenu.getMenu().findItem(R.id.add_favorite).setVisible(false);
//        } else {
//            popupMenu.getMenu().findItem(R.id.remove_favorite).setEnabled(false);
//            popupMenu.getMenu().findItem(R.id.remove_favorite).setVisible(false);
//        }
        popupMenu.show();
    }


}
