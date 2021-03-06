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

package minicp.engine.constraints;

import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.DFSearch;
import minicp.search.SearchStatistics;
import minicp.util.InconsistencyException;
import minicp.util.NotImplementedException;
import org.junit.Test;

import static minicp.cp.Factory.makeIntVar;
import static minicp.cp.Factory.makeSolver;
import static minicp.cp.Heuristics.firstFail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;


public class Element1DTest {

    @Test
    public void element1dTest1() { //Test
        try {
            try {

                Solver cp = makeSolver();
                IntVar x = makeIntVar(cp, -2, 40);
                IntVar z = makeIntVar(cp, 2, 40);

                int[] T = new int[]{9, 8, 7, 5, 6};

                cp.post(new Element1D(T, x, z));

                assertEquals(0, x.getMin());
                assertEquals(4, x.getMax());
                assertEquals(5, z.getMin());
                assertEquals(9, z.getMax());

                z.removeAbove(7);
                cp.fixPoint();

                assertEquals(2, x.getMin());


                x.remove(3);
                cp.fixPoint();

                assertEquals(7, z.getMax());
                assertEquals(6, z.getMin());


            } catch (InconsistencyException e) {
                fail("should not fail");
            }
        } catch (NotImplementedException e) {
            e.print();
        }
    }


    @Test
    public void element1dTest2() {
        try {
            try {

                Solver cp = new Solver();
                IntVar x = makeIntVar(cp, -2, 40);
                IntVar z = makeIntVar(cp, 2, 40);


                int[] T = new int[]{9, 8, 7, 5, 6};

                cp.post(new Element1D(T, x, z));

                DFSearch dfs = new DFSearch(cp.getTrail(), firstFail(x, z));
                dfs.onSolution(() ->
                        assertEquals(T[x.getMin()], z.getMin())
                );
                SearchStatistics stats = dfs.start();

                assertEquals(5, stats.nSolutions);

            } catch (InconsistencyException e) {
                fail("should not fail");
            }
        } catch (NotImplementedException e) {
            e.print();
        }
    }

    @Test
    public void element1dTest3() {
        try {
            try {

                Solver cp = new Solver();
                IntVar y = makeIntVar(cp, 0, 4);
                IntVar z = makeIntVar(cp, 5, 9);


                int[] T = new int[]{9, 8, 7, 5, 6};

                cp.post(new Element1D(T, y, z));

                y.remove(3); //T[4]=5
                y.remove(0); //T[0]=9

                cp.fixPoint();

                assertEquals(6, z.getMin());
                assertEquals(8, z.getMax());
            } catch (InconsistencyException e) {
                fail("should not fail");
            }
        } catch (NotImplementedException e) {
            e.print();
        }
    }

    @Test
    public void element1dTest4() {
        try {
            try {

                Solver cp = new Solver();
                IntVar y = makeIntVar(cp, 0, 4);
                IntVar z = makeIntVar(cp, 5, 9);


                int[] T = new int[]{9, 8, 7, 5, 6};

                cp.post(new Element1D(T, y, z));

                z.remove(9); //new max is 8
                z.remove(5); //new min is 6
                cp.fixPoint();

                assertFalse(y.contains(0));
                assertFalse(y.contains(3));
            } catch (InconsistencyException e) {
                fail("should not fail");
            }
        } catch (NotImplementedException e) {
            e.print();
        }
    }
}
