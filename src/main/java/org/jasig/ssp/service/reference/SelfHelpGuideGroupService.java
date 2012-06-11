package org.jasig.ssp.service.reference;

import java.util.UUID;

import org.jasig.ssp.model.reference.SelfHelpGuideGroup;
import org.jasig.ssp.service.AuditableCrudService;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.jasig.ssp.web.api.validation.ValidationException;

public interface SelfHelpGuideGroupService extends
		AuditableCrudService<SelfHelpGuideGroup> {

	@Override
	PagingWrapper<SelfHelpGuideGroup> getAll(SortingAndPaging sAndP);

	@Override
	SelfHelpGuideGroup get(UUID id) throws ObjectNotFoundException;

	@Override
	SelfHelpGuideGroup create(SelfHelpGuideGroup obj)
			throws ObjectNotFoundException, ValidationException;

	@Override
	SelfHelpGuideGroup save(SelfHelpGuideGroup obj)
			throws ObjectNotFoundException, ValidationException;

	@Override
	void delete(UUID id) throws ObjectNotFoundException;
}