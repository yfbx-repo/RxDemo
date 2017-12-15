package com.yfbx.rxdemo.activity;

import com.yfbx.rxdemo.rxbus.RxBus;
import com.yfbx.rxdemo.rxbus.event.ExampleEvent;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Date:2017/12/15
 * Author:Edward
 * Description:
 */

public class ExBusDemo {

    private void postEvent() {
        RxBus.getDefault().post(new ExampleEvent());
    }


    private void onEvent() {
        RxBus.getDefault()
                .toObservable(ExampleEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExampleEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ExampleEvent exampleEvent) {

                    }
                });
    }
}
