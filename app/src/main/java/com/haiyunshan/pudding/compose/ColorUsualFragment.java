package com.haiyunshan.pudding.compose;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.color.ColorPanel;
import com.haiyunshan.pudding.compose.event.FormatColorEvent;
import com.haiyunshan.pudding.widget.ColorLayout;
import com.haiyunshan.pudding.widget.ColorView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColorUsualFragment extends Fragment implements View.OnClickListener {

    View mBtnPanel;
    ColorView mBtnNoColor;
    TextView mNoColorView;

    ArrayList<ColorView> mList;
    LinearLayout mContainer;

    int mColor;

    public ColorUsualFragment() {
        this.mList = new ArrayList<>();

        this.mColor = Color.TRANSPARENT;
    }

    public void setArguments(int color) {
        Bundle args = new Bundle();
        args.putInt("color", color);

        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_color_usual, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mBtnPanel = view.findViewById(R.id.btn_color_panel);
            mBtnPanel.setOnClickListener(this);

            this.mBtnNoColor = view.findViewById(R.id.btn_no_color);
            mBtnNoColor.setOnClickListener(this);

            this.mNoColorView = view.findViewById(R.id.tv_no_color);

            this.mContainer = view.findViewById(R.id.color_container);
        }

        {
            this.ensurePanel();
            for (ColorView v : mList) {
                v.setOnClickListener(this);
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            if (args != null) {
                this.mColor = args.getInt("color", Color.TRANSPARENT);
            }
        }

        this.setChecked(mColor);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void setNoColor(boolean visible, CharSequence text) {
        mBtnNoColor.setVisibility(visible? View.VISIBLE: View.GONE);
        mNoColorView.setText(text);
    }

    public void setColor(int color) {
        this.mColor = color;
        this.setChecked(color);
    }

    public boolean hasColor(int color) {
        for (ColorView v : mList) {
            if (v.getColor() == color) {
                return true;
            }
        }

        if (mBtnNoColor.getVisibility() == View.VISIBLE) {
            if (color == Color.TRANSPARENT) {
                return true;
            }
        }

        return false;
    }

    void ensurePanel() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout container = this.mContainer;
        ColorPanel panel = ColorPanel.instance();
        ArrayList<ColorView> list = this.mList;

        int[][] colors = panel.getColors();
        for (int[] c : colors) {
            ColorLayout group = createLayout(inflater, container, c, list);
            mContainer.addView(group);
        }
    }

    ColorLayout createLayout(LayoutInflater inflater, LinearLayout container, int[] colors, List<ColorView> list) {
        ColorLayout layout = (ColorLayout)(inflater.inflate(R.layout.layout_color_group, container, false));
        LinearLayout group = layout.getContainer();

        int length = colors.length;
        for (int i = 0; i < length; i++) {
            {
                int c = colors[i];
                ColorView view = createView(inflater, group, c);
                layout.addChild(view);

                list.add(view);
            }

            if ((i + 1) != length) {
                View view = inflater.inflate(R.layout.layout_color_separate, group, false);
                layout.addChild(view);
            }
        }

        return layout;
    }

    ColorView createView(LayoutInflater inflater, LinearLayout container, int color) {
        ColorView view = (ColorView)(inflater.inflate(R.layout.layout_color_item, container, false));

        int c = color;
        view.setColor(c);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnPanel) {

        } else if (v == mBtnNoColor) {
            this.setChecked(mBtnNoColor);
        } else if (mList.contains(v)) {
            this.setChecked(v);
        }
    }

    void setChecked(View view) {
        int color = this.mColor;

        this.clear();

        for (ColorView v : mList) {
            if (v == view) {
                this.mColor = v.getColor();

                v.setChecked(true);
                break;
            }
        }

        if (view == mBtnNoColor) {
            this.mColor = Color.TRANSPARENT;

            mBtnNoColor.setChecked(true);
        }

        // 通知颜色发生变化
        if (color != this.mColor) {
            this.notifyColorChanged(color, this.mColor);
        }
    }

    protected void notifyColorChanged(int oldColor, int newColor) {
        EventBus.getDefault().post(new FormatColorEvent(FormatColorEvent.SRC_USUAL, newColor));
    }

    void setChecked(int color) {
        this.clear();

        for (ColorView v : mList) {
            if (v.getColor() == color) {
                v.setChecked(true);
                break;
            }
        }

        if (color == Color.TRANSPARENT) {
            mBtnNoColor.setChecked(true);
        }
    }

    void clear() {
        mBtnNoColor.setChecked(false);

        for (ColorView v : mList) {
            v.setChecked(false);
        }
    }
}
