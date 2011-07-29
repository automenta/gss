/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss;

/**
 * Contains current parameters decided by user defining their relative "interest" in a particular data source.
 * Can be extended with more parameters later.
 * @author seh
 */
public class DataInterest {
    private double importance;

    public DataInterest(double importance) {
        super();
        setImportance(importance);
    }
    
    public double getImportance() {
        return importance;
    }

    public void setImportance(double importance) {
        this.importance = importance;
    }
    
}
