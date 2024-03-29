package com.example.membershipapp;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotification {

    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static String serverKey = "AAAA7uH6BQk:APA91bEctbzu1ylmxJZM5oLdxz9q5avHAJDRyFTsF50ntOrmimeoMPNOXiswJMC0p-SissfDMf_UIksLEYc0CZf-NoeoWa7vYxgawyk8QX2PaAg9R5G8rXlKzkdGntv7us7UfR0jTwJc";
    private static String TAG = "LogActivity";
    private static String adminToken = "eF2efY8nR2O_2IjSxyRqDm:APA91bGeZYStey21ViPG5MjtwxdNdWYswzwLLHUsRInF2uEbdqyc9KZJmpUZ3msKUFB22w9CO7DeZABp46qH45Tm3u_nv_mxdPaT_Fa4t28bevWsnPOBaAu5hO68JWEhT4y5KGm9GLGc";

    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public static void sendNotification(final String regToken, final String title, final String messsage){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... parms) {
                try {
                    //OkHttp는 통신 라이브러리이다 : HTTP 기반의 request/response 방식
                    //OKHTTP를 이용해 웹서버로 토큰값을 날려준다
                    //[1] 우리를 okhttp를 사용하는 고객이라고 생각하고 OkHttpClient 객체를 생성해준다.
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    //notificationJson
                    JSONObject notificationJson = new JSONObject();
                    notificationJson.put("title", title);   //내가 보낼 메세지 타이틀
                    notificationJson.put("body", messsage); //내가 보낼 메세지 컨텐츠
                    json.put("notification", notificationJson);
                    //dataJson
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("title", title);
                    dataJson.put("body", messsage);
                    json.put("data", dataJson);
                    json.put("to", regToken);   //내가 메세지를 보낼 기기의 토큰

                    // click_action 추가! //fcm이 백그라운드일때 내가 지정한 액티비티로 보내기
                    if(regToken.contentEquals(adminToken)){ //알림을 보내려는 토근이 = 관리자 토큰일때
                        notificationJson.put("click_action", "OPEN_ADMIN_ORDER_ACTIVITY");
                    }else{                                  //알림을 보내려는 토큰이 = 사용자 토큰일때
                        notificationJson.put("click_action", "OPEN_USER_ORDER_ACTIVITY");
                    }

                    //[2] 요철할 body를 만들어준다.
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    //[3] build()를 통해 몸통을 만들어준다.
                    //[4] 이 몸통을 어디에 요청할 것인지 requst를 통해 만들어준다.
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + serverKey)
                            .url("https://fcm.googleapis.com/fcm/send") //토큰 저장할라고 보낼 URL
                            .post(body) //위의 url에 (post형식으로)보낼 body를 요청사항에 넣고 request를 만들어준다
                            .build();
                    //[5] 이제 client에게 요청하도록 시켜준다
                    Response response = client.newCall(request).execute();
                    Log.d("LogActivity", "뭐지이 : "+json.toString());
                    String finalResponse = response.body().string();
                }catch (Exception e){
                    Log.d("error", e+"");
                }
                return  null;
            }
        }.execute();
    }

    public static void sendToAdminFCM(final String fcmTitle, final String fcmMessage) {
        Log.d(TAG, "관리자에게 주문 알람을 보내는 코드에 진입");
        databaseReference.child("Admin")
                .child("admin@naver,com").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                Log.d(TAG, "관리자의 키 : "+ key);

                Map<String,String> map= (Map<String, String>) dataSnapshot.getValue(); //상대유저의 토큰
                String adminToken = map.get("adminToken");

                Log.d(TAG, "관리자의 토큰 : "+ adminToken);
                sendNotification(adminToken, fcmTitle, fcmMessage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public static void sendToUserFCM(final String uerEmail, final String fcmTitle, final String fcmMessage) {
        Log.d(TAG, "관리자에게 주문 알람을 보내는 코드에 진입");
        databaseReference.child("users")
                .child(uerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String key = dataSnapshot.getKey();
//                Log.d(TAG, "관리자의 키 : "+ key);

                Map<String,String> map= (Map<String, String>) dataSnapshot.getValue(); //상대유저의 토큰
                String userToken = map.get("userToken");

                Log.d(TAG, "사용자의 토큰 : "+ userToken);
                sendNotification(userToken, fcmTitle, fcmMessage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

}
