package org.jasig.ssp.service;

import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.jasig.ssp.dao.AuditableCrudDao;
import org.jasig.ssp.model.Auditable;
import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.jasig.ssp.web.api.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class which provides a building block for creating an
 * AuditableCrudService
 * 
 * @param <T>
 *            Any class that extends Auditable
 */
@Transactional
public abstract class AbstractAuditableCrudService<T extends Auditable>
		implements AuditableCrudService<T> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractAuditableCrudService.class);

	/**
	 * Need access to the data access instance, so make children provide it.
	 * 
	 * @return
	 */
	protected abstract AuditableCrudDao<T> getDao();

	@Override
	public PagingWrapper<T> getAll(final SortingAndPaging sAndP) {
		return getDao().getAll(sAndP);
	}

	@Override
	public T get(final UUID id) throws ObjectNotFoundException {
		final T obj = getDao().get(id);
		if (ObjectStatus.ACTIVE.equals(obj.getObjectStatus())) {
			return obj;
		}

		throw new ObjectNotFoundException(id, this.getClass().getName());
	}

	@Override
	public T create(final T obj) throws ObjectNotFoundException,
			ValidationException {
		try {
			return getDao().save(obj);
		} catch (final ConstraintViolationException exc) {
			throw new ValidationException(
					"Invalid data. See cause for list of violations.", exc);
		}
	}

	@Override
	/**
	 * Save is dependent on children
	 */
	public abstract T save(T obj) throws ObjectNotFoundException,
			ValidationException;

	@Override
	public void delete(final UUID id) throws ObjectNotFoundException {
		final T current = getDao().get(id);

		if (!ObjectStatus.DELETED.equals(current.getObjectStatus())) {
			// Object found and is not already deleted, set it and save change.
			current.setObjectStatus(ObjectStatus.DELETED);
			try {
				save(current);
			} catch (final ValidationException exc) {
				// there should never be a ValidationException by simply marking
				// deleted object status field and nothing else
				LOGGER.error(
						"ValidationException thrown during simple object deletion. This is probably a bug. Message: {}",
						exc.getMessage());
				throw new RuntimeException( // NOPMD because don't want to
											// pollute the whole stack with
											// ValidationExceptions on deletes
											// that should never occur in theory
						"ValidationException thrown during simple object deletion. This is probably a bug.",
						exc);
			}
		}
	}
}