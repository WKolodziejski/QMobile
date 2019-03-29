package com.tinf.qmobile.Class.Calendario;

import com.tinf.qmobile.Class.Calendario.Base.EventBase;
import com.tinf.qmobile.Class.Materias.Journal;
import com.tinf.qmobile.Class.Materias.Matter;
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
        Matter matter = journal.getTarget().period.getTarget().matter.getTarget();
        return matter != null ? matter.getTitle() : super.getDescription();
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
        return ViewType.DEFAULT;
    }

}
