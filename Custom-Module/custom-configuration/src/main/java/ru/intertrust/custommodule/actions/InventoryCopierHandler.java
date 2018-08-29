package ru.intertrust.custommodule.actions;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.business.api.dto.IdentifiableObjectCollection;
import ru.intertrust.cm.core.gui.api.server.action.ActionHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.action.SimpleActionContext;
import ru.intertrust.cm.core.gui.model.action.SimpleActionData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ComponentName("inventory.copier")
public class InventoryCopierHandler extends ActionHandler<SimpleActionContext, SimpleActionData> {

    @Autowired
    private CollectionsService collectionsService;

    @Autowired
    CrudService crudService;

    private static final String IS_BUILDINGS_FILLED_FIELD = "is_buildings_filled";
    private static final String IS_RIGHTHOLDERS_FILLED_FIELD = "is_rightholders_filled";
    private static final String IS_ELEMENTS_FILLED_FIELD = "is_elements_filled";
    private static final String QUERY_STATUS_BY_NAME = "select * from status where name = 'Draft inventory'";
    private static final Boolean CHECKBOX_FALSE_VALUE = false;
    private static Id rootInventoryId;
    private static final String LINKED_DO_REFERENCE_FIELD = "inventory_id";
    private static final String INVENTORY_TERRITORY_REFERENCE_FIELD = "territory_id";
    private final String[] LINKED_DO_NAMES = {
            "rightholder",
            "build_residential",
            "build_permanent",
            "build_temporary",
            "gard_green_hedge",
            "gard_bush",
            "gard_lawn",
            "gard_parterre",
            "gard_tree",
            "gard_vertical",
            "maf_playground_equipment",
            "maf_trash_can",
            "maf_benches",
            "maf_tables",
            "maf_canopies",
            "maf_fountains",
            "maf_pavilions",
            "maf_sport_equipments",
            "maf_terraces",
            "pl_bicycle_parking",
            "pl_bicycle_road",
            "pl_car_parking",
            "pl_container_square",
            "pl_dog_walking_area",
            "pl_playground",
            "pl_sidewalk",
            "pl_sport_ground",
            "pl_yard_passage",
            "others_barrier",
            "other_fences",
            "other_information_stand",
            "other_lighting_device",
            "other_pond",
            "other_power_pylon",
            "other_rampant",
            "other_underground_hatch"
    };

    @Override
    public SimpleActionData executeAction(SimpleActionContext context){
        final Id statusDraftId;
        IdentifiableObjectCollection identifiableObjects = collectionsService.findCollectionByQuery(QUERY_STATUS_BY_NAME);

        if (identifiableObjects!=null && identifiableObjects.size()>0) {
            statusDraftId = identifiableObjects.get(0).getId();
            rootInventoryId = context.getRootObjectId();
            DomainObject rootInventoryObj = crudService.find(rootInventoryId);
            Id territoryReference = rootInventoryObj.getReference(INVENTORY_TERRITORY_REFERENCE_FIELD);

            ArrayList<List<DomainObject>> allLinkedObjectsList = new ArrayList<>();

            for (String linkedDoName : LINKED_DO_NAMES)
                if (!linkedDoName.isEmpty())
                    allLinkedObjectsList.add(getLinkedObjects(linkedDoName));

            Id newInventoryId = copyInventory(rootInventoryObj, territoryReference, statusDraftId);
            for (List<DomainObject> objList : allLinkedObjectsList) {
                if (!objList.isEmpty()) {
                    for (DomainObject dObject : objList) {
                        copyLinkedObject(dObject, newInventoryId);
                    }
                }
            }
        }

        return new SimpleActionData();
    }

    private Id copyInventory(DomainObject rootInventory, Id terrReference, Id status) {

        List<String> fields = rootInventory.getFields();
        DomainObject newInventory = crudService.createDomainObject(rootInventory.getTypeName());

        for (String fieldName : fields) {
            if (fieldName.equals("territory_id")) {
                newInventory.setReference(fieldName, terrReference);
            }
            if (fieldName.equals("status")) {
                newInventory.setReference(fieldName, status);
            }
            if (fieldName.equals("start_time")) {
                newInventory.setTimestamp(fieldName, new Date());
            }

            if (fieldName.equals(IS_BUILDINGS_FILLED_FIELD) ||
                fieldName.equals(IS_RIGHTHOLDERS_FILLED_FIELD) ||
                fieldName.equals(IS_ELEMENTS_FILLED_FIELD)) {
                newInventory.setBoolean(fieldName, CHECKBOX_FALSE_VALUE);
            }
        }
        return crudService.save(newInventory).getId();
    }

    private DomainObject copyLinkedObject(DomainObject rootDo, Id invReference) {
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
        return crudService.save(newDo);
    }

    private List<DomainObject> getLinkedObjects(String doName) {
        return crudService.findLinkedDomainObjects(rootInventoryId, doName, LINKED_DO_REFERENCE_FIELD);
    }
}






