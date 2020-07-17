package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AdminOrderlistCancelActivity extends AdminOrderNavActivity {

    RecyclerView recyclerOrderlist;
    AdminOrderlistAdapter adminOrderlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin_orderlist_cancel);

        //앱바 제목 변경
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("관리자 주문 관리");
        //네비 색상 변경
        bottomNavigationView.setItemBackground(getResources().getDrawable(R.color.colorSubOrange));

        //리사이클러뷰 초기화
        recyclerInit();

        //주문목록 데이터 불러오기
        loadAdminOrderTimeData();
    }

    public void recyclerInit(){
        recyclerOrderlist = (RecyclerView)findViewById(R.id.recyclerAdminOrderlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerOrderlist.setLayoutManager(linearLayoutManager);
        //어댑터 객체 생성
        adminOrderlistAdapter = new AdminOrderlistAdapter(this);
        //리사이클러뷰에 어댑터 설정
        recyclerOrderlist.setAdapter(adminOrderlistAdapter);
        //어댑터에 액티비티 이름 데이터 보내기
        adminOrderlistAdapter.activityName = "orderCancel";
    }

    //관리자가 보는 주문리스트 날짜키 데이터 불러오기
    public void loadAdminOrderTimeData(){

        applicationClass.databaseReference.child("AdminOrderlist")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        applicationClass.makeLog("///AdminOrderlistCancelActivity : Timedata -> onChildAdded()///");
                        //초기화
//                        adminOrderlistAdapter.timeKeyList.clear();
                        applicationClass.makeLog("timeKey 확인 시작");

                        //key값 구하기
                        String timeKey = dataSnapshot.getKey();
                        applicationClass.makeLog("timeKey(확인) : " + timeKey);
                        adminOrderlistAdapter.timeKeyList.add(timeKey);

                        loadAdminOrderlistData(timeKey);
                        applicationClass.makeLog("timeKey 확인 끝");
                        applicationClass.makeLog("timeKey 사이즈 : "+adminOrderlistAdapter.timeKeyList.size());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        applicationClass.makeLog("///AdminOrderlistCancelActivity : Timedata -> onChildChanged()///");
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    //관리자가 보는 주문리스트 불러오기
    public void loadAdminOrderlistData(String timeKeyData){

        applicationClass.makeLog("///여기는 옴///");
//        adminOrderlistAdapter.orderInfoDTOArrayList.clear();

        applicationClass.databaseReference.child("AdminOrderlist").child(timeKeyData)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        applicationClass.makeLog("///AdminOrderlistCancelActivity : Orderlistdata -> onChildAdded()///");
                        //초기화
//                        adminOrderlistAdapter.orderInfoDTOArrayList.clear();
                        applicationClass.makeLog("emailKey 확인 시작");

                        //key값 구하기
                        String emailKey = dataSnapshot.getKey();
                        applicationClass.makeLog("emailKey(확인) : " + emailKey);

                        //주문 정보 객체 받아오기
                        OrderInfoDTO orderInfoDTO = dataSnapshot.getValue(OrderInfoDTO.class);
                        //주문 삳태가 '주문거부 or 주문취소 or 결제취소' 인 데이터만 저장하기
                        if(orderInfoDTO.getOrderState().matches("주문거부") || orderInfoDTO.getOrderState().matches("주문취소") || orderInfoDTO.getOrderState().matches("결제취소")){
                            adminOrderlistAdapter.orderInfoDTOArrayList.add(0, orderInfoDTO);
                        }

                        applicationClass.makeLog("orderInfoList 사이즈 : " + adminOrderlistAdapter.orderInfoDTOArrayList.size());

                        adminOrderlistAdapter.notifyDataSetChanged();
                        applicationClass.makeLog("emailKey 확인 끝");
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        applicationClass.makeLog("///AdminOrderlistCancelActivity : Orderlistdata -> onChildChanged()///"); }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_admin_orderlist_cancel;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavAdminOrder04;
    }
}
