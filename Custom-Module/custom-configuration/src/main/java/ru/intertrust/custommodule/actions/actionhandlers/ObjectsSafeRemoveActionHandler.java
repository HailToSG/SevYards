package ru.intertrust.custommodule.actions.actionhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.dao.access.AccessControlService;
import ru.intertrust.cm.core.dao.api.DomainObjectDao;
import ru.intertrust.cm.core.gui.api.server.action.ActionHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.action.SimpleActionContext;
import ru.intertrust.cm.core.gui.model.action.SimpleActionData;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;
import java.util.List;

@ComponentName("objects.safe.remove.handler")
public class ObjectsSafeRemoveActionHandler extends ActionHandler<SimpleActionContext, SimpleActionData> {

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private CrudService crudService;

    @Autowired
    private CollectionsService collectionsService;

    @Autowired
    private DomainObjectDao domainObjectService;

    @Override
    public SimpleActionData executeAction(SimpleActionContext context) {
        Id dObjId = context.getRootObjectId();
        deleteObjectAndLinkedObjects(dObjId);
        return new SimpleActionData();
    }

    private void deleteObjectAndLinkedObjects(Id objId) {
        DomainObject dObj = crudService.find(objId);
        String objType = dObj.getTypeName();

        switch (objType) {
            case "ter_territory":
                deleteTerritory(objId);
                break;
            case "inventory":
                deleteInventory(objId);
                break;
            default:
                deleteObject(objId);
                break;
        }
    }

    private void deleteObject(Id objId) {
        StatusSetter statusSetter = new StatusSetter();
        if (objId != null) {
            DomainObject dObj = crudService.find(objId);
            String actualStatusName = statusSetter.getQueryStatusById(dObj.getStatus(), collectionsService);

            if (actualStatusName != null && !actualStatusName.equals(CustomModuleConstants.STATUS_DELETED)) {
                    statusSetter.changeStatusForDo(dObj.getId(), CustomModuleConstants.STATUS_DELETED,
                            collectionsService, accessControlService, domainObjectService);
            }
        }
    }

    private void deleteInventory(Id objId) {
        List<DomainObject> linkedObjectsList;
        InventoryCopierActionHandler linkedObjectsGetter = new InventoryCopierActionHandler();

        for (String linkedDoName : CustomModuleConstants.LINKED_DO_TYPES) {
            linkedObjectsList = linkedObjectsGetter.getLinkedObjects(linkedDoName, objId, crudService);
            if (linkedObjectsList.size()>0) {
                for (DomainObject dObject : linkedObjectsList) {
                    deleteObject(dObject.getId());
                }
            }
        }
        deleteObject(objId);
    }

    private void deleteTerritory(Id objId) {
        List<Id> inventoryIdList = crudService.findLinkedDomainObjectsIds(
                objId, "inventory", CustomModuleConstants.TERRITORY_LINKED_FIELD);

        for (Id invId : inventoryIdList)
            deleteInventory(invId);

        deleteObject(objId);
    }
}