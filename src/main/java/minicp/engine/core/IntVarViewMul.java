/*
 * mini-cp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License  v3
 * as published by the Free Software Foundation.
 *
 * mini-cp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY.
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with mini-cp. If not, see http://www.gnu.org/licenses/lgpl-3.0.en.html
 *
 * Copyright (c)  2017. by Laurent Michel, Pierre Schaus, Pascal Van Hentenryck
 */


package minicp.engine.core;


import minicp.util.InconsistencyException;

public class IntVarViewMul implements IntVar {

    private final int a;
    private final IntVar x;

    public IntVarViewMul(IntVar x, int a) {
        assert(a > 0);
        this.a = a;
        this.x = x;
    }

    @Override
    public Solver getSolver() {
        return x.getSolver();
    }

    @Override
    public void whenDomainChange(ConstraintClosure.Filtering c) {
        x.whenDomainChange(c);
    }

    @Override
    public void whenBind(ConstraintClosure.Filtering c) {
        x.whenBind(c);
    }

    @Override
    public void whenBoundsChange(ConstraintClosure.Filtering c) {
        x.whenBoundsChange(c);
    }

    @Override
    public void propagateOnDomainChange(Constraint c) {
        x.propagateOnDomainChange(c);
    }

    @Override
    public void propagateOnBind(Constraint c) {
        x.propagateOnBind(c);
    }

    @Override
    public void propagateOnBoundChange(Constraint c) {
        x.propagateOnBoundChange(c);
    }

    @Override
    public int getMin() {
        return a * x.getMin();
    }

    @Override
    public int getMax() {
        return a * x.getMax();
    }

    @Override
    public int getSize() {
        return x.getSize();
    }

    public int[] getValues() {
        return x.getValues();
    }

    @Override
    public boolean isBound() {
        return x.isBound();
    }

    @Override
    public boolean contains(int v) {
        return (v % a != 0) ? false : x.contains(v / a);
    }

    @Override
    public void remove(int v) throws InconsistencyException {
        if (v % a == 0) {
            x.remove(v / a);
        }
    }

    @Override
    public void assign(int v) throws InconsistencyException {
        if (v % a == 0) {
            x.assign(v / a);
        } else {
            throw new InconsistencyException();
        }
    }

    @Override
    public int removeBelow(int v) throws InconsistencyException {
        return (x.removeBelow(ceilDiv(v, a))) * a;
    }

    @Override
    public int removeAbove(int v) throws InconsistencyException {
        return (x.removeAbove(floorDiv(v, a))) * a;
    }


    // Java's division always rounds to the integer closest to zero, but we need flooring/ceiling versions.
    private int floorDiv(int a, int b) {
        int q = a / b;
        return (a < 0 && q * b != a) ? q - 1 : q;
    }

    private int ceilDiv(int a, int b) {
        int q = a / b;
        return (a > 0 && q * b != a) ? q + 1 : q;
    }

    /**
     * set the first values of <code>dest</code> to the ones
     * present in the set
     * @param dest, an array large enough dest.length >= getSize()
     * @return the size of the set
     */
    public int fillArray(int [] dest) {
        int s = x.getSize();
        System.arraycopy(x.getValues(), 0, dest, 0, s);
        return s;
    }
}
