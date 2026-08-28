package med.lib;

public class MedicalAccessory extends Item {

    private String modelNo;
    private boolean hasDiscount;

    public MedicalAccessory(String name, String manufacturer, String modelNo, double unitPrice, int quantity) {
        super(name, manufacturer, unitPrice, quantity);
        this.modelNo = modelNo;
        addPrefixCodeToId("MA-");
    }

    public String getModelNo() {
        return modelNo;
    }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public boolean isHasDiscount() {
        return hasDiscount;
    }

    public void setHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    @Override
    public String toString() {
        return super.toString() + "\nModel No. : " + this.modelNo + "\nDiscount : " + isHasDiscount();
    }

    @Override
    public double getBill() {
        double base = super.getBill();
        if (isHasDiscount()) {
            return base * 0.9;
        }
        return base;
    }
}
