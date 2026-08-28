package com.example.pharmafx;

import med.lib.MedPharma;

public class Records {
    public static final String DATA_FILE = "records.txt";
    public static final MedPharma pharma = MedPharma.load(DATA_FILE);

    public static void save() {
        pharma.saveToFile(DATA_FILE);
    }
}
