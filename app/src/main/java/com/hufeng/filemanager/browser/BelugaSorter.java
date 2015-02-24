package com.hufeng.filemanager.browser;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.hufeng.filemanager.BelugaEntry;
import com.hufeng.filemanager.BelugaSortableInterface;
import com.hufeng.filemanager.CategorySelectEvent;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;

public class BelugaSorter {

    private static final String TAG = BelugaSorter.class.getSimpleName();

    public static final String SORT_PREFERENCE_NAME = "sorter_preferences";

    private static HashMap<CategorySelectEvent.CategoryType, SORTER> mCategoryDefaultSorter;

    static {
        mCategoryDefaultSorter = new HashMap<CategorySelectEvent.CategoryType, SORTER>();
        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.NONE, new SORTER(SORT_FIELD.NAME, SORT_ORDER.ASC));
        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.PHOTO, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.AUDIO, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.VIDEO, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.DOC, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.APK, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.ZIP, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.APP, new SORTER(SORT_FIELD.NAME, SORT_ORDER.ASC));
//        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.DOWNLOAD, new SORTER(SORT_FIELD.DATE, SORT_ORDER.DESC));
//        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.FAVORITE, new SORTER(SORT_FIELD.NAME, SORT_ORDER.ASC));
//        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.CLOUD, new SORTER(SORT_FIELD.DATE, SORT_ORDER.ASC));
//        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.SAFE, new SORTER(SORT_FIELD.DATE, SORT_ORDER.ASC));
//        mCategoryDefaultSorter.put(CategorySelectEvent.CategoryType.SELECTED, new SORTER(SORT_FIELD.DATE, SORT_ORDER.ASC));
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
        DATE, NAME, SIZE, EXTENSION, PACKAGE;
    }

    public enum SORT_ORDER {
        ASC, DESC;
    }

    public static void saveFileSorter(Context context, CategorySelectEvent.CategoryType type, SORTER sorter) {
        SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(SORT_PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        preferenceEditor.putString(type.toString()+"_sort_field", sorter.field.toString());
        preferenceEditor.putString(type.toString()+"_sort_order", sorter.order.toString());
        preferenceEditor.commit();
    }
    
    public static SORTER getFileSorter(Context context, CategorySelectEvent.CategoryType type) {
        SORTER defaultSorter = mCategoryDefaultSorter.get(type);

        if (defaultSorter == null) {
            defaultSorter = mCategoryDefaultSorter.get(CategorySelectEvent.CategoryType.NONE);
        }

        SharedPreferences preferences = context.getSharedPreferences(SORT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String sortField = preferences.getString(type.toString()+"_sort_field", defaultSorter.field.toString());
        String sortOrder = preferences.getString(type.toString()+"_sort_order", defaultSorter.order.toString());

        return new SORTER(SORT_FIELD.valueOf(sortField), SORT_ORDER.valueOf(sortOrder));
    }


    public static Comparator<File> getFileComparator(final SORT_FIELD field, final SORT_ORDER order) {
        switch(field){
            case NAME:
                if(SORT_ORDER.ASC == order){
                    return FILE_COMPARATOR_NAME_ASC;
                }else{
                    return FILE_COMPARATOR_NAME_DESC;
                }
            case EXTENSION:
                if(SORT_ORDER.ASC == order){
                    return FILE_COMPARATOR_EXTENSION_ASC;
                }else{
                    return FILE_COMPARATOR_EXTENSION_DESC;
                }
            case SIZE:
                if(SORT_ORDER.ASC == order){
                    return FILE_COMPARATOR_SIZE_ASC;
                }else{
                    return FILE_COMPARATOR_SIZE_DESC;
                }
            case DATE:
            default:
                if(SORT_ORDER.ASC == order){
                    return FILE_COMPARATOR_DATE_ASC;
                }else{
                    return FILE_COMPARATOR_DATE_DESC;
                }
        }
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

    public static final Comparator<File> FILE_COMPARATOR_NAME_ASC = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            return sCollator.compare(lhs.getName(), rhs.getName());
        }
    };


    public static final Comparator<File> FILE_COMPARATOR_NAME_DESC = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            String lhs_name = lhs.getName();
            String rhs_name = rhs.getName();
            if(lhs_name.startsWith(".") && !rhs_name.startsWith(".")){
                return 1;
            }else if(rhs_name.startsWith(".") && !lhs_name.startsWith(".")) {
                return -1;
            } else {
                return sCollator.compare(rhs_name, lhs_name);
            }
        }
    };
	
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

    public static final Comparator<File> FILE_COMPARATOR_EXTENSION_ASC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            String lhs_name = lhs.getName().toLowerCase();
            String rhs_name = rhs.getName().toLowerCase();
            return compare_extension_asc(lhs_name, rhs_name/*, lhs.isDirectory(), rhs.isDirectory()*/);
        }

    };

    public static Comparator<File> FILE_COMPARATOR_EXTENSION_DESC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            // TODO Auto-generated method stub
            String lhs_name = lhs.getName().toLowerCase();
            String rhs_name = rhs.getName().toLowerCase();
            return compare_extension_desc (lhs_name, rhs_name/*, lhs.isDirectory(), rhs.isDirectory()*/);
        }

    };


    public static Comparator<File> FILE_COMPARATOR_SIZE_ASC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            // TODO Auto-generated method stub
            long lhs_size = lhs.length();
            long rhs_size = rhs.length();

            return lhs_size>rhs_size?1:(lhs_size<rhs_size?-1:0);
        }

    };


    public static Comparator<File> FILE_COMPARATOR_SIZE_DESC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            // TODO Auto-generated method stub
            long lhs_size = lhs.length();
            long rhs_size = rhs.length();

            return rhs_size>lhs_size?1:(rhs_size<lhs_size?-1:0);
        }

    };


    public static Comparator<File> FILE_COMPARATOR_DATE_ASC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            // TODO Auto-generated method stub
            long lhs_date = lhs.lastModified();
            long rhs_date = rhs.lastModified();

            return lhs_date>rhs_date?1:(lhs_date<rhs_date?-1:0);
        }

    };

    public static Comparator<File> FILE_COMPARATOR_DATE_DESC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            // TODO Auto-generated method stub
            long lhs_date = lhs.lastModified();
            long rhs_date = rhs.lastModified();

            return rhs_date>lhs_date?1:(rhs_date<lhs_date?-1:0);
        }

    };


    public static final Comparator<BelugaSortableInterface> COMPARATOR_NAME_DESC = new Comparator<BelugaSortableInterface>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            return sCollator.compare(rhs.getName(), lhs.getName());
        }
    };


    public static final Comparator<BelugaSortableInterface> COMPARATOR_EXTENSION_ASC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            String lhs_name = lhs.getName().toLowerCase();
            String rhs_name = rhs.getName().toLowerCase();
            return compare_extension_asc(lhs_name, rhs_name/*, lhs.isDirectory(), rhs.isDirectory()*/);
        }

    };

    public static Comparator<BelugaSortableInterface> COMPARATOR_EXTENSION_DESC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            // TODO Auto-generated method stub
            String lhs_name = lhs.getName().toLowerCase();
            String rhs_name = rhs.getName().toLowerCase();
            return compare_extension_desc(lhs_name, rhs_name/*, lhs.isDirectory(), rhs.isDirectory()*/);
        }

    };


    public static Comparator<BelugaSortableInterface> COMPARATOR_SIZE_ASC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            // TODO Auto-generated method stub
            long lhs_size = lhs.getSize();
            long rhs_size = rhs.getSize();

            return lhs_size>rhs_size?1:(lhs_size<rhs_size?-1:0);
        }

    };


    public static Comparator<BelugaSortableInterface> COMPARATOR_SIZE_DESC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            // TODO Auto-generated method stub
            long lhs_size = lhs.getSize();
            long rhs_size = rhs.getSize();

            return rhs_size>lhs_size?1:(rhs_size<lhs_size?-1:0);
        }

    };


    public static Comparator<BelugaSortableInterface> COMPARATOR_DATE_ASC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            // TODO Auto-generated method stub
            long lhs_date = lhs.getTime();
            long rhs_date = rhs.getTime();

            return lhs_date>rhs_date?1:(lhs_date<rhs_date?-1:0);
        }

    };

    public static Comparator<BelugaSortableInterface> COMPARATOR_DATE_DESC = new Comparator<BelugaSortableInterface>() {

        @Override
        public int compare(BelugaSortableInterface lhs, BelugaSortableInterface rhs) {
            // TODO Auto-generated method stub
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
//        if(rhs_directory && lhs_directory){
//            if(lhs_name.startsWith(".") && !rhs_name.startsWith(".")){
//                result = 1;
//            }else if(rhs_name.startsWith(".") && !lhs_name.startsWith(".")){
//                result = -1;
//            }else{
//                result = lhs_name.compareTo(rhs_name);
//            }
//        } else if(lhs_directory){
//            result = 1;
//        } else if(rhs_directory){
//            result = -1;
//        } else {
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
//        }
        return result;
    }

    public static int compare_extension_desc(String lhs_name, String rhs_name/*, boolean lhs_directory, boolean rhs_directory*/) {
        int lhs_idx = lhs_name.lastIndexOf(".");
        int rhs_idx = rhs_name.lastIndexOf(".");
        String lhs_extension = (lhs_idx <= 0)?"":lhs_name.substring(lhs_idx+1);
        String rhs_extension = (rhs_idx <= 0)?"":rhs_name.substring(rhs_idx+1);
//        if(rhs_directory && lhs_directory){
//            if(lhs_name.startsWith(".") && !rhs_name.startsWith(".")){
//                return 1;
//            }else if(rhs_name.startsWith(".") && !lhs_name.startsWith(".")){
//                return -1;
//            }else{
//                return lhs_name.compareTo(rhs_name);
//            }
//        } else if(lhs_directory){
//            return 1;
//        } else if(rhs_directory){
//            return -1;
//        } else {
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
//        }
    }
}
