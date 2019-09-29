package com.haiyunshan.pudding.compose.export;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.haiyunshan.pudding.FtpServerActivity;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.compose.ComposeFragment;
import com.haiyunshan.pudding.dataset.FileStorage;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExportDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    View mRootView;

    View mExportHexoBtn;
    View mShareBtn;
    View mCancelBtn;

    ComposeFragment mParentFragment;

    public ExportDialogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_Export);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_dialog, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRootView = view;
            mRootView.setOnClickListener(this);

            this.mExportHexoBtn = view.findViewById(R.id.btn_export_hexo);
            mExportHexoBtn.setOnClickListener(this);

            this.mShareBtn = view.findViewById(R.id.btn_share);
            mShareBtn.setOnClickListener(this);

            this.mCancelBtn = view.findViewById(R.id.btn_cancel);
            mCancelBtn.setOnClickListener(this);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Fragment parent = this.getParentFragment();
        while (true) {
            if (parent == null) {
                break;
            }

            if (parent instanceof ComposeFragment) {
                this.mParentFragment = (ComposeFragment)parent;
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mRootView) {
          this.dismiss();
        } else if (v == mExportHexoBtn) {
            mParentFragment.export(ExportFactory.HEXO_MARKDOWN);

            this.dismiss();
        } else if (v == mShareBtn) {
            File file = FileStorage.getDocumentExportRoot();
            String homeDir = file.getAbsolutePath();

            FtpServerActivity.start(this, homeDir);

            this.dismiss();
        } else if (v == mCancelBtn) {
            this.dismiss();
        }
    }
}
