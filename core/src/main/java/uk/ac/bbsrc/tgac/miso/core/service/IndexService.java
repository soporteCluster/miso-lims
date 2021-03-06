package uk.ac.bbsrc.tgac.miso.core.service;

import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface IndexService extends PaginatedDataSource<Index> {
  public IndexFamily getIndexFamilyByName(String name);

  public Collection<IndexFamily> getIndexFamilies();

  public Collection<IndexFamily> getIndexFamiliesByPlatform(PlatformType platformType);

  public Index getIndexById(long id);
}
