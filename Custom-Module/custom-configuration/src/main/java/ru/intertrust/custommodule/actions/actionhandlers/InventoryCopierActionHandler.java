package ru.intertrust.custommodule.actions.actionhandlers;
//TODO: сделать обновление после копирования

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.AttachmentService;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.business.api.dto.IdentifiableObjectCollection;
import ru.intertrust.cm.core.gui.api.server.action.ActionHandler;
import ru.intertrust.cm.core.gui.impl.server.action.SimpleActionHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.action.ActionContext;
import ru.intertrust.cm.core.gui.model.action.ActionData;
import ru.intertrust.cm.core.gui.model.action.SimpleActionContext;
import ru.intertrust.cm.core.gui.model.action.SimpleActionData;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;
import ru.intertrust.custommodule.gui.model.client.NewActionContext;
import ru.intertrust.custommodule.gui.model.client.NewActionData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ComponentName("inventory.copier")
public class InventoryCopierActionHandler extends ActionHandler<SimpleActionContext, SimpleActionData> {

    @Autowired
    private CollectionsService collectionsService;

    @Autowired
    CrudService crudService;

    @Autowired
    AttachmentService attachmentService;

    @Override
    public SimpleActionData executeAction(SimpleActionContext context) {
        StatusSetter statusSetter = new StatusSetter();
        Id statusDraftId;
        Id rootInventoryId;
        statusDraftId = statusSetter.getStatusIdByName(CustomModuleConstants.STATUS_DRAFT, collectionsService);
        rootInventoryId = context.getRootObjectId();
        DomainObject rootInventoryObj = crudService.find(rootInventoryId);
        Id territoryReference = rootInventoryObj.getReference(CustomModuleConstants.TERRITORY_LINKED_FIELD);

        ArrayList<List<DomainObject>> allLinkedObjectsList = new ArrayList<>();

        for (String linkedDoName : CustomModuleConstants.LINKED_DO_TYPES)
            allLinkedObjectsList.add(getLinkedObjects(linkedDoName, rootInventoryId, crudService));

        Id newInventoryId = copyInventory(rootInventoryObj, territoryReference, statusDraftId);
        for (List<DomainObject> objList : allLinkedObjectsList) {
            if (!objList.isEmpty()) {
                for (DomainObject dObject : objList) {
                    copyLinkedObject(dObject, newInventoryId);
                }
            }
        }
        return new SimpleActionData();
}

    private Id copyInventory(DomainObject rootInventory, Id terrReference, Id status) {

        List<String> fields = rootInventory.getFields();
        DomainObject newInventory = crudService.createDomainObject(rootInventory.getTypeName());

        for (String fieldName : fields) {
            if (!fieldName.equals("end_time"))
                newInventory.setValue(fieldName, rootInventory.getValue(fieldName));

            if (fieldName.equals("territory_id"))
                newInventory.setReference(fieldName, terrReference);

            if (fieldName.equals("status"))
                newInventory.setReference(fieldName, status);

            if (fieldName.equals("start_time"))
                newInventory.setTimestamp(fieldName, new Date());

            if (fieldName.equals("territory_name"))
                newInventory.setValue(fieldName, rootInventory.getValue("territory_name"));

            if (fieldName.equals(CustomModuleConstants.IS_BUILDINGS_FILLED_FIELD) ||
                    fieldName.equals(CustomModuleConstants.IS_RIGHTHOLDERS_FILLED_FIELD) ||
                    fieldName.equals(CustomModuleConstants.IS_ELEMENTS_FILLED_FIELD)) {
                newInventory.setBoolean(fieldName, false);
            }
        }
        newInventory = crudService.save(newInventory);
        List<DomainObject> attachList = attachmentService
                .findAttachmentDomainObjectsFor(rootInventory.getId(), "territory_photo");

        if (!attachList.isEmpty()) {
            List<Id> attachIds = new ArrayList<>();

            for (DomainObject domainObject : attachList) {
                attachIds.add(domainObject.getId());
            }

            attachmentService.copyAttachments(attachIds, newInventory.getId(), "territory_photo");
        }
        return newInventory.getId();
    }

    private void copyLinkedObject(DomainObject rootDo, Id invReference) {
        List<String> fields = rootDo.getFields();
        String doType = rootDo.getTypeName();
        DomainObject newDo = crudService.createDomainObject(doType);

        for (String fieldName : fields) {
            if (!fieldName.equals("id") && !fieldName.equals("created_date") && !fieldName.equals("updated_date")) {
                newDo.setValue(fieldName, rootDo.getValue(fieldName));
            }
            if (fieldName.equals("inventory_id")) {
                newDo.setReference(fieldName, invReference);
            }
        }
        newDo = crudService.save(newDo);
        if (!rootDo.getTypeName().equals("rightholder")) {
            List<DomainObject> attachList = attachmentService.findAttachmentDomainObjectsFor(rootDo.getId());
            if (!attachList.isEmpty())
                attachmentService.copyAttachmentsFrom(rootDo.getId(), getDoAttachmentName(rootDo), newDo.getId(), getDoAttachmentName(newDo));
        }
    }

    public List<DomainObject> getLinkedObjects(String doName, Id inventoryId, CrudService crudService) {
        return crudService.findLinkedDomainObjects(inventoryId, doName, CustomModuleConstants.LINKED_DO_INVENTORY_FIELD);
    }

    private String getDoAttachmentName(DomainObject domainObject) {
        String doName = domainObject.getTypeName();
        return doName.substring(doName.indexOf("_") + 1) + "_photo";
    }
}






