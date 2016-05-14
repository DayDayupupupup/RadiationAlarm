package edu.neu.radiationalarm.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import edu.neu.radiationalarm.R;
import edu.neu.radiationalarm.service.LacService;

public class BaiduMapFragment extends Fragment implements LacService.FragmentListener_3 {


    public BaiduMapFragment() {
        // Required empty public constructor
    }

    public static BaiduMapFragment newInstance(String param1, String param2) {
        BaiduMapFragment fragment = new BaiduMapFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_baidu_map, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }




    @Override
    public void onDataChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });

    }
}
