package org.jasig.ssp.service.impl;

import org.jasig.ssp.dao.JournalEntryDao;
import org.jasig.ssp.dao.PersonDao;
import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.transferobject.reports.BaseStudentReportTO;
import org.jasig.ssp.transferobject.reports.JournalCaseNotesStudentReportTO;
import org.jasig.ssp.transferobject.reports.JournalStepSearchFormTO;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contagem de pontos para a classe extraída durante refatoração: 7
 */
@Component
class SearchPersonWithJournalEntries {

    @Autowired//1
    private JournalEntryDao journalEntryDao;
    @Autowired//1
    private PersonDao personDao;
    @Autowired//1
    private PersonWithJournalEntriesBySchool personWithJournalEntriesBySchool;

    List<JournalCaseNotesStudentReportTO> find(JournalStepSearchFormTO personSearchForm, SortingAndPaging sAndP) throws ObjectNotFoundException {
        final List<JournalCaseNotesStudentReportTO> personsWithJournalEntries = journalEntryDao.getJournalCaseNoteStudentReportTOsFromCriteria(personSearchForm, sAndP);
        final Map<String, JournalCaseNotesStudentReportTO> personsWithJournalBySchoolId = new HashMap<>();

        //1
        for (JournalCaseNotesStudentReportTO entry : personsWithJournalEntries) {
            personsWithJournalBySchoolId.put(entry.getSchoolId(), entry);
        }

        final SortingAndPaging personSAndP = SortingAndPaging.createForSingleSortAll(ObjectStatus.ACTIVE, "lastName", "DESC");
        //1
        final PagingWrapper<BaseStudentReportTO> persons = personDao.getStudentReportTOs(personSearchForm, personSAndP);

        //1
        if (persons == null) {
            return personsWithJournalEntries;
        }

        List<JournalCaseNotesStudentReportTO> journalEntriesByCoachSchool = personWithJournalEntriesBySchool.find(personSearchForm, personsWithJournalBySchoolId, persons);
        personsWithJournalEntries.addAll(journalEntriesByCoachSchool);

        //1
        personsWithJournalEntries.sort(new JournalCaseNotesStudentReportSorter());
        return personsWithJournalEntries;
    }
}
