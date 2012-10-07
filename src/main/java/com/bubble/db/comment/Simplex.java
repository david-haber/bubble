package com.bubble.db.comment;

public class Simplex {
    private static final float EPSILON = (float) 1.0E-10;
    private float[][] a;   // tableaux
    private int M;          // number of constraints
    private int N;          // number of original variables

    private int[] basis;    // basis[i] = basic variable corresponding to row i
                            // only needed to print out solution, not book

    // sets up the simplex tableaux
    public Simplex(float[][] A, float[] b, float[] c) {
        M = b.length;
        N = c.length;
        a = new float[M+1][N+M+1];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                a[i][j] = A[i][j];
        for (int i = 0; i < M; i++) a[i][N+i] = (float) 1.0;
        for (int j = 0; j < N; j++) a[M][j]   = c[j];
        for (int i = 0; i < M; i++) a[i][M+N] = b[i];

        basis = new int[M];
        for (int i = 0; i < M; i++) basis[i] = N + i;

        solve();

        // check optimality conditions
        assert check(A, b, c);
    }

    // run simplex algorithm starting from initial BFS
    private void solve() {
        while (true) {

            // find entering column q
            int q = bland();
            if (q == -1) break;  // optimal

            // find leaving row p
            int p = minRatioRule(q);
            if (p == -1) throw new RuntimeException("Linear program is unbounded");

            // pivot
            pivot(p, q);

            // update basis
            basis[p] = q;
        }
    }

    // lowest index of a non-basic column with a positive cost
    private int bland() {
        for (int j = 0; j < M + N; j++)
            if (a[M][j] > 0) return j;
        return -1;  // optimal
    }

   // index of a non-basic column with most positive cost
    @SuppressWarnings("unused")
	private int dantzig() {
        int q = 0;
        for (int j = 1; j < M + N; j++)
            if (a[M][j] > a[M][q]) q = j;

        if (a[M][q] <= 0) return -1;  // optimal
        else return q;
    }

    // find row p using min ratio rule (-1 if no such row)
    private int minRatioRule(int q) {
        int p = -1;
        for (int i = 0; i < M; i++) {
            if (a[i][q] <= 0) continue;
            else if (p == -1) p = i;
            else if ((a[i][M+N] / a[i][q]) < (a[p][M+N] / a[p][q])) p = i;
        }
        return p;
    }

    // pivot on entry (p, q) using Gauss-Jordan elimination
    private void pivot(int p, int q) {

        // everything but row p and column q
        for (int i = 0; i <= M; i++)
            for (int j = 0; j <= M + N; j++)
                if (i != p && j != q) a[i][j] -= a[p][j] * a[i][q] / a[p][q];

        // zero out column q
        for (int i = 0; i <= M; i++)
            if (i != p) a[i][q] = (float) 0.0;

        // scale row p
        for (int j = 0; j <= M + N; j++)
            if (j != q) a[p][j] /= a[p][q];
        a[p][q] = (float) 1.0;
    }

    // return optimal objective value
    public float value() {
        return -a[M][M+N];
    }

    // return primal solution vector
    public float[] primal() {
        float[] x = new float[N];
        for (int i = 0; i < M; i++)
            if (basis[i] < N) x[basis[i]] = a[i][M+N];
        return x;
    }

    // return dual solution vector
    public float[] dual() {
        float[] y = new float[M];
        for (int i = 0; i < M; i++)
            y[i] = -a[M][N+i];
        return y;
    }


    // is the solution primal feasible?
    private boolean isPrimalFeasible(float[][] A, float[] b) {
        float[] x = primal();

        // check that x >= 0
        for (int j = 0; j < x.length; j++) {
            if (x[j] < 0.0) {
                System.out.println("x[" + j + "] = " + x[j] + " is negative");
                return false;
            }
        }

        // check that Ax <= b
        for (int i = 0; i < M; i++) {
            float sum = (float) 0.0;
            for (int j = 0; j < N; j++) {
                sum += A[i][j] * x[j];
            }
            if (sum > b[i] + EPSILON) {
                System.out.println("not primal feasible");
                System.out.println("b[" + i + "] = " + b[i] + ", sum = " + sum);
                return false;
            }
        }
        return true;
    }

    // is the solution dual feasible?
    private boolean isDualFeasible(float[][] A, float[] c) {
        float[] y = dual();

        // check that y >= 0
        for (int i = 0; i < y.length; i++) {
            if (y[i] < 0.0) {
                System.out.println("y[" + i + "] = " + y[i] + " is negative");
                return false;
            }
        }

        // check that yA >= c
        for (int j = 0; j < N; j++) {
            float sum = (float) 0.0;
            for (int i = 0; i < M; i++) {
                sum += A[i][j] * y[i];
            }
            if (sum < c[j] - EPSILON) {
                System.out.println("not dual feasible");
                System.out.println("c[" + j + "] = " + c[j] + ", sum = " + sum);
                return false;
            }
        }
        return true;
    }

    // check that optimal value = cx = yb
    private boolean isOptimal(float[] b, float[] c) {
        float[] x = primal();
        float[] y = dual();
        float value = value();

        // check that value = cx = yb
        float value1 = (float) 0.0;
        for (int j = 0; j < x.length; j++)
            value1 += c[j] * x[j];
        float value2 = (float) 0.0;
        for (int i = 0; i < y.length; i++)
            value2 += y[i] * b[i];
        if (Math.abs(value - value1) > EPSILON || Math.abs(value - value2) > EPSILON) {
            System.out.println("value = " + value + ", cx = " + value1 + ", yb = " + value2);
            return false;
        }

        return true;
    }

    private boolean check(float[][]A, float[] b, float[] c) {
        return isPrimalFeasible(A, b) && isDualFeasible(A, c) && isOptimal(b, c);
    }

    // print tableaux
    public void show() {
        System.out.println("M = " + M);
        System.out.println("N = " + N);
        for (int i = 0; i <= M; i++) {
            for (int j = 0; j <= M + N; j++) {
                System.out.printf("%7.2f ", a[i][j]);
            }
            System.out.println();
        }
        System.out.println("value = " + value());
        for (int i = 0; i < M; i++)
            if (basis[i] < N) System.out.println("x_" + basis[i] + " = " + a[i][M+N]);
        System.out.println();
    }


    public static void test(float[][] A, float[] b, float[] c) {
        Simplex lp = new Simplex(A, b, c);
        System.out.println("value = " + lp.value());
        float[] x = lp.primal();
        for (int i = 0; i < x.length; i++)
            System.out.println("x[" + i + "] = " + x[i]);
        float[] y = lp.dual();
        for (int j = 0; j < y.length; j++)
            System.out.println("y[" + j + "] = " + y[j]);
    }

}