package com.nhq.goodie.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nhq.goodie.API.API;
import com.nhq.goodie.Activity.ReadNotificationActivity;
import com.nhq.goodie.Class.Notice;
import com.nhq.goodie.Class.NoticeAdapter;
import com.nhq.goodie.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment {

    public NoticeFragment() {
        // Required empty public constructor
    }

    ListView listView;
    ArrayList<Notice> listNotice;
    NoticeAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notice, container, false);

        //config listView
        listView = view.findViewById(R.id.listView);
        listNotice = new ArrayList<>();
        adapter = new NoticeAdapter(getActivity(), R.layout.listview_notice, listNotice);
        listView.setAdapter(adapter);

        getDataForNoticeList();

        setOnClickForListView();

        //swipe to refresh
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataForNoticeList();
            }
        });
        return view;
    }

    private void setOnClickForListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Notice notice = listNotice.get(i);
                String id = notice.getId();

                if (notice.getMark()) {
                    notice.setMark(false);
                    adapter.notifyDataSetChanged();

                    Call<String> setNoticeMark = API.getInstance().setNoticeMark(id);
                    setNoticeMark.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String s = response.body();
                            if (s.equals("true")) {
                                Log.d("QUANG", "đã đọc thông báo");
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                            Log.d("QUANG", t.toString());
                        }
                    });
                }


                Intent intent = new Intent(getActivity(), ReadNotificationActivity.class);
                intent.putExtra("id_notice", id);
                startActivity(intent);
            }
        });
    }

    private void getDataForNoticeList() {
        listNotice.clear();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginState", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        Call<List<Notice>> getNotices = API.getInstance().getNotices(username);
        getNotices.enqueue(new Callback<List<Notice>>() {
            @Override
            public void onResponse(Call<List<Notice>> call, Response<List<Notice>> response) {
                List<Notice> notices = response.body();
                for (Notice n : notices ) {
                    listNotice.add(n.getDuplicate());
                }

                adapter.notifyDataSetChanged();

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Notice>> call, Throwable t) {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                Log.d("QUANG", t.toString());
            }
        });

        adapter.notifyDataSetChanged();
    }
}
