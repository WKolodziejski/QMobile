package com.tinf.qmobile.data;

import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.model.MyObjectBox;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;
import java.util.List;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;

import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class DataBase {
    private static final String TAG = "DataBase";
    private static DataBase instance;
    private BoxStore boxStore;
    private List<Matter> matters;

    private DataBase() {
        if (User.isValid() || Client.get().isValid()) {
            boxStore = MyObjectBox
                    .builder()
                    .androidContext(App.getContext())
                    .name(User.getCredential(REGISTRATION))
                    .build();
        } else return;

        boxStore.subscribe(Matter.class)
                .observer(data -> {
                    if (matters == null) {
                        matters = boxStore
                                .boxFor(Matter.class)
                                .query()
                                .order(Matter_.title_)
                                .equal(Matter_.year_, User.getYear(pos))
                                .and()
                                .equal(Matter_.period_, User.getPeriod(pos))
                                .build()
                                .find();

                        Log.d(TAG, "First fetch");

                    } else {
                        List<Matter> updated = boxStore
                                .boxFor(Matter.class)
                                .query()
                                .order(Matter_.title_)
                                .equal(Matter_.year_, User.getYear(pos))
                                .and()
                                .equal(Matter_.period_, User.getPeriod(pos))
                                .build()
                                .find();

                        for (Matter m1 : updated)
                            for (Matter m2 : matters)
                                if (m1.id == m2.id) {
                                    m1.isExpanded = m2.isExpanded;
                                    m1.shouldAnimate = m2.shouldAnimate;
                                }

                        matters.clear();
                        matters = updated;

                        Log.d(TAG, "List updated");
                    }
                });
    }

    public static synchronized DataBase get() {
        if (instance == null)
            instance = new DataBase();
        return instance;
    }

    public List<Matter> getMatters() {
        return matters;
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }

    public void closeBoxStore() {
        if (boxStore != null) {
            boxStore.closeThreadResources();
            boxStore.close();
            boxStore.deleteAllFiles();
            boxStore = null;
        }
    }

}
