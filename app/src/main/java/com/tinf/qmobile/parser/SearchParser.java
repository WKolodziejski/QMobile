package com.tinf.qmobile.parser;

import android.os.AsyncTask;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.material.Material_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import com.tinf.qmobile.model.message.Sender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.objectbox.Box;

public class SearchParser extends AsyncTask<String, Void, List<Queryable>> {
    protected Box<Matter> matterBox = DataBase.get().getBoxStore().boxFor(Matter.class);
    protected Box<Journal> journalBox = DataBase.get().getBoxStore().boxFor(Journal.class);
    protected Box<Schedule> scheduleBox = DataBase.get().getBoxStore().boxFor(Schedule.class);
    protected Box<Material> materialsBox = DataBase.get().getBoxStore().boxFor(Material.class);
    protected Box<EventSimple> eventSimpleBox = DataBase.get().getBoxStore().boxFor(EventSimple.class);
    protected Box<Message> messageBox = DataBase.get().getBoxStore().boxFor(Message.class);
    protected Box<Attachment> attachmentBox = DataBase.get().getBoxStore().boxFor(Attachment.class);
    protected Box<Sender> senderBox = DataBase.get().getBoxStore().boxFor(Sender.class);

    private OnSearch onSearch;

    public interface OnSearch {
        void onSearch(List<Queryable> list);
    }

    public SearchParser(OnSearch onSearch) {
        this.onSearch = onSearch;
    }

    @Override
    protected List<Queryable> doInBackground(String... strings) {
        String query = strings[0].trim();
        Set<Queryable> list = new HashSet<>();

        list.addAll(journalBox.query()
                .contains(Journal_.title, query)
                .build()
                .find());

        list.addAll(materialsBox.query()
                .contains(Material_.title, query)
                .build()
                .find());

        list.addAll(messageBox.query()
                .contains(Message_.text_, query)
                .or()
                .contains(Message_.subject_, query)
                .build()
                .find());

        return new ArrayList<>(list);
    }

    @Override
    protected void onPostExecute(List<Queryable> list) {
        super.onPostExecute(list);
        onSearch.onSearch(list);
    }

}
