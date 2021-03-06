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
import org.junit.Test;

import static minicp.cp.Factory.makeIntVar;
import static minicp.cp.Factory.makeSolver;
import static minicp.cp.Heuristics.firstFail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class Element2DTest {

    @Test
    public void element2dTest1() {

        try {

            Solver cp = makeSolver();
            IntVar x = makeIntVar(cp, -2, 40);
            IntVar y = makeIntVar(cp, -3, 10);
            IntVar z = makeIntVar(cp, 2, 40);

            int[][] T = new int[][]{
                    {9, 8, 7, 5, 6},
                    {9, 1, 5, 2, 8},
                    {8, 3, 1, 4, 9},
                    {9, 1, 2, 8, 6},
            };

            cp.post(new Element2D(T, x, y, z));

            assertEquals(0, x.getMin());
            assertEquals(0, y.getMin());
            assertEquals(3, x.getMax());
            assertEquals(4, y.getMax());
            assertEquals(2, z.getMin());
            assertEquals(9, z.getMax());

            z.removeAbove(7);
            cp.fixPoint();

            assertEquals(1, y.getMin());

            x.remove(0);
            cp.fixPoint();

            assertEquals(6, z.getMax());
            assertEquals(3, x.getMax());

            y.remove(4);
            cp.fixPoint();

            assertEquals(5, z.getMax());
            assertEquals(2, z.getMin());

            y.remove(2);
            cp.fixPoint();

            assertEquals(4, z.getMax());
            assertEquals(2, z.getMin());


        } catch (InconsistencyException e) {
            fail("should not fail");
        }
    }

    @Test
    public void element2dTest2() {

        try {

            Solver cp = new Solver();
            IntVar x = makeIntVar(cp, -2, 40);
            IntVar y = makeIntVar(cp, -3, 10);
            IntVar z = makeIntVar(cp, -20, 40);

            int[][] T = new int[][]{
                    {9, 8, 7, 5, 6},
                    {9, 1, 5, 2, 8},
                    {8, 3, 1, 4, 9},
                    {9, 1, 2, 8, 6},
            };

            cp.post(new Element2D(T, x, y, z));

            DFSearch dfs = new DFSearch(cp.getTrail(), firstFail(x, y, z));
            dfs.onSolution(() ->
                    assertEquals(T[x.getMin()][y.getMin()], z.getMin())
            );
            SearchStatistics stats = dfs.start();

            assertEquals(20, stats.nSolutions);


        } catch (InconsistencyException e) {
            fail("should not fail");
        }
    }

}
