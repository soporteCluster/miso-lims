package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.KitService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultKitService implements KitService {

  @Autowired
  private KitStore kitStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setKitStore(KitStore kitStore) {
    this.kitStore = kitStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public Collection<Kit> listKits() throws IOException {
    return kitStore.listAll();
  }

  @Override
  public Collection<KitDescriptor> listKitDescriptors() throws IOException {
    return kitStore.listAllKitDescriptors();
  }

  @Override
  public Collection<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException {
    return kitStore.listKitDescriptorsByType(kitType);
  }

  @Override
  public void deleteKitNote(Kit kit, Long noteId) throws IOException {
    if (noteId == null || noteId.equals(Note.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Kit managed = kitStore.get(kit.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getNoteId().equals(noteId)) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for Kit " + kit.getId());
    }
    authorizationManager.throwIfNonAdminOrMatchingOwner(deleteNote.getOwner());
    managed.getNotes().remove(deleteNote);
    kitStore.save(managed);
  }

  @Override
  public void saveKitNote(Kit kit, Note note) throws IOException {
    Kit managed = kitStore.get(kit.getId());
    note.setCreationDate(new Date());
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    kitStore.save(managed);
  }

  @Override
  public long saveKit(Kit kit) throws IOException {
    authorizationManager.throwIfNotInternal();
    if (kit.getId() != KitImpl.UNSAVED_ID) {
      Kit original = getKitById(kit.getId());
      original.setIdentificationBarcode(kit.getIdentificationBarcode());
      original.setKitDate(kit.getKitDate());
      original.setKitDescriptor(getKitDescriptorById(kit.getKitDescriptor().getId()));
      original.setLocationBarcode(kit.getLocationBarcode());
      original.setLotNumber(kit.getLotNumber());
    }
    return kitStore.save(kit);
  }

  @Override
  public long saveKitDescriptor(KitDescriptor kitDescriptor) throws IOException {
    authorizationManager.throwIfNotInternal();
    if (kitDescriptor.getId() != KitDescriptor.UNSAVED_ID) {
      KitDescriptor original = getKitDescriptorById(kitDescriptor.getId());
      original.setVersion(kitDescriptor.getVersion());
      original.setManufacturer(kitDescriptor.getManufacturer());
      original.setPartNumber(kitDescriptor.getPartNumber());
      original.setStockLevel(kitDescriptor.getStockLevel());
      original.setDescription(kitDescriptor.getDescription());
      kitDescriptor = original;
    }
    setChangeDetails(kitDescriptor);
    return kitStore.saveKitDescriptor(kitDescriptor);
  }

  @Override
  public Kit getKitById(long kitId) throws IOException {
    authorizationManager.throwIfNotInternal();
    return kitStore.get(kitId);
  }

  @Override
  public Kit getKitByIdentificationBarcode(String barcode) throws IOException {
    authorizationManager.throwIfNotInternal();
    return kitStore.getKitByIdentificationBarcode(barcode);
  }

  @Override
  public Kit getKitByLotNumber(String lotNumber) throws IOException {
    authorizationManager.throwIfNotInternal();
    return kitStore.getKitByLotNumber(lotNumber);
  }

  @Override
  public KitDescriptor getKitDescriptorById(long kitDescriptorId) throws IOException {
    return kitStore.getKitDescriptorById(kitDescriptorId);
  }

  @Override
  public KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException {
    return kitStore.getKitDescriptorByPartNumber(partNumber);
  }

  @Override
  public Map<String, Integer> getKitDescriptorColumnSizes() throws IOException {
    return kitStore.getKitDescriptorColumnSizes();
  }

  /**
   * Updates all timestamps and user data associated with the change
   * 
   * @param sample the Sample to update
   * @throws IOException
   */
  private void setChangeDetails(KitDescriptor kitDescriptor) throws IOException {
    User user = authorizationManager.getCurrentUser();
    kitDescriptor.setLastModifier(user);
  }

  @Override
  public List<KitDescriptor> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return kitStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return kitStore.count(errorHandler, filter);
  }

}
