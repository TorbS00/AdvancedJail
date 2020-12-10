package com.github.beastyboo.advancedjail.domain.entity;

/**
 * Created by Torbie on 10.12.2020.
 */
public class Crime {

    private final String name;
    private final double bill;
    private final int penalty;

    public Crime(String name, double bill, int penalty) {
        this.name = name;
        this.bill = bill;
        this.penalty = penalty;
    }

    public String getName() {
        return name;
    }

    public double getBill() {
        return bill;
    }

    public int getPenalty() {
        return penalty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Crime crime = (Crime) o;

        if (Double.compare(crime.getBill(), getBill()) != 0) return false;
        if (getPenalty() != crime.getPenalty()) return false;
        return getName().equals(crime.getName());
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getName().hashCode();
        temp = Double.doubleToLongBits(getBill());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getPenalty();
        return result;
    }

    @Override
    public String toString() {
        return "Crime{" +
                "name='" + name + '\'' +
                ", bill=" + bill +
                ", penalty=" + penalty +
                '}';
    }
}
