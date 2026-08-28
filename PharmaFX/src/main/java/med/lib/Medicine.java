package med.lib;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Medicine extends Item {

    private double dose;
    private String unit;
    private LocalDate expirationDate;

    public Medicine(String name, String manufacturer, double dose, String unit, double unitPrice, int quantity, LocalDate expirationDate) {
        super(name, manufacturer, unitPrice, quantity);
        this.dose = dose;
        this.unit = unit;
        this.expirationDate = expirationDate;
        addPrefixCodeToId("M-");
    }

    public Medicine(String name, String manufacturer, double dose, String unit, double unitPrice, int quantity, String expirationDate) {
        super(name, manufacturer, unitPrice, quantity);
        this.dose = dose;
        this.unit = unit;
        addPrefixCodeToId("M-");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(expirationDate, format);
        this.expirationDate = date;
    }

    public double getDose() {
        return dose;
    }

    public void setDose(double dose) {
        this.dose = dose;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean hasExpired() {
        return LocalDate.now().isAfter(getExpirationDate());
    }

    @Override
    public String toString() {
        return super.toString() + "\nDose : " + this.dose + "\nUnit : " + this.unit + "\nExpiration Date : "
                + this.expirationDate;
    }
}
