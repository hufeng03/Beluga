package com.hufeng.filemanager;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.BaseAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.hufeng.filemanager.browser.FileSorter;
import com.hufeng.filemanager.ui.FileOperation;
import com.hufeng.filemanager.utils.GridViewUtils;

/**
 * Created by feng on 13-9-9.
 */
public abstract class FileGridFragment extends GridFragment{

    private static final String TAG = FileGridFragment.class.getSimpleName();

    protected String mSearchString;
    protected int mCategory;

    protected int mMenuId;

    protected FileOperation mFileOperation = null;

    private FileOperationActivity mOperationActivity;

    public FileGridFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOperationActivity = (FileOperationActivity)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOperationActivity = null;
    }

    public void setFileOperation(FileOperation operation) {
        mFileOperation = operation;
    }

    public FileOperation getFileOperation() {
        if (mFileOperation != null) {
            return mFileOperation;
        } else {
            return mOperationActivity == null ? null : mOperationActivity.getGlobalFileOperation();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "onCreateOptionsMenu with menuId = "+mMenuId);
        menu.clear();
        if (mMenuId == 0) {
            return;
        }
        inflater.inflate(mMenuId, menu);

        if( getDisplayMode() == DISPLAY_MODE.LIST ) {
            if(menu.findItem(R.id.menu_display)!=null)
                menu.findItem(R.id.menu_display).setIcon(R.drawable.ic_menu_display_as_grid_holo_light);
        }else{
            if(menu.findItem(R.id.menu_display)!=null)
                menu.findItem(R.id.menu_display).setIcon(R.drawable.ic_menu_display_as_list_holo_light);
        }

        if(menu.findItem(R.id.menu_search)!=null) {
            if (getSherlockActivity() == null) {
                super.onCreateOptionsMenu(menu, inflater);
                return;
            }
            SearchManager searchManager = (SearchManager) getSherlockActivity()
                    .getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
                    .getActionView();
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getSherlockActivity().getComponentName()));

            menu.findItem(R.id.menu_search).setOnActionExpandListener(new MenuItem.OnActionExpandListener(){

                int navigation_mode = ActionBar.NAVIGATION_MODE_STANDARD;

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    navigation_mode = (getSherlockActivity()).getSupportActionBar().getNavigationMode();
                    (getSherlockActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    (getSherlockActivity()).getSupportActionBar().setNavigationMode(navigation_mode);
                    return true;
                }

            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    // TODO Auto-generated method stub
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if ((mSearchString == null && !TextUtils.isEmpty(newText)) ||
                            (newText == null && !TextUtils.isEmpty(mSearchString)) ||
                            (mSearchString != null && !mSearchString.equals(newText))) {
                        mSearchString = newText;
                        reloadFiles();
                    }
                    return true;
                }

            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {

                @Override
                public boolean onClose() {
                   return false;
                }
            });

        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        switch(item.getItemId()) {
            case R.id.menu_back:
                getSherlockActivity().onBackPressed();
                break;
            case R.id.menu_display:
                GridViewUtils.stopScroll(getGridView());
                switchDisplayMode();
                getSherlockActivity().invalidateOptionsMenu();
                break;
            case R.id.menu_create:
//                Fragment fragment = getParentFragment();
//                if (fragment!=null) {
//                    FmDialogFragment.showCreateDirectoryDialog(fragment, root);
//                }
//                fileOperation.onOperationCreate(getActivity());
                getFileOperation().onOperationCreate(getActivity());
                break;
            case R.id.menu_sort_by_date_asc:
                sort(FileSorter.SORT_FIELD.DATE, FileSorter.SORT_ORDER.ASC);
                break;
            case R.id.menu_sort_by_date_desc:
                sort(FileSorter.SORT_FIELD.DATE, FileSorter.SORT_ORDER.DESC);
                break;
            case R.id.menu_sort_by_extension_asc:
                sort(FileSorter.SORT_FIELD.EXTENSION, FileSorter.SORT_ORDER.ASC);
                break;
            case R.id.menu_sort_by_extension_desc:
                sort(FileSorter.SORT_FIELD.EXTENSION, FileSorter.SORT_ORDER.DESC);
                break;
            case R.id.menu_sort_by_size_asc:
                sort(FileSorter.SORT_FIELD.SIZE, FileSorter.SORT_ORDER.ASC);
                break;
            case R.id.menu_sort_by_size_desc:
                sort(FileSorter.SORT_FIELD.SIZE, FileSorter.SORT_ORDER.DESC);
                break;
            case R.id.menu_sort_by_name_asc:
                sort(FileSorter.SORT_FIELD.NAME, FileSorter.SORT_ORDER.ASC);
                break;
            case R.id.menu_sort_by_name_desc:
                sort(FileSorter.SORT_FIELD.NAME, FileSorter.SORT_ORDER.DESC);
                break;
            case R.id.menu_paste_confirm:
                getFileOperation().onOperationPasteConfirm(getActivity());
                break;
            case R.id.menu_paste_cancel:
                getFileOperation().onOperationPasteCancel(getActivity());
                break;
            default:
                break;
        }
        return true;
    }



