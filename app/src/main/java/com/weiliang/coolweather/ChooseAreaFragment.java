package com.weiliang.coolweather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 创建日期：2017/7/5 0005
 * 描述:
 * 作者:刘敏
 */

public class ChooseAreaFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        TextView titleText = (TextView) view.findViewById(R.id.title_text);
        Button backButton = (Button) view.findViewById(R.id.back_button);
        ListView listview = (ListView) view.findViewById(R.id.list_view);

        return view;
    }
}
