package com.collective.collective.View.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import android.widget.Button;
import android.widget.CheckBox;

import com.collective.collective.R;


public class AlbumStorageTypeDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Context context;
    private Button dismiss, add;
    private CheckBox checkBoxCassette;
    private CheckBox checkBoxCd;
    private CheckBox checkBoxVinyl;
    private CheckBox checkBoxCloud;
    private OnStorageType onStorageType;

    public AlbumStorageTypeDialog(Activity activity) {
        super(activity);
        this.context = activity;
        onStorageType = (OnStorageType) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.album_storage_type_dialog);
        dismiss = findViewById(R.id.button_dismiss);
        add = findViewById(R.id.button_add);
        checkBoxCassette = findViewById(R.id.checkBox_cassette);
        checkBoxCd = findViewById(R.id.checkBox_cd);
        checkBoxVinyl = findViewById(R.id.checkBox_vinyl);
        checkBoxCloud = findViewById(R.id.checkBox_cloud);

        dismiss.setOnClickListener(this);
        add.setOnClickListener(this);
        checkBoxCassette.setOnClickListener(this);
        checkBoxCd.setOnClickListener(this);
        checkBoxVinyl.setOnClickListener(this);
        checkBoxCloud.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_dismiss:
                dismiss();
                clearCheckBoxes();
                break;
            case R.id.button_add:
                dismiss();
                onStorageType.onStorageType(
                        checkBoxCassette.isChecked(),
                        checkBoxCd.isChecked(),
                        checkBoxVinyl.isChecked(),
                        checkBoxCloud.isChecked());
                clearCheckBoxes();
                break;
            default:
                break;
        }
    }

    private void clearCheckBoxes() {
        checkBoxCloud.setChecked(false);
        checkBoxCassette.setChecked(false);
        checkBoxCd.setChecked(false);
        checkBoxVinyl.setChecked(false);
    }
}