package com.tinf.qmobile.model.journal;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.tinf.qmobile.model.journal.JournalBase.ViewType.FOOTER;
import static com.tinf.qmobile.model.journal.JournalBase.ViewType.HEADER;
import static com.tinf.qmobile.model.journal.JournalBase.ViewType.JOURNAL;


public interface JournalBase {

    @IntDef({HEADER, JOURNAL, FOOTER})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
        //int DEFAULT = 100;
        int HEADER = 200;
        int JOURNAL = 600;
        int FOOTER = 400;
    }

    int getItemType();

}
