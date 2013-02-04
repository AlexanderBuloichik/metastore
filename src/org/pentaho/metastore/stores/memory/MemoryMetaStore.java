package org.pentaho.metastore.stores.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.metastore.api.BaseMetaStore;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.exceptions.MetaStoreElementTypeExistsException;
import org.pentaho.metastore.api.exceptions.MetaStoreDependenciesExistsException;
import org.pentaho.metastore.api.exceptions.MetaStoreElementExistException;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.api.exceptions.MetaStoreNamespaceExistsException;

public class MemoryMetaStore extends BaseMetaStore implements IMetaStore {

  private Map<String, MemoryMetaStoreNamespace> namespacesMap;
  
  public MemoryMetaStore() {
    namespacesMap = new HashMap<String, MemoryMetaStoreNamespace>();
  }
  
  @Override
  public List<String> getNamespaces() throws MetaStoreException {
    return new ArrayList<String>(namespacesMap.keySet());
  }

  @Override
  public void createNamespace(String namespace) throws MetaStoreException, MetaStoreNamespaceExistsException {
    MemoryMetaStoreNamespace storeNamespace = namespacesMap.get(namespace);
    if (storeNamespace!=null) {
      throw new MetaStoreNamespaceExistsException("Unable to create namespace '"+namespace+"' as it already exist!");
    }
    storeNamespace = new MemoryMetaStoreNamespace(namespace);
    namespacesMap.put(namespace, storeNamespace);
  }

  @Override
  public void deleteNamespace(String namespace) throws MetaStoreException {
    if (namespacesMap.get(namespace)==null) {
      throw new MetaStoreException("Unable to delete namespace '"+namespace+"' as it doesn't exist");
    }
    namespacesMap.remove(namespace);
  }

  @Override
  public List<IMetaStoreElementType> getElementTypes(String namespace) throws MetaStoreException {
    MemoryMetaStoreNamespace storeNamespace = namespacesMap.get(namespace);
    if (storeNamespace==null) {
      return new ArrayList<IMetaStoreElementType>();
    } else {
      return new ArrayList<IMetaStoreElementType>(storeNamespace.getTypeMap().values());
    }
  }

  @Override
  public IMetaStoreElementType getElementType(String namespace, String elementTypeId) throws MetaStoreException {
    MemoryMetaStoreNamespace storeNamespace = namespacesMap.get(namespace);
    if (storeNamespace!=null) {
      storeNamespace.getTypeMap().get(elementTypeId);
    }
    return null;
  }

  @Override
  public List<String> getElementTypeIds(String namespace) throws MetaStoreException {
    MemoryMetaStoreNamespace storeNamespace = namespacesMap.get(namespace);
    if (storeNamespace!=null) {
      ArrayList<String> list = new ArrayList<String>();
      for (MemoryMetaStoreElementType elementType : storeNamespace.getTypeMap().values()) {
        list.add(elementType.getId());
      }
      return list;
    } else {
      return new ArrayList<String>();
    }
  }

  @Override
  public void createElementType(String namespace, IMetaStoreElementType elementType) throws MetaStoreException, MetaStoreElementTypeExistsException {
    MemoryMetaStoreNamespace storeNamespace = namespacesMap.get(namespace);
    if (storeNamespace!=null) {
      MemoryMetaStoreElementType verifyType = storeNamespace.getTypeMap().get(elementType.getId());
      if (verifyType!=null) {
        throw new MetaStoreElementTypeExistsException(getElementTypes(namespace), "Element type with ID '"+elementType.getId()+"' already exists");
      } else {
        MemoryMetaStoreElementType copiedType = new MemoryMetaStoreElementType(elementType);
        storeNamespace.getTypeMap().put(elementType.getId(), copiedType);
      }
    } else {
      throw new MetaStoreException("Namespace '"+namespace+"' doesn't exist!");
    }
  }

  @Override
  public void updateElementType(String namespace, IMetaStoreElementType elementType) throws MetaStoreException {
    MemoryMetaStoreNamespace storeNamespace = namespacesMap.get(namespace);
    if (storeNamespace!=null) {
      MemoryMetaStoreElementType verifyType = storeNamespace.getTypeMap().get(elementType.getId());
      if (verifyType==null) {
        throw new MetaStoreElementTypeExistsException(getElementTypes(namespace), "Element type to update, with ID '"+elementType.getId()+"', does not exist");
      } else {
        MemoryMetaStoreElementType copiedType = new MemoryMetaStoreElementType(elementType);
        storeNamespace.getTypeMap().put(elementType.getId(), copiedType);
      }
    } else {
      throw new MetaStoreException("Namespace '"+namespace+"' doesn't exist!");
    }
  }

  @Override
  public void deleteElementType(String namespace, String elementTypeId) throws MetaStoreException, MetaStoreDependenciesExistsException {
    MemoryMetaStoreNamespace storeNamespace = namespacesMap.get(namespace);
    if (storeNamespace!=null) {
      MemoryMetaStoreElementType verifyType = storeNamespace.getTypeMap().get(elementTypeId);
      if (verifyType==null) {
        throw new MetaStoreElementTypeExistsException(getElementTypes(namespace), "Element type to delete, with ID '"+elementTypeId+"', does not exist");
      } else {
        // See if there are elements in there...
        //
        if (!verifyType.getElementMap().isEmpty()) {
          throw new MetaStoreDependenciesExistsException(getElementIds(namespace, elementTypeId), "Element type with ID '"+elementTypeId+"' could not be deleted as it still contains elements.");
        }
        storeNamespace.getTypeMap().remove(elementTypeId);
      }
    } else {
      throw new MetaStoreException("Namespace '"+namespace+"' doesn't exist!");
    }
  }

  @Override
  public List<IMetaStoreElement> getElements(String namespace, String elementTypeId) throws MetaStoreException {
    MemoryMetaStoreNamespace storeNamespace = namespacesMap.get(namespace);
    if (storeNamespace!=null) {
      MemoryMetaStoreElementType elementType = storeNamespace.getTypeMap().get(elementTypeId);
      if (elementType==null) {
        return new ArrayList<IMetaStoreElement>();
      } else {
        return new ArrayList<IMetaStoreElement>(elementType.getElementMap().values());
      }
    } else {
      throw new MetaStoreException("Namespace '"+namespace+"' doesn't exist!");
    }
  }

  @Override
  public List<String> getElementIds(String namespace, String elementTypeId) throws MetaStoreException {
    List<String> ids = new ArrayList<String>();
    
    return ids;
  }

  @Override
  public IMetaStoreElement getElement(String namespace, String elementTypeId, String elementId) throws MetaStoreException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void createElement(String namespace, String elementTypeId, IMetaStoreElement element) throws MetaStoreException,
      MetaStoreElementExistException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteElement(String namespace, String elementTypeId, String elementId) throws MetaStoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public IMetaStoreElementType newElementType(String namespace) throws MetaStoreException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IMetaStoreElement newElement() throws MetaStoreException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IMetaStoreElement newElement(String id, Object value) throws MetaStoreException {
    // TODO Auto-generated method stub
    return null;
  }

 
}