//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        Log.i(TAG, "onCreateContextMenu");
//        android.view.MenuInflater inflater = getSherlockActivity().getMenuInflater();
//        FileViewHolder viewHolder = (FileViewHolder)((AdapterView.AdapterContextMenuInfo)menuInfo).targetView.getTag();
//        String path = viewHolder.path;
//
//
//        if(TextUtils.isEmpty(path) || path.startsWith("ad:")){
//            return;
//        }
//
//        if(path.startsWith("app:")) {
//            String package_name = path.substring(4);
//            PackageInfo info = null;
//            try {
//                info = v.getContext().getPackageManager().getPackageInfo(package_name, PackageManager.GET_UNINSTALLED_PACKAGES);
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//            if( info!=null ) {
//                inflater.inflate(R.menu.app_installed_menu, menu);
//            }
//            return;
//        }
//
//        boolean favorite = false;
//        boolean download = false;
//
//        String[] paths = FileUtils.getFavoriteFiles();
//        if(paths!=null)
//        {
//            for(String file:paths)
//            {
//                if(file.equals(path))
//                {
//                    favorite = true;
//                    break;
//                }
//            }
//        }
//
//        String[] paths2 = FileUtils.getDownloadDirs();
//        if(paths2!=null)
//        {
//            for(String file:paths2)
//            {
//                if(file.equals(path))
//                {
//                    download = true;
//                    break;
//                }
//            }
//        }
//
//        if (mSelection) {
//            inflater.inflate(R.menu.file_selection_menu, menu);
//        } else if(favorite) {
//            if(FileUtils.getFileType(new File(path))==FileUtils.FILE_TYPE_AUDIO)
//            {
//                inflater.inflate(R.menu.favorite_file_audio_menu, menu);
//            }
//            else if(FileUtils.getFileType(new File(path))==FileUtils.FILE_TYPE_IMAGE)
//            {
//                inflater.inflate(R.menu.favorite_file_image_menu, menu);
//            }
//            else if(new File(path).isDirectory())
//            {
//                inflater.inflate(R.menu.favorite_directory_normal_menu, menu);
//            }
//            else
//            {
//                inflater.inflate(R.menu.favorite_file_normal_menu, menu);
//            }
//        }
//        else
//        {
//            if(FileUtils.getFileType(new File(path))==FileUtils.FILE_TYPE_AUDIO)
//            {
//                inflater.inflate(R.menu.file_audio_menu, menu);
//            }
//            else if(FileUtils.getFileType(new File(path))==FileUtils.FILE_TYPE_IMAGE)
//            {
//                inflater.inflate(R.menu.file_image_menu, menu);
//            }
//            else if(new File(path).isDirectory())
//            {
//                inflater.inflate(R.menu.file_directory_menu, menu);
//            }
//            else
//            {
//                inflater.inflate(R.menu.file_normal_menu, menu);
//            }
//        }
//
//        if(new File(path).isDirectory())
//        {
//            if(!new File(path).getParentFile().canWrite())
//            {
//                menu.removeItem(R.id.menu_rename);
//            }
//        }
//        else
//        {
//            if(!new File(path).getParentFile().canWrite())
//            {
//                menu.removeItem(R.id.menu_rename);
//            }
//            if(new File(path).canRead()) {
//                if(ChannelUtil.isKanBoxChannel(v.getContext())/* && KanBoxApi.getInstance().getTokenStatus()== KanBoxApi.TOKEN_STATUS.VALID*/) {
//                    menu.add(0,R.id.menu_add_cloud, Menu.FIRST, R.string.menu_add_cloud);
//                }
//            }
//        }
//        if(download)
//        {
//            menu.removeItem(R.id.menu_rename);
//        }
//        if(path.endsWith(".apk")) {
//            PackageInfo info = null ;
//            try {
//                info = v.getContext().getPackageManager().getPackageArchiveInfo(path,
//                    PackageManager.GET_ACTIVITIES);
//            }catch(Exception e) {
//                e.printStackTrace();;
//            }
//            ApplicationInfo appInfo = null;
//            if (info != null) {
//                appInfo = info.applicationInfo;
//                String package_name = appInfo.packageName;
//                info = null;
//                try {
//                    info = v.getContext().getPackageManager().getPackageInfo(package_name, PackageManager.GET_UNINSTALLED_PACKAGES);
//                } catch (PackageManager.NameNotFoundException e) {
//                    e.printStackTrace();
//                }
//                if( info!=null ) {
//                    menu.add(0,R.id.menu_app_run, 0, R.string.menu_run);
//                    menu.add(0,R.id.menu_app_detail, 0, R.string.menu_app_detail);
//                }
//            }
//        }
////        if(!mSelection) {
////            if(ChannelUtil.isKanBoxChannel(v.getContext())) {
////                menu.add(0,R.id.menu_add_cloud,0,R.string.menu_add_cloud);
////            }
////        }
//    }
//
//
//    @Override
//    public boolean onContextItemSelected(android.view.MenuItem item) {
//        Log.i(TAG, "onContextItemSelected");
//
//        int group_id = item.getGroupId();
//        if(group_id == R.id.menu_cloud_group) {
//            return false;
//        }
//
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
//                .getMenuInfo();
//        FileViewHolder view = (FileViewHolder) info.targetView.getTag();
//        String path = view.path;
//        if (TextUtils.isEmpty(path)) {
//            return true;
//        }
//        FileOperation fileOperation = ((FileOperationActivity)getActivity()).getFileOperation();
//        switch (item.getItemId()) {
//            case R.id.menu_add_cloud: {
//                //fileOperation.onOperationAddToCloud(path);
//                if(KanBoxApi.getInstance().getTokenStatus()== KanBoxApi.TOKEN_STATUS.VALID) {
//                    Intent intent = new Intent(FileManagerTabActivity.ACTION_UPLOAD_FILES);
//                    intent.setClassName(getActivity().getPackageName(), FileManagerTabActivity.class.getName());
//                    intent.putExtra("files", new String[]{path});
//                    getActivity().startActivity(intent);
//                } else {
//                    Activity activity = getActivity();
//                    if(activity instanceof FileManagerTabActivity) {
//                        ((FileManagerTabActivity)activity).gotoCloud();
//                    }
//                }
//                break;
//            }
//            case R.id.menu_add_safe: {
//                fileOperation.onOperationAddToSafe(getActivity());
//                break;
//            }
//            case R.id.menu_add_favorite: {
//                fileOperation.onOperationAddFavorite(getActivity());
//                // FileAction.addToFavorite(path);
//                break;
//            }
//            case R.id.menu_remove_favorite: {
//                fileOperation.onOperationRemoveFavorite(getActivity());
//                // FileAction.removeFromFavorite(path);
//                break;
//            }
//            case R.id.menu_send: {
//                fileOperation.onOperationSend(getActivity());
//                break;
//            }
//            case R.id.menu_wallpaper: {
////			MobclickAgent.onEvent(getSherlockActivity(), "click_menu_wallpaper");
//                fileOperation.onOperationSetAsWallpaper(getActivity());
//                // FileAction.setAsWallpaper(new File(path));
//                break;
//            }
//            case R.id.menu_ringtone: {
////			MobclickAgent.onEvent(getSherlockActivity(), "click_menu_ringtone");
//                fileOperation.onOperationSetAsRingtone(getActivity());
//                // if(MultiSimUtil.isMultiSim_Spreadtrum_Eton())
//                // {
//                // FmDialogFragment.showMultiSimRingtoneDialog(getChildFragmentManager(),path);
//                // }
//                // else
//                // {
//                // FileAction.setAsRingTone(new File(path));
//                // }
//                break;
//            }
//            case R.id.menu_rename: {
////			MobclickAgent.onEvent(getSherlockActivity(), "click_menu_rename");
//                fileOperation.onOperationRename(getActivity());
//                // FmDialogFragment.showRenameDialog(getChildFragmentManager(),path);
//                break;
//            }
//            case R.id.menu_view_detail: {
////			MobclickAgent.onEvent(getSherlockActivity(), "click_menu_detail");
//                fileOperation.onOperationViewDetail(getActivity());
//                // FmDialogFragment.showDetailDialog(getChildFragmentManager(),path);
//                break;
//            }
//            case R.id.menu_app_run: {
//                String package_name = null;
//                if (path.startsWith("app:")) {
//                    package_name = path.substring(4);
//                } else if(path.endsWith(".apk")) {
//                    PackageInfo package_info = null ;
//                    try {
//                        package_info = getActivity().getPackageManager().getPackageArchiveInfo(path,
//                                PackageManager.GET_ACTIVITIES);
//                    }catch(Exception e) {
//                        e.printStackTrace();;
//                    }
//                    ApplicationInfo appInfo = null;
//                    if (package_info != null) {
//                        appInfo = package_info.applicationInfo;
//                        package_name = appInfo.packageName;
//                    }
//                }
//                if (!TextUtils.isEmpty(package_name)) {
//                    AppAction.launchApp(getActivity(), package_name);
//                }
//                break;
//            }
//            case R.id.menu_app_detail: {
//                String package_name = null;
//                if (path.startsWith("app:")) {
//                    package_name = path.substring(4);
//                } else if(path.endsWith(".apk")) {
//                    PackageInfo package_info = null ;
//                    try {
//                        package_info = getActivity().getPackageManager().getPackageArchiveInfo(path,
//                                PackageManager.GET_ACTIVITIES);
//                    }catch(Exception e) {
//                        e.printStackTrace();;
//                    }
//                    ApplicationInfo appInfo = null;
//                    if (package_info != null) {
//                        appInfo = package_info.applicationInfo;
//                        package_name = appInfo.packageName;
//                    }
//                }
//                if (!TextUtils.isEmpty(package_name)) {
//                    AppAction.showInstalledAppDetails(getActivity(), package_name);
//                }
//                break;
//            }
//        }
//        return true;
//    }


    private void sort(FileSorter.SORT_FIELD field, FileSorter.SORT_ORDER order) {
        FileSorter.saveFileSorter(getActivity(), mCategory, new FileSorter.SORTER(field, order));
        reloadFiles();
    }

    public void refreshUI() {
        ((BaseAdapter)getGridAdapter()).notifyDataSetChanged();
    }

    public abstract void reloadFiles();

    public abstract String[] getAllFiles();

    public abstract String getParentFile();

}
