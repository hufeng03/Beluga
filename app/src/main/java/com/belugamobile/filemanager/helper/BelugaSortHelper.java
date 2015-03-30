package com.belugamobile.filemanager.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.belugamobile.filemanager.BelugaSortableInterface;
import com.belugamobile.filemanager.CategorySelectEvent;
import com.belugamobile.filemanager.FileManager;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;

public class BelugaSortHelper {

    private static final String TAG = BelugaSortHelper.class.getSimpleName();

    public static final String SORT_PREFERENCE_NAME = "sorter_preferences";

    private static HashMap<Integer, SORTER> mCategoryDefaultSorter;


    static {
        mCategoryDefaultSorter = new HashMap<Integer, SORTER>();
        mCategoryDefaultSorter.put(FileCategoryHelper.CATEGORY_TYPE_UNKNOW, new SORTER(SORT_FIELD.NAME, SORT_ORDER.ASC));
        mCategoryDefaultSorter.put(FileCategoryHelper.CATEGORY_TYPE_IMAGE, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(FileCategoryHelper.CATEGORY_TYPE_AUDIO, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(FileCategoryHelper.CATEGORY_TYPE_VIDEO, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(FileCategoryHelper.CATEGORY_TYPE_DOCUMENT, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(FileCategoryHelper.CATEGORY_TYPE_APK, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(FileCategoryHelper.CATEGORY_TYPE_ZIP, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));

        //Other that are not local file category
        mCategoryDefaultSorter.put(FileCategoryHelper.CATEGORY_TYPE_APP, new SORTER(SORT_FIELD.NAME, SORT_ORDER.ASC));
        mCategoryDefaultSorter.put(FileCategoryHelper.CATEGORY_TYPE_FAVORITE, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(FileCategoryHelper.CATEGORY_TYPE_DOWNLOAD, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
    }

    public static class SORTER {
        public SORT_FIELD field;
        public SORT_ORDER order;

        public SORTER(SORT_FIELD field, SORT_ORDER order) {
            this.field = field;
            this.order = order;
        }

        @Override
        public boolean equals(Object o) {
            return o!=null && o instanceof SORTER && ((SORTER) o).field == field && ((SORTER) o).order == order;
        }
    }

    public enum SORT_FIELD{
        DATE, NAME, SIZE, EXTENSION, IDENTITY;
    }

    public enum SORT_ORDER {
        ASC, DESC;
    }

    public static void saveFileSorter(int category, SORTER sorter) {
        SharedPreferences.Editor preferenceEditor = FileManager.getAppContext().getSharedPreferences(SORT_PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        preferenceEditor.putString("sort_field_" + category, sorter.field.toString());
        preferenceEditor.putString("sort_order_" + category, sorter.order.toString());
        preferenceEditor.commit();
    }
    
    public static SORTER getFileSorter(int category) {
        SORTER defaultSorter = mCategoryDefaultSorter.get(category);

        if (defaultSorter == null) {
            defaultSorter = mCategoryDefaultSorter.get(FileCategoryHelper.CATEGORY_TYPE_UNKNOW);
        }

        SharedPreferences preferences = FileManager.getAppContext().getSharedPreferences(SORT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String sortField = preferences.getString("sort_field_" + category, defaultSorter.field.toString());
        String sortOrder = preferences.getString("sort_order_" + category, defaultSorter.order.toString());

        return new SORTER(SORT_FIELD.valueOf(sortField), SORT_ORDER.valueOf(sortOrder));
    }

    public static Comparator<BelugaSortableInterface> getComparator(int category){
        SORTER sorter = getFileSorter(category);
        return getComparator(sorter.field, sorter.order);
    }

	public static Comparator<BelugaSortableInterface> getComparator(final SORT_FIELD field, final SORT_ORDER order){
		switch(field){
		case NAME:
			if(SORT_ORDER.ASC == order){
				return COMPARATOR_NAME_ASC;
			}else{
				return COMPARATOR_NAME_DESC;
			}
		case EXTENSION:
			if(SORT_ORDER.ASC == order){
				return COMPARATOR_EXTENSION_ASC;
			}else{
				return COMPARATOR_EXTENSION_DESC;
			}
		case SIZE:
			if(SORT_ORDER.ASC == order){
				return COMPARATOR_SIZE_ASC;
			}else{
				return COMPARATOR_SIZE_DESC;
			}
        case IDENTITY:
            if(SORT_ORDER.ASC == order){
                return COMPARATOR_IDENTITY_ASC;
            }else{
                return COMPARATOR_IDENTITY_DESC;
            }
		case DATE:
		default:
			if(SORT_ORDER.ASC == order){
				return COMPARATOR_DATE_ASC;
			}else{
				return COMPARATOR_DATE_DESC;
			}
		}
	}

    private static final Collator sCollator = Collator.getInstance();
	
	public static final Comparator<BelugaSortableInterface> COMPARATOR_NAME_ASC = new Comparator<BelugaSortableInterface>() {
		@Override
		public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            String lhs_name = lhs.getName();
            String rhs_name = rhs.getName();
            if(lhs_name.startsWith(".") && !rhs_name.startsWith(".")){
                return 1;
            }else if(rhs_name.startsWith(".") && !lhs_name.startsWith(".")) {
                return -1;
            } else {
                return sCollator.compare(lhs_name, rhs_name);
            }
	    }
	};

    public static final Comparator<BelugaSortableInterface> COMPARATOR_IDENTITY_ASC = new Comparator<BelugaSortableInterface>() {
        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            return sCollator.compare(lhs.getIdentity(), rhs.getIdentity());
        }
    };

    public static final Comparator<BelugaSortableInterface> COMPARATOR_NAME_DESC = new Comparator<BelugaSortableInterface>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            return sCollator.compare(rhs.getName(), lhs.getName());
        }
    };

    public static final Comparator<BelugaSortableInterface> COMPARATOR_IDENTITY_DESC = new Comparator<BelugaSortableInterface>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            return sCollator.compare(rhs.getIdentity(), lhs.getIdentity());
        }
    };


    public static final Comparator<BelugaSortableInterface> COMPARATOR_EXTENSION_ASC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            String lhs_name = lhs.getName().toLowerCase();
            String rhs_name = rhs.getName().toLowerCase();
            return compare_extension_asc(lhs_name, rhs_name);
        }

    };

