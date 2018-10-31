package ru.intertrust.custommodule.actions.constants;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.config.ConfigurationExplorer;
import java.util.HashMap;

public class CustomModuleConstants {

    @Autowired
    ConfigurationExplorer configurationExplorer;

    public static final String QUERY_STATUS_BY_NAME = "select * from status where name = {0}";
    public static final String QUERY_STATUS_BY_ID = "select * from status where id = {0}";
    public static final String QUERY_INVENTORIES_BY_TERRITORY_ID = "select * from inventory where territory_id = {0}";
    public static final String QUERY_INVENTORIES_BY_STATUS = "select * from inventory where status = {0}";

    public static final String IS_BUILDINGS_FILLED_FIELD = "is_buildings_filled";
    public static final String IS_RIGHTHOLDERS_FILLED_FIELD = "is_rightholders_filled";
    public static final String IS_ELEMENTS_FILLED_FIELD = "is_elements_filled";

    public static final String TERRITORY_LINKED_FIELD = "territory_id";
    public static final String TERRITORY_NAME_FIELD = "name";
    public static final String INVENTORY_TERRITORY_NAME_FIELD = "territory_name";
    public static final String DATE_START_FIELD = "start_time";
    public static final String STATUS_FIELD_NAME = "name";
    public static final String STATUS_DRAFT = "Draft inventory";
    public static final String STATUS_FINISHED = "Finished inventory";
    public static final String STATUS_DELETED = "Deleted";
    public static final String STATUS_EXISTS = "Exists";
    public static final String STATUS_FIELD = "status";
    public static final String LINKED_DO_INVENTORY_FIELD = "inventory_id";

    public static final String INVENTORY_JSON_ATTACHMENTS_FIELD = "territory_import";
    public static final String INVENTORY_FIELD = "inventory";
    public static final String ATTACHMENT_PATH_FIELD = "path";
    public static final String ATTACHMENT_NAME_FIELD = "name";
    public static final String INVENTORY_PHOTO_ATTACHMENTS_FIELD = "territory_photo";
    public static final String ATTACHMENT_MIME_TYPE_FIELD = "mimetype";
    public static final String JSON_FILE_NAME = "import.json";

    public static final String[] COMMON_FIELD_LIST = {
         "id",
         "updated_date",
         "status",
         "id_type",
         "created_date",
         "created_by",
         "created_by_type",
         "updated_by",
         "updated_by_type",
         "inventory_id",
         "inventory_id_type",
         "status_type",
         "access_object_id",
         "territory_id"
    };

    public static final String[] IMPORT_COMMON_FIELD_LIST = {
            "do_type",
            "id",
            "updated_date",
            "status"
    };

    public static final String[] LINKED_DO_TYPES = {
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
            "other_barrier",
            "other_fences",
            "other_information_stand",
            "other_lighting_device",
            "other_pond",
            "other_power_pylon",
            "other_rampant",
            "other_underground_hatch"
    };

    private static HashMap<Id, Id> terrReferenceMap = new HashMap<>();

    public static HashMap<Id, Id> getTerrReferenceMap() {
        return terrReferenceMap;
    }

    public static void setTerrReference(Id userId, Id territoryReference) {
        CustomModuleConstants.terrReferenceMap.put(userId, territoryReference);
    }
}
