package com.belugamobile.filemanager.translator;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.belugamobile.filemanager.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Feng Hu on 15-03-24.
 * <p/>
 * TODO: Add a class header comment.
 */
public class TranslatorViewHolder extends RecyclerView.ViewHolder{

    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.language) TextView language;

    public TranslatorViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public void bindTranslator(Translator t) {
        name.setText(t.name);
        language.setText(t.language);
    }

}