    public static Comparator<BelugaSortableInterface> COMPARATOR_EXTENSION_DESC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            String lhs_name = lhs.getName().toLowerCase();
            String rhs_name = rhs.getName().toLowerCase();
            return compare_extension_desc(lhs_name, rhs_name);
        }

    };


    public static Comparator<BelugaSortableInterface> COMPARATOR_SIZE_ASC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            long lhs_size = lhs.getSize();
            long rhs_size = rhs.getSize();

            return lhs_size>rhs_size?1:(lhs_size<rhs_size?-1:0);
        }

    };


    public static Comparator<BelugaSortableInterface> COMPARATOR_SIZE_DESC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            long lhs_size = lhs.getSize();
            long rhs_size = rhs.getSize();

            return rhs_size>lhs_size?1:(rhs_size<lhs_size?-1:0);
        }

    };


    public static Comparator<BelugaSortableInterface> COMPARATOR_DATE_ASC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            long lhs_date = lhs.getTime();
            long rhs_date = rhs.getTime();

            return lhs_date>rhs_date?1:(lhs_date<rhs_date?-1:0);
        }

    };

    public static Comparator<BelugaSortableInterface> COMPARATOR_DATE_DESC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            long lhs_date = lhs.getTime();
            long rhs_date = rhs.getTime();

            return rhs_date>lhs_date?1:(rhs_date<lhs_date?-1:0);
        }
    };

    public static int compare_extension_asc(String lhs_name, String rhs_name/*, boolean lhs_directory, boolean rhs_directory*/) {
        int lhs_idx = lhs_name.lastIndexOf(".");
        int rhs_idx = rhs_name.lastIndexOf(".");
        String lhs_extension = (lhs_idx <= 0)?"":lhs_name.substring(lhs_idx+1);
        String rhs_extension = (rhs_idx <= 0)?"":rhs_name.substring(rhs_idx+1);
        int result = 0;
            if(lhs_name.startsWith(".") && !rhs_name.startsWith(".")){
                result = 1;
            }else if(rhs_name.startsWith(".") && !lhs_name.startsWith(".")){
                result = -1;
            }else {
                if (TextUtils.isEmpty(lhs_extension) && !TextUtils.isEmpty(rhs_extension)) {
                    result = -1;
                } else if (TextUtils.isEmpty(rhs_extension) && !TextUtils.isEmpty(lhs_extension)) {
                    result = 1;
                } else {
                    result = lhs_extension.compareTo(rhs_extension);
                }
            }
        return result;
    }

    public static int compare_extension_desc(String lhs_name, String rhs_name/*, boolean lhs_directory, boolean rhs_directory*/) {
        int lhs_idx = lhs_name.lastIndexOf(".");
        int rhs_idx = rhs_name.lastIndexOf(".");
        String lhs_extension = (lhs_idx <= 0)?"":lhs_name.substring(lhs_idx+1);
        String rhs_extension = (rhs_idx <= 0)?"":rhs_name.substring(rhs_idx+1);
            if(lhs_name.startsWith(".") && !rhs_name.startsWith(".")){
                return 1;
            }else if(rhs_name.startsWith(".") && !lhs_name.startsWith(".")){
                return -1;
            }else {
                if (TextUtils.isEmpty(lhs_extension) && !TextUtils.isEmpty(rhs_extension)) {
                    return 1;
                } else if (TextUtils.isEmpty(rhs_extension) && !TextUtils.isEmpty(lhs_extension)) {
                    return -1;
                } else {
                    return (0 - lhs_extension.compareTo(rhs_extension));
                }
            }
    }
}
