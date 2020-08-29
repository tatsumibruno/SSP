package org.jasig.ssp.service.impl;

import org.apache.commons.lang.StringUtils;
import org.jasig.ssp.dao.JournalEntryDao;
import org.jasig.ssp.transferobject.reports.BaseStudentReportTO;
import org.jasig.ssp.transferobject.reports.JournalCaseNotesStudentReportTO;
import org.jasig.ssp.transferobject.reports.JournalStepSearchFormTO;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contagem de pontos para a classe extraída durante refatoração: 7
 */
@Component
class PersonWithJournalEntriesBySchool {

    @Autowired//1
    private JournalEntryDao journalEntryDao;

    //1
    List<JournalCaseNotesStudentReportTO> find(JournalStepSearchFormTO personSearchForm,
                                               Map<String, JournalCaseNotesStudentReportTO> personsWithJournalBySchoolId,
                                               PagingWrapper<BaseStudentReportTO> persons) {
        List<JournalCaseNotesStudentReportTO> personsWithJournalEntries = new ArrayList<>();
        //1
        for (BaseStudentReportTO person : persons) {
            //1
            if (!personsWithJournalBySchoolId.containsKey(person.getSchoolId()) && StringUtils.isNotBlank(person.getCoachSchoolId())) {
                boolean addStudent = true;
                //1
                if (personSearchForm.getJournalSourceIds() != null) {
                    //1
                    if (journalEntryDao.getJournalCountForPersonForJournalSourceIds(person.getId(), personSearchForm.getJournalSourceIds()) == 0) {
                        addStudent = false;
                    }
                }
                //1
                if (addStudent) {
                    final JournalCaseNotesStudentReportTO entry = new JournalCaseNotesStudentReportTO(person);
                    personsWithJournalEntries.add(entry);
                    personsWithJournalBySchoolId.put(entry.getSchoolId(), entry);
                }
            }
        }
        return personsWithJournalEntries;
    }
}
