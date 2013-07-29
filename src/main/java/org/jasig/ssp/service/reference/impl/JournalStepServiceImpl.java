/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.service.reference.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.jasig.ssp.dao.reference.JournalStepDao;
import org.jasig.ssp.dao.reference.JournalStepJournalStepDetailDao;
import org.jasig.ssp.dao.reference.JournalTrackJournalStepDao;
import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.model.reference.JournalStep;
import org.jasig.ssp.model.reference.JournalStepDetail;
import org.jasig.ssp.model.reference.JournalStepJournalStepDetail;
import org.jasig.ssp.model.reference.JournalTrack;
import org.jasig.ssp.model.reference.JournalTrackJournalStep;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.reference.JournalStepService;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JournalStep service implementation
 */
@Service
@Transactional
public class JournalStepServiceImpl extends
		AbstractReferenceService<JournalStep>
		implements JournalStepService {

	@Autowired
	transient private JournalStepDao dao;

	@Autowired
	transient private JournalStepJournalStepDetailDao journalStepJournalStepDetailDao;
	
	@Autowired
	transient private JournalTrackJournalStepDao journalTrackJournalStepDao;


	protected void setDao(final JournalStepDao dao) {
		this.dao = dao;
	}

	@Override
	protected JournalStepDao getDao() {
		return dao;
	}

	@Override
	public PagingWrapper<JournalStep> getAllForJournalTrack(
			final JournalTrack journalTrack,
			final SortingAndPaging sAndP) {
		Set<JournalStep> steps = new HashSet<JournalStep>();
		PagingWrapper<JournalStep> allForJournalTrack = getDao().getAllForJournalTrack(journalTrack.getId(), new SortingAndPaging(ObjectStatus.ALL, sAndP.getFirstResult(), sAndP.getMaxResults(), sAndP.getSortFields(), sAndP.getDefaultSortProperty(), sAndP.getDefaultSortDirection()));
		PagingWrapper<JournalTrackJournalStep> allForJournalTrackAssociations = journalTrackJournalStepDao.getAllForJournalTrack(journalTrack.getId(), new SortingAndPaging(sAndP.getStatus()));
		for (JournalStep journalStep : allForJournalTrack) 
		{
			for (JournalTrackJournalStep journalTrackJournalStep : allForJournalTrackAssociations) {
				if(journalStep.getId().equals(journalTrackJournalStep.getJournalStep().getId()))
				{
					steps.add(journalStep);
				}
			}
		}
		return new PagingWrapper<JournalStep>(steps);
	}
	
	@Override
	public PagingWrapper<JournalStepJournalStepDetail> getJournalStepDetailAssociationsForJournalStep(
			final UUID journalStepId, final SortingAndPaging sAndP)
			throws ObjectNotFoundException {
		return journalStepJournalStepDetailDao.getAllForJournalStep(journalStepId, sAndP);
	}

	@Override
	public JournalStepJournalStepDetail addJournalStepDetailToJournalStep(
			final JournalStepDetail journalStepDetail,
			final JournalStep journalStep) {
		final PagingWrapper<JournalStepJournalStepDetail> journalStepDetailCategories = journalStepJournalStepDetailDao
				.getAllForJournalStepDetailAndJournalStep(
						journalStepDetail.getId(),
						journalStep.getId(),
						new SortingAndPaging(ObjectStatus.ACTIVE));

		JournalStepJournalStepDetail journalStepJournalStepDetail = null;
		// if this journalStepDetail is already there and ACTIVE, ignore
		if (journalStepDetailCategories.getResults() < 1) {
			journalStepJournalStepDetail = new JournalStepJournalStepDetail();
			journalStepJournalStepDetail.setJournalStep(journalStep);
			journalStepJournalStepDetail
					.setJournalStepDetail(journalStepDetail);
			journalStepJournalStepDetail.setObjectStatus(ObjectStatus.ACTIVE);

			journalStepJournalStepDetail = journalStepJournalStepDetailDao
					.save(journalStepJournalStepDetail);
		}

		return journalStepJournalStepDetail;
	}

	@Override
	public JournalStepJournalStepDetail removeJournalStepDetailFromJournalStep(
			final JournalStepDetail journalStepDetail,
			final JournalStep journalStep) {
		// get current journalStepDetails for journalStep
		final PagingWrapper<JournalStepJournalStepDetail> journalStepDetailCategories = journalStepJournalStepDetailDao
				.getAllForJournalStepDetailAndJournalStep(
						journalStepDetail.getId(),
						journalStep.getId(),
						new SortingAndPaging(ObjectStatus.ACTIVE));

		JournalStepJournalStepDetail journalStepJournalStepDetail = null;
		// if this journalStepDetail is already there and ACTIVE, delete
		if (journalStepDetailCategories.getResults() > 0) {
			for (final JournalStepJournalStepDetail item : journalStepDetailCategories
					.getRows()) {
				item.setObjectStatus(ObjectStatus.INACTIVE);

				// we'll just return the last one
				journalStepJournalStepDetail = journalStepJournalStepDetailDao
						.save(item);
			}
		}

		return journalStepJournalStepDetail;
	}

	public JournalTrackJournalStepDao getJournalTrackJournalStepDao() {
		return journalTrackJournalStepDao;
	}

	public void setJournalTrackJournalStepDao(JournalTrackJournalStepDao journalTrackJournalStepDao) {
		this.journalTrackJournalStepDao = journalTrackJournalStepDao;
	}
}