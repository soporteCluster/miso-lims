package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingParametersDao;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional
@Service
public class DefaultSequencingParametersService implements SequencingParametersService {
  protected static final Logger log = LoggerFactory.getLogger(DefaultSequencingParametersService.class);

  @Autowired
  private SequencingParametersDao sequencingParametersDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public SequencingParameters get(Long sequencingParametersId) throws IOException {
    return sequencingParametersDao.getSequencingParameters(sequencingParametersId);
  }

  @Override
  public Long create(SequencingParameters sequencingParameters) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    sequencingParameters.setCreatedBy(user);
    sequencingParameters.setUpdatedBy(user);
    return sequencingParametersDao.addSequencingParameters(sequencingParameters);
  }

  @Override
  public void update(SequencingParameters sequencingParameters) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    sequencingParameters.setUpdatedBy(user);
    sequencingParametersDao.update(sequencingParameters);
  }

  @Override
  public Set<SequencingParameters> getAll() throws IOException {
    return Sets.newHashSet(sequencingParametersDao.getSequencingParameters());
  }

  @Override
  public void delete(Long sequencingParametersId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SequencingParameters sequencingParameters = sequencingParametersDao.getSequencingParameters(sequencingParametersId);
    sequencingParametersDao.deleteSequencingParameters(sequencingParameters);
  }

}