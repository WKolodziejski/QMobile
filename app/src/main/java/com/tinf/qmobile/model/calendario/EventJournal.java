package com.tinf.qmobile.model.calendario;

import com.tinf.qmobile.model.calendario.Base.EventBase;
import com.tinf.qmobile.model.matter.Journal;
import com.tinf.qmobile.model.matter.Matter;

import io.objectbox.annotation.Entity;
import io.objectbox.relation.ToOne;

@Entity
public class EventJournal extends EventBase {
    public ToOne<Journal> journal;

    public EventJournal(Journal journal) {
        super(journal.getTitle(), journal.getDate());
        this.journal.setTarget(journal);
    }

    public EventJournal(String title, long startTime) {
        super(title, startTime);
    }

    @Override
    public int getColor() {
        if (journal.getTargetId() != 0) {
            Matter matter = journal.getTarget().period.getTarget().matter.getTarget();
            return matter != null ? matter.getColor() : super.getColor();
        } else {
            return super.getColor();
        }
    }

    @Override
    public String getDescription() {
        if (journal.getTargetId() != 0) {
            Matter matter = journal.getTarget().period.getTarget().matter.getTarget();
            return matter != null ? matter.getTitle() : super.getDescription();
        }
        return super.getDescription();
    }

    public Journal getJournal() {
        return journal.getTarget();
    }

    /*
     * Required methods
     */

    public EventJournal() {
        super();
    }

    @Override
    public int getItemType() {
        return ViewType.JOURNAL;
    }

}
