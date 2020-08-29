package org.jasig.ssp.service.impl;

import org.jasig.ssp.transferobject.reports.JournalCaseNotesStudentReportTO;

import java.util.Comparator;

public class JournalCaseNotesStudentReportSorter implements Comparator<JournalCaseNotesStudentReportTO> {

    @Override           //1
    public int compare(JournalCaseNotesStudentReportTO o1, JournalCaseNotesStudentReportTO o2) {
        int value = o1.getLastName().compareToIgnoreCase(
                o2.getLastName());
        //1
        if (value != 0)
            return value;

        value = o1.getFirstName().compareToIgnoreCase(
                o2.getFirstName());
        //1
        if (value != 0)
            return value;
        //1
        if (o1.getMiddleName() == null && o2.getMiddleName() == null)
            return 0;
        //1
        if (o1.getMiddleName() == null)
            return -1;
        //1
        if (o2.getMiddleName() == null)
            return 1;
        return o1.getMiddleName().compareToIgnoreCase(
                o2.getMiddleName());
    }
}
