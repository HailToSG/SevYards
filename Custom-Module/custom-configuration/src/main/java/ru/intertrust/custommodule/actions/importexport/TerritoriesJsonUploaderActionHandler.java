package ru.intertrust.custommodule.actions.importexport;
//TODO узнать где берётся имя и номер территории
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.gui.api.server.action.ActionHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.action.SimpleActionContext;
import ru.intertrust.cm.core.gui.model.action.SimpleActionData;
import ru.intertrust.custommodule.actions.actionhandlers.InventoryCopierActionHandler;
import ru.intertrust.custommodule.actions.actionhandlers.StatusSetter;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ComponentName("territories.json.exporter")
public class TerritoriesJsonUploaderActionHandler extends ActionHandler<SimpleActionContext, SimpleActionData> {

    @Autowired
    CrudService crudService;

    @Autowired
    CollectionsService collectionsService;

    @Override
    public SimpleActionData executeAction(SimpleActionContext context) {
        Id currentTerrId = context.getRootObjectId();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss.S").create();
        JsonObject jsonObject = new JsonObject();
        List<Territory> territories = new ArrayList<>();
        Inventory inventory;
        Territory territory;
        List objectsList;
        StatusSetter statusSetter = new StatusSetter();
        List<Value> params = new ArrayList<>();
        Value currentTerrReference = new ReferenceValue(currentTerrId);
        params.add(currentTerrReference);
        DomainObject draftInventoryObject = null;
        IdentifiableObjectCollection draftInventoryCollection = collectionsService
                .findCollectionByQuery(CustomModuleConstants.QUERY_INVENTORIES_BY_TERRITORY_ID, params);

        if (draftInventoryCollection != null && draftInventoryCollection.size() > 0) {
            for (IdentifiableObject identifiableObject : draftInventoryCollection) {
                DomainObject dobj = crudService.find(identifiableObject.getId());
                if (dobj.getStatus() != statusSetter.getStatusIdByName(
                        CustomModuleConstants.STATUS_DRAFT, collectionsService)) {
                    draftInventoryObject = dobj;
                }
            }
        }

        if (draftInventoryObject != null) {
            Id terrId = draftInventoryObject.getReference("territory_id");
            objectsList = getInventoryObjects(draftInventoryObject.getId(), crudService);
            territory = getTerritoryProperties(terrId, crudService);
            inventory = getInventoryProperties(draftInventoryObject.getId(), crudService);
            inventory.setAllLinkedObjects(objectsList);
            territory.setInventory(inventory);
            territories.add(territory);
        }
        jsonObject.setTerritories(territories);

        String filePath = "C:\\Users\\EgorMobile\\Desktop\\";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String fileName = simpleDateFormat.format(new Date()) + ".json";
        filePath += fileName;

        try {
            FileWriter fw = new FileWriter(filePath);
            String jsonString = gson.toJson(jsonObject);
            fw.write(new String(jsonString.getBytes(), "Windows-1251"));
            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();

        return new SimpleActionData();
    }

    private Territory getTerritoryProperties(Id territoryId, CrudService crudService) {
        Territory territory = new Territory();
        if (territoryId != null) {
            DomainObject terrDoObj = crudService.find(territoryId);
            territory.setId(territoryId.toStringRepresentation());
            territory.setName(terrDoObj.getString("name"));
            territory.setNumber(terrDoObj.getString("number"));
            territory.setType(Math.toIntExact(terrDoObj.getLong("territory_type")));
        }
        return territory;
    }

    private Inventory getInventoryProperties(Id inventoryId, CrudService crudService) {
        Inventory inventory = new Inventory();
        if (inventoryId != null) {
            DomainObject inventoryDoObj = crudService.find(inventoryId);
            inventory.setId(inventoryId.toStringRepresentation());
            DomainObject terrObj = crudService.find(inventoryDoObj.getReference("territory_id"));

            switch (Math.toIntExact(terrObj.getLong("territory_type"))) {
                case 0:
                    inventory.setKind(Math.toIntExact(inventoryDoObj.getLong("kind_of_territory")));
                    inventory.setSummaryArea(Math.toIntExact(inventoryDoObj.getLong("summary_area")));
                    break;
                case 5:
                    inventory.setSummaryArea(Math.toIntExact(inventoryDoObj.getLong("summary_area")));
                    break;
                case 10:
                case 15:
                    inventory.setFacadeState(Math.toIntExact(inventoryDoObj.getLong("facade_state")));
                    inventory.setTerritoryState(Math.toIntExact(inventoryDoObj.getLong("territory_state")));
                    break;
                default:
                    break;
            }
        }
        return inventory;
    }

    private List<Object> getInventoryObjects(Id inventoryDoId, CrudService crudService) {
        StatusSetter statusSetter = new StatusSetter();
        ArrayList<String> fields;
        List<Object> linkedObjectsList = new ArrayList<>();
        List<DomainObject> doObjList;
        LinkedTreeMap<String, Object> objMap;
        InventoryCopierActionHandler objectFinder = new InventoryCopierActionHandler();

        for (String doName : CustomModuleConstants.LINKED_DO_TYPES) {
            doObjList = objectFinder.getLinkedObjects(doName, inventoryDoId, crudService);
            if (doObjList != null && !doObjList.isEmpty())
                for (DomainObject dObj : doObjList) {
                    if (statusSetter.getQueryStatusById(dObj.getStatus(), collectionsService).equals("Deleted"))
                        continue;
                    objMap = new LinkedTreeMap<>();
                    fields = dObj.getFields();
                    objMap.put("do_type", dObj.getTypeName());
                    objMap.put("id", dObj.getId().toStringRepresentation());
                    objMap.put("updated_date", new Timestamp(dObj.getModifiedDate().getTime()).toString());
                    for (String field : fields) {

                        if (!Arrays.asList(CustomModuleConstants.COMMON_FIELD_LIST).contains(field)) {
                            objMap.put(field, dObj.getValue(field).toString());
                        }
                    }
                    linkedObjectsList.add(objMap);
                }
        }
        return linkedObjectsList;
    }
}
