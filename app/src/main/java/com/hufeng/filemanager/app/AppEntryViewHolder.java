package com.hufeng.filemanager.app;

import android.content.Intent;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.hufeng.filemanager.BelugaEntry;
import com.hufeng.filemanager.BelugaEntryViewHolder;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.helper.BelugaHighlightHelper;
import com.hufeng.filemanager.helper.BelugaTimeHelper;
import com.hufeng.filemanager.utils.SizeUtil;
import com.hufeng.playimage.BelugaLazyLoadImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Feng Hu on 15-01-25.
 * <p/>
 * TODO: Add a class header comment.
 */
public class AppEntryViewHolder extends BelugaEntryViewHolder {

    @InjectView(R.id.icon)
    BelugaLazyLoadImageView icon;
    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.description) TextView description;
    @InjectView(R.id.status) TextView status;
    @InjectView(R.id.overflow) ImageView overflow;

    private AppEntry entry;

    public AppEntryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        overflow.setOnClickListener(this);
    }

    @Override
    public void bindEntry(BelugaEntry entry, String highlightString) {
        if (!(entry instanceof AppEntry)) {
            //throw new Exception("AppEntryViewHolder can only bind AppEntry");
        }
        this.entry = (AppEntry)entry;
//        name.setText(this.entry.getName());
        BelugaHighlightHelper.setTextWithHighlight(name, this.entry.getName(), highlightString);
        icon.setImageDrawable(this.entry.getIcon());
        status.setText(SizeUtil.normalize(this.entry.apkEntry.size));
        description.setText(BelugaTimeHelper.getDateString(this.entry.apkEntry.lastModified));
        this.overflow.setTag(this.entry.packageName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.overflow:
                showAppMenu(v);
                break;
            default:
                showAppDetailsSettings();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        showAppMenu(overflow);
        return true;
    }

    private void showAppMenu(final View view) {
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(context, R.style.Widget_AppCompat_Light_PopupMenu);
        PopupMenu popupMenu = new PopupMenu(contextWrapper, view);
        popupMenu.inflate(R.menu.app_item_overflow_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.open:
//                        Toast.makeText(context, "open", Toast.LENGTH_SHORT).show();
                        launchApp();
                        break;
                    case R.id.market:
                        showAppDetailsMarket();
                        break;
                }
                return false;
            }
        });
        ((ImageView)view).setImageResource(R.drawable.beluga_overflow_menu_open);
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                ((ImageView)view).setImageResource(R.drawable.beluga_overflow_menu);
            }
        });
        popupMenu.show();
    }

    private void launchApp() {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(this.entry.packageName);
        try{
            context.startActivity(intent);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void showAppDetailsMarket() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id="+this.entry.packageName));
        this.itemView.getContext().startActivity(intent);
    }

    private void showAppDetailsSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + this.entry.packageName));
        this.itemView.getContext().startActivity(intent);
    }

}
