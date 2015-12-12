package me.valour.bowls.models;

public class User {

    private double subtotal;
    private double tax;
    private double tip;

    public User(){
        subtotal = 0.0;
        tax = 0.0;
        tip = 0.0;

    }

    /**
     * @return the subtotal
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * @param subtotal the subtotal to set
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double plusSubtotal(double amount){
        this.subtotal += amount;
        return subtotal;
    }

    public double subtractSubtotal(double amount){
        this.subtotal -= amount;
        return subtotal;
    }

    /**
     * @return the tax
     */
    public double getTax() {
        return tax;
    }

    /**
     * @param tax the tax to set
     */
    public void setTax(double tax) {
        this.tax = tax;
    }

    public void applyTax(double percent){
        this.tax = subtotal*percent;
    }

    /**
     * @return the tip
     */
    public double getTip() {
        return tip;
    }

    /**
     * @param tip the tip to set
     */
    public void setTip(double tip) {
        this.tip = tip;
    }

    public void applyTip(double percent){
        this.tip = subtotal*percent;
    }

    public double getTotal(){
        return subtotal + tax + tip;
    }

}