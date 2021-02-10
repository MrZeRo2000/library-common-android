package com.romanpulov.library.common.db;

public interface DBController {
    void closeDB();
    void openDB();
    void dbDataChanged();
    String getDBName();
}
