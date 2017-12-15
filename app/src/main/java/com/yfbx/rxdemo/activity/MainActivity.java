package com.yfbx.rxdemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yfbx.rxdemo.R;
import com.yfbx.rxdemo.bean.NetResult;
import com.yfbx.rxdemo.bean.User;
import com.yfbx.rxdemo.net.Api;
import com.yfbx.rxdemo.net.MySubscriber;
import com.yfbx.rxdemo.net.Net;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private TextView text;
    private Button btn;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pd = new ProgressDialog(this);

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        text = (TextView) findViewById(R.id.info);
    }

    /**
     * 网络请求
     */
    private void login() {
        pd.show();//在doOnNext中执行不起作用
        Net.create(Api.class)
                .login("username", "password")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<NetResult<User>>() {
                    @Override
                    public void onCompleted(boolean isSuccess, String msg) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(NetResult<User> userHttpResult) {
                        text.setText(userHttpResult.toString());
                    }
                });
    }

    /**
     * 网络请求/缓存
     */
    private void getUser() {
        Observable.concat(getCache(), netData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<User>() {
                    @Override
                    public void onCompleted(boolean isSuccess, String msg) {

                    }

                    @Override
                    public void onSuccess(User user) {

                    }
                });

    }

    /**
     * 请求网络数据
     */
    private Observable<User> netData() {
        return Net.create(Api.class)
                .login("username", "password")
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends NetResult<User>>>() {
                    @Override
                    public Observable<? extends NetResult<User>> call(Throwable throwable) {
                        return Observable.empty();
                    }
                })
                .map(new Func1<NetResult<User>, User>() {
                    @Override
                    public User call(NetResult<User> result) {
                        return result.code == 0 ? result.data : null;
                    }
                })
                .filter(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        return user != null;
                    }
                })
                .flatMap(new Func1<User, Observable<User>>() {
                    @Override
                    public Observable<User> call(User user) {
                        return Observable.just(user);
                    }
                });
    }

    /**
     * 读取缓存
     */
    private Observable<User> getCache() {
        return Observable.just("prefs key")
                .map(new Func1<String, User>() {
                    @Override
                    public User call(String s) {
                        // TODO: 2017/12/15 读取缓存，并转换为User
                        return new User();
                    }
                })
                .filter(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        return user != null;
                    }
                })
                .flatMap(new Func1<User, Observable<User>>() {
                    @Override
                    public Observable<User> call(User user) {
                        return Observable.just(user);
                    }
                });
    }


}
