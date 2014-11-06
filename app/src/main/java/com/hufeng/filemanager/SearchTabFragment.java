package com.hufeng.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.hufeng.filemanager.browser.FileUtils;

/**
 * Created by feng on 14-5-6.
 */
public class SearchTabFragment extends FileTabFragment {


    Spinner mSearchCategory;
    EditText mSearchString;
    private int mCategoryValue = FileUtils.FILE_TYPE_ALL;
    private String mSearchValue = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_tab_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showSearchFragment();

        mSearchCategory = (Spinner) view.findViewById(R.id.search_category);
        mSearchString = (EditText) view.findViewById(R.id.search_string);

        mSearchCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int old_category = mCategoryValue;
                switch (position) {
                    case 0:
                        mCategoryValue = FileUtils.FILE_TYPE_ALL;
                        break;
                    case 1:
                        mCategoryValue = FileUtils.FILE_TYPE_IMAGE;
                        break;
                    case 2:
                        mCategoryValue = FileUtils.FILE_TYPE_AUDIO;
                        break;
                    case 3:
                        mCategoryValue = FileUtils.FILE_TYPE_VIDEO;
                        break;
                    case 4:
                        mCategoryValue = FileUtils.FILE_TYPE_APK;
                        break;
                    case 5:
                        mCategoryValue = FileUtils.FILE_TYPE_ZIP;
                        break;
                    case 6:
                        mCategoryValue = FileUtils.FILE_TYPE_DOCUMENT;
                        break;
                    default:
                        mCategoryValue = FileUtils.FILE_TYPE_ALL;
                }

                if (old_category != mCategoryValue) {
                    performSearch();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                int old_category = mCategoryValue;
                mCategoryValue = FileUtils.FILE_TYPE_ALL;
                if (old_category != mCategoryValue) {
                    performSearch();
                }
            }
        });
        mSearchString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String old_search = mSearchValue;
                mSearchValue = s.toString();
                if (mSearchValue != old_search) {
                    performSearch();
                }
            }
        });
    }

    private void performSearch() {

    }

    public void showSearchFragment() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        SearchFragment fragment = (SearchFragment) getChildFragmentManager().findFragmentByTag(SearchFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new SearchFragment();
            ft.replace(R.id.fragment_container, fragment, SearchFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
        mCurrentChildFragment = fragment;
    }

    @Override
    public void refreshFiles() {

    }

    @Override
    public String[] getAllFiles() {
        return new String[0];
    }

    @Override
    public String getParentFile() {
        return null;
    }
}
