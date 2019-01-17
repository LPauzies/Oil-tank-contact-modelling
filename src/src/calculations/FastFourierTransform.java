package src.calculations;

public abstract class FastFourierTransform {
    
    /** effectue la transform�e de fourier d'une s�rie COMPLEXE **/
    public final static void fft(double[] [] in,double [] [] out) {
        // assume n is a power of 2
        int n = in.length;
        int nu = (int)(Math.log(n)/Math.log(2));
        int n2 = n/2;
        int nu1 = nu - 1;
        double tr, ti, p, arg, c, s;
        for (int i = 0; i < n; i++) {
            out[i][0] = in[i][0];
            out[i][1] = in[i][1];
        }
        int k = 0;

        for (int l = 1; l <= nu; l++) {
            while (k < n) {
                for (int i = 1; i <= n2; i++) {
                    p = bitrev (k >> nu1,n,nu);
                    arg = 2 *  Math.PI * p / n;
                    c =  Math.cos (arg);
                    s =  Math.sin (arg);
                    tr = out[k+n2][0]*c + out[k+n2][1]*s;
                    ti = out[k+n2][1]*c - out[k+n2][0]*s;
                    out[k+n2][0] = out[k][0] - tr;
                    out[k+n2][1] = out[k][1] - ti;
                    out[k][0] += tr;
                    out[k][1] += ti;
                    k++;
                }
                k += n2;
            }
            k = 0;
            nu1--;
            n2 = n2/2;
        }
        k = 0;
        int r;
        while (k < n) {
            r = bitrev (k,n,nu);
            if (r > k) {
                tr = out[k][0];
                ti = out[k][1];
                out[k][0] = out[r][0];
                out[k][1] = out[r][1];
                out[r][0] = tr;
                out[r][1] = ti;
            }
            k++;
        }
    }
    
    /** effectue la transform�e de fourier d'une s�rie REELLE **/    
    public final static void fftReal(double[] in,double [] [] out) {
        // assume n is a power of 2
        int n = in.length;
        int nu = (int)(Math.log(n)/Math.log(2));
        int n2 = n/2;
        int nu1 = nu - 1;
        double tr, ti, p, arg, c, s;
        for (int i = 0; i < n; i++) {
            out[i][0] = in[i];
            out[i][1] = 0d;
        }
        int k = 0;

        for (int l = 1; l <= nu; l++) {
            while (k < n) {
                for (int i = 1; i <= n2; i++) {
                    p = bitrev (k >> nu1,n,nu);
                    arg = 2 *  Math.PI * p / n;
                    c =  Math.cos (arg);
                    s =  Math.sin (arg);
                    tr = out[k+n2][0]*c + out[k+n2][1]*s;
                    ti = out[k+n2][1]*c - out[k+n2][0]*s;
                    out[k+n2][0] = out[k][0] - tr;
                    out[k+n2][1] = out[k][1] - ti;
                    out[k][0] += tr;
                    out[k][1] += ti;
                    k++;
                }
                k += n2;
            }
            k = 0;
            nu1--;
            n2 = n2/2;
        }
        k = 0;
        int r;
        while (k < n) {
            r = bitrev (k,n,nu);
            if (r > k) {
                tr = out[k][0];
                ti = out[k][1];
                out[k][0] = out[r][0];
                out[k][1] = out[r][1];
                out[r][0] = tr;
                out[r][1] = ti;
            }
            k++;
        }
    }
    
    /** effcectue la transform�e de fourier inverse d'une s�rie COMPLEXE  **/
    
    public static final void invfft(double[][] in,double[][] out) {
        int N = in.length;
        double [] [] in2 = new double [N][2];
        for (int i = 0 ; i < N ; i++) {
            in2[i][0] = in[i][0]/N;
            in2[i][1] = -in[i][1]/N;
        }
        fft(in2,out);
    }
    
    /** effcectue la transform�e de fourier inverse d'une s�rie COMPLEXE   **/   
    /** en sachant que le r�sultat doit �tre une s�rie REELLE   **/
    public static final void invfftReal(double[][] in,double[] out) {
        int N = in.length;
        double [][] in2 = new double [N][2];
        for (int i = 0 ; i <= N/2 ; i++) { // l'�chantuillon N/2 (Nyquist) est compris !!!
            in2[i][0] = in[i][0]/N;
            in2[i][1] = -in[i][1]/N;
        }
        for (int i = N/2+1 ; i < N ; i++) {
            in2[i][0] = 0d;
            in2[i][1] = 0d;
        }
        /** REMARQUE IMPORTANTE : la contribution du premier �chantillon et de l'�chantilon 
         * Nyquist doit �tre divis� par 2 !!!!!!
         * */
        in2[0][0] /= 2d;
        in2[0][1] /= 2d;
        
        in2[N/2][0] /= 2d;
        in2[N/2][1] /= 2d;
        
        double [][] out2 = new double[N][2];
        fft(in2,out2);
        for(int i = 0 ; i < N ; i++) {
            out[i] = 2d*out2[i][0]; // deux fois partie r�elle
        }
    }

    private final static int bitrev(int j,int n,int nu) {

        int j2;
        int j1 = j;
        int k = 0;
        for (int i = 1; i <= nu; i++) {
            j2 = j1/2;
            k  = 2*k + j1 - 2*j2;
            j1 = j2;
        }
        return k;
    }
  
}
