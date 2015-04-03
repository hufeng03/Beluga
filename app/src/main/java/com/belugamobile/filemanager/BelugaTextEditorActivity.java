package com.belugamobile.filemanager;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Feng Hu on 15-03-31.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaTextEditorActivity extends BelugaBaseActionBarActivity {

    BelugaTextEditorTaskFragment taskFragment = null;
    BelugaTextEditorFragment editorFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beluga_text_editor_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_color_dark));
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            FragmentTransaction mFragmentTransaction = mFragmentManager
                    .beginTransaction();
            BelugaTextEditorFragment textEditorFragment = BelugaTextEditorFragment.newInstance(getIntent().getData());
            mFragmentTransaction.replace(R.id.content, textEditorFragment, "TextEditorFragment");

            BelugaTextEditorTaskFragment textEditorTaskFragment = new BelugaTextEditorTaskFragment();
            mFragmentTransaction.add(textEditorTaskFragment, "TextEditorTaskFragment");

            mFragmentTransaction.commit();

            editorFragment = textEditorFragment;
            taskFragment = textEditorTaskFragment;
        } else {
            editorFragment = (BelugaTextEditorFragment)mFragmentManager.findFragmentByTag("TextEditorFragment");
            taskFragment = (BelugaTextEditorTaskFragment)mFragmentManager.findFragmentByTag("TextEditorTaskFragment");
        }
    }

    public static class BelugaTextEditorFragment extends BelugaBaseFragment{

        EditText mEditor;
        ProgressBar mProgress;
        Uri mUri;

        public static BelugaTextEditorFragment newInstance(Uri path) {
            BelugaTextEditorFragment fragment = new BelugaTextEditorFragment();
            Bundle data = new Bundle();
            data.putParcelable("uri", path);
            fragment.setArguments(data);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            if (getArguments() != null) {
                mUri = getArguments().getParcelable("uri");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.beluga_text_editor_fragment, container, false);
            mEditor = (EditText)view.findViewById(R.id.input);
            mProgress = (ProgressBar)view.findViewById(R.id.progress);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            if (savedInstanceState == null) {
                mProgress.setVisibility(View.VISIBLE);
                ((BelugaTextEditorActivity)getActivity()).loadTextContentAsync(mUri);
            } else {
                mProgress.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.text_editor_fragment_menu, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.menu_save) {
                ((BelugaTextEditorActivity)getActivity()).saveTextContentAsync(mUri, mEditor.getText().toString());
                return true;
            } else {
                return false;
            }
        }

        public void textLoaded(String content) {
            mEditor.setText(content);
            mProgress.setVisibility(View.GONE);
        }

        public void textSaved(boolean result) {
            mProgress.setVisibility(View.GONE);
        }
    }

    public void loadTextContentAsync(Uri uri) {
        if (taskFragment != null) {
            taskFragment.loadText(uri);
        }
    }

    public void saveTextContentAsync(Uri uri, String content) {
        if (taskFragment != null) {
            taskFragment.saveText(uri, content);
        }
    }

    public void textLoadedCallback(String content) {
        if (editorFragment != null) {
            editorFragment.textLoaded(content);
        }
    }

    public void textSaveedCallback(boolean success) {
        if (editorFragment != null) {
            editorFragment.textSaved(success);
        }
    }

    public static class BelugaTextEditorTaskFragment extends Fragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void saveText(final Uri uri, final String text) {
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    //Uri uri = params[0];

                    OutputStream fOutStream = null;
                    boolean result = false;
                    try {
                        fOutStream =  FileManager.getAppContext().getContentResolver().openOutputStream(uri);
                        fOutStream.write(text.getBytes("UTF-8"));
                        result = true;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (fOutStream != null) {
                            try {
                                fOutStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean s) {
                    super.onPostExecute(s);
                    ((BelugaTextEditorActivity)getActivity()).textSaveedCallback(s);
                }
            }.execute();
        }

        public void loadText(final Uri uri) {
            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... params) {
                    //Uri uri = params[0];

                    final StringBuilder content = new StringBuilder();
                    InputStream fInStream = null;
                    try {
                        fInStream =  FileManager.getAppContext().getContentResolver().openInputStream(uri);
                        BufferedReader br = new BufferedReader(new InputStreamReader(fInStream, "UTF-8"));
                        String line;
                        try {
                            while ((line = br.readLine()) != null) {
                                content.append(line).append("\n");
                            }
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (fInStream != null) {
                            try {
                                fInStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return content.toString();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    ((BelugaTextEditorActivity)getActivity()).textLoadedCallback(s);
                }
            }.execute();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
