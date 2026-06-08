package com.example.backend.formengine.domain;

import java.util.Set;

public final class ModuleKeys {

    public static final String MEMBERS = "members";
    public static final String CLERGY = "clergy";
    public static final String BAPTISMS = "baptisms";
    public static final String FAMILY_MEMBERS = "family_members";
    public static final String STAFF_MINISTERS = "staff_ministers";
    public static final String OFFICE_STAFF = "office_staff";
    public static final String WORKERS = "workers";
    public static final String EMERGENCY_CONTACTS = "emergency_contacts";
    public static final String PARISH_COUNCIL = "parish_council";
    public static final String CONTRIBUTIONS = "contributions";
    public static final String TRANSFERS = "transfers";
    public static final String DECEASED = "deceased";
    public static final String SUNDAY_SCHOOL = "sunday_school";
    public static final String ABNET_SCHOOL = "abnet_school";

    private static final Set<String> ALL = Set.of(
            MEMBERS, CLERGY, BAPTISMS, FAMILY_MEMBERS, STAFF_MINISTERS, OFFICE_STAFF,
            WORKERS, EMERGENCY_CONTACTS, PARISH_COUNCIL, CONTRIBUTIONS, TRANSFERS,
            DECEASED, SUNDAY_SCHOOL, ABNET_SCHOOL
    );

    private ModuleKeys() {
    }

    public static boolean isValid(String moduleKey) {
        return moduleKey != null && ALL.contains(moduleKey);
    }

    public static Set<String> all() {
        return ALL;
    }
}
