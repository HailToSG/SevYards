package ru.intertrust.custommodule.actions.actionhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.config.ConfigurationExplorer;
import ru.intertrust.cm.core.dao.access.AccessControlService;
import ru.intertrust.cm.core.dao.api.DomainObjectDao;
import ru.intertrust.cm.core.gui.api.server.action.ActionHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.action.SimpleActionContext;
import ru.intertrust.cm.core.gui.model.action.SimpleActionData;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

import java.util.ArrayList;
import java.util.List;

@ComponentName("objects.safe.restore.handler")
public class ObjectsSafeRestoreActionHandler extends ActionHandler<SimpleActionContext, SimpleActionData> {

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
        restoreAllLinkedObjects(dObjId);
        return new SimpleActionData();
    }

    private void restoreAllLinkedObjects(Id objId) {
        DomainObject dObj = crudService.find(objId);
        String objType = dObj.getTypeName();

        switch (objType) {
            case "ter_territory":
                restoreTerritory(objId);
                break;
            case "inventory":
                restoreInventory(objId);
                break;
            default:
                restoreObject(objId, dObj.getTypeName());
                break;
        }
    }

    private void restoreObject(Id objId, String objType) {
        StatusSetter statusSetter = new StatusSetter();
        if (objId != null) {
            DomainObject dObj = crudService.find(objId);
            String actualStatusName = statusSetter.getQueryStatusById(dObj.getStatus(), collectionsService);

            if (actualStatusName != null && actualStatusName.equals(CustomModuleConstants.STATUS_DELETED)) {
                if(objType.equals("inventory"))
                statusSetter.changeStatusForDo(dObj.getId(), CustomModuleConstants.STATUS_FINISHED,
                        collectionsService, accessControlService, domainObjectService);
                else
                    statusSetter.changeStatusForDo(dObj.getId(), CustomModuleConstants.STATUS_EXISTS,
                            collectionsService, accessControlService, domainObjectService);
            }
        }
    }

    private void restoreInventory(Id objId) {
        String invType = crudService.getDomainObjectType(objId);
        ArrayList<List<DomainObject>> allLinkedObjectsList = new ArrayList<>();
        InventoryCopierActionHandler linkedObjectsGetter = new InventoryCopierActionHandler();

        for (String linkedDoName : CustomModuleConstants.LINKED_DO_TYPES)
            allLinkedObjectsList.add(linkedObjectsGetter.getLinkedObjects(linkedDoName, objId, crudService));

        for (List<DomainObject> objList : allLinkedObjectsList) {
            String objType;
            if (!objList.isEmpty()) {
                for (DomainObject dObject : objList) {
                    objType = dObject.getTypeName();
                    restoreObject(dObject.getId(),objType);
                }
            }
        }
        restoreObject(objId, invType);
    }

    private void restoreTerritory(Id objId) {
        String objType = crudService.getDomainObjectType(objId);
        List<Id> inventoryIdList = crudService.findLinkedDomainObjectsIds(
                objId, "inventory", CustomModuleConstants.TERRITORY_LINKED_FIELD);

        for (Id invId : inventoryIdList)
            restoreInventory(invId);

        restoreObject(objId, objType);
    }
}