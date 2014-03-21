package com.hufeng.filemanager.ui;

import android.app.Activity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.IconLoader;
import com.hufeng.filemanager.browser.IconLoaderHelper;
import com.hufeng.filemanager.treeview.AbstractTreeViewAdapter;
import com.hufeng.filemanager.treeview.TreeNodeInfo;
import com.hufeng.filemanager.treeview.TreeStateManager;
import com.hufeng.playimage.MyLazyLoadImageView;


public class FileTreeAdapter extends AbstractTreeViewAdapter<String>{

//	private final Set<Long> selected;
//	private final String[] mFilePaths;
	private final IconLoader mIconLoader;
	
    private final OnCheckedChangeListener onCheckedChange = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final CompoundButton buttonView,
                final boolean isChecked) {
            final Long id = (Long) buttonView.getTag();
            changeSelected(isChecked, id);
        }

    };

    private void changeSelected(final boolean isChecked, final Long id) {
//        if (isChecked) {
//            selected.add(id);
//        } else {
//            selected.remove(id);
//        }
    }
	
	public FileTreeAdapter(Activity activity,
            final TreeStateManager<String> treeStateManager,
            final int numberOfLevels) {
		super(activity, treeStateManager, numberOfLevels);
        mIconLoader = IconLoader.getInstance();
	}

	private String getDescription(final String path) {
       // final Integer[] hierarchy = getManager().getHierarchyDescription(id);
       // return "Node " + id + Arrays.asList(hierarchy);
       // String path = mFilePaths[(int)id];
        String name = path.substring(path.lastIndexOf("/")+1);
        return name;
    }

    @Override
    public View getNewChildView(final TreeNodeInfo<String> treeNodeInfo) {
        final LinearLayout viewLayout = (LinearLayout) getActivity()
                .getLayoutInflater().inflate(R.layout.file_tree_item, null);
        return updateView(viewLayout, treeNodeInfo);
    }

    @Override
    public LinearLayout updateView(final View view,
            final TreeNodeInfo<String> treeNodeInfo) {
        final LinearLayout viewLayout = (LinearLayout) view;
        final TextView descriptionView = (TextView) viewLayout
                .findViewById(R.id.file_tree_item_name);
        final MyLazyLoadImageView imageView = (MyLazyLoadImageView) viewLayout
                .findViewById(R.id.file_tree_item_image);
        String path = treeNodeInfo.getId();
        descriptionView.setText(getDescription(path));
        //levelView.setText(Integer.toString(treeNodeInfo.getLevel()));
//        mIconLoader.loadIcon(imageView, null, null, path);
        imageView.setDefaultResource(IconLoaderHelper.getFileIcon(view.getContext(), path));
        imageView.requestDisplayLocalThumbnail(path);


//        final CheckBox box = (CheckBox) viewLayout
//                .findViewById(R.id.demo_list_checkbox);
//        box.setTag(treeNodeInfo.getId());
//        if (treeNodeInfo.isWithChildren()) {
//            box.setVisibility(View.GONE);
//        } else {
//            box.setVisibility(View.VISIBLE);
//            box.setChecked(selected.contains(treeNodeInfo.getId()));
//        }
//        box.setOnCheckedChangeListener(onCheckedChange);
        return viewLayout;
    }

    @Override
    public void handleItemClick(final View view, final Object id) {
        final String longId = (String) id;
        final TreeNodeInfo<String> info = getManager().getNodeInfo(longId);
        if (info.isWithChildren()) {
            super.handleItemClick(view, id);
        } else {
//            final ViewGroup vg = (ViewGroup) view;
//            final CheckBox cb = (CheckBox) vg
//                    .findViewById(R.id.demo_list_checkbox);
//            cb.performClick();
        }
    }
    
    public int getFilePosition(String path){
    	return getTreePosition(path);
    }

    @Override
    public long getItemId(final int position) {
       // return getTreeId(position);
    	return 0;
    }
}
