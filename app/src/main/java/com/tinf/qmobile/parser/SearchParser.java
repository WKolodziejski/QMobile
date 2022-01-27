package com.tinf.qmobile.parser;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.material.Material_;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Clazz_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import com.tinf.qmobile.model.message.Sender;
import com.tinf.qmobile.model.search.Header;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

public class SearchParser  {
    protected Box<Matter> matterBox = DataBase.get().getBoxStore().boxFor(Matter.class);
    protected Box<Journal> journalBox = DataBase.get().getBoxStore().boxFor(Journal.class);
    protected Box<Schedule> scheduleBox = DataBase.get().getBoxStore().boxFor(Schedule.class);
    protected Box<Material> materialsBox = DataBase.get().getBoxStore().boxFor(Material.class);
    protected Box<EventSimple> eventSimpleBox = DataBase.get().getBoxStore().boxFor(EventSimple.class);
    protected Box<Message> messageBox = DataBase.get().getBoxStore().boxFor(Message.class);
    protected Box<Attachment> attachmentBox = DataBase.get().getBoxStore().boxFor(Attachment.class);
    protected Box<Sender> senderBox = DataBase.get().getBoxStore().boxFor(Sender.class);
    protected Box<Clazz> clazzBox = DataBase.get().getBoxStore().boxFor(Clazz.class);

    private final OnSearch onSearch;
    private final Context context;
    private final ExecutorService executor;

    public interface OnSearch {
        void onSearch(List<Queryable> list);
    }

    public SearchParser(Context context, OnSearch onSearch) {
        this.context = context;
        this.onSearch = onSearch;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void execute(String... strings) {
        executor.execute(() -> {

            String string = strings[0].trim();
            String[] queries = string.split(" ");

            List<Queryable> list = new ArrayList<>();

            List<Journal> journals = new ArrayList<>();
            List<Material> materials = new ArrayList<>();
            List<Message> messages = new ArrayList<>();
            List<Clazz> classes = new ArrayList<>();

            for (String query : queries) {

                journals.addAll(journalBox.query()
                        .contains(Journal_.title, query, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                        .build()
                        .find());

                materials.addAll(materialsBox.query()
                        .contains(Material_.title, query, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                        .build()
                        .find());

                messages.addAll(messageBox.query()
                        .contains(Message_.text_, query, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                        .or()
                        .contains(Message_.subject_, query, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                        .build()
                        .find());

                classes.addAll(clazzBox.query()
                        .contains(Clazz_.content_, query, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                        .or()
                        .contains(Clazz_.teacher_, query, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                        .build()
                        .find());
            }

            if (!journals.isEmpty()) {
                list.add(new Header(context.getResources().getString(R.string.title_diarios)));
                list.addAll(journals);
            }

            if (!materials.isEmpty()) {
                list.add(new Header(context.getResources().getString(R.string.title_materiais)));
                list.addAll(materials);
            }

            if (!messages.isEmpty()) {
                list.add(new Header(context.getResources().getString(R.string.title_messages)));
                list.addAll(messages);
            }

            if (!classes.isEmpty()) {
                list.add(new Header(context.getResources().getString(R.string.title_class)));
                list.addAll(classes);
            }

            if (list.isEmpty())
                list.add(new Empty());

            new Handler(Looper.getMainLooper()).post(() -> onSearch.onSearch(list));
        });
    }

}