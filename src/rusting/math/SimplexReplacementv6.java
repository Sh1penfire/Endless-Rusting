//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rusting.math;

import java.util.Random;

public class SimplexReplacementv6 {
    static final int[][] grad3 = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};
    static final int[][] grad4 = new int[][]{{0, 1, 1, 1}, {0, 1, 1, -1}, {0, 1, -1, 1}, {0, 1, -1, -1}, {0, -1, 1, 1}, {0, -1, 1, -1}, {0, -1, -1, 1}, {0, -1, -1, -1}, {1, 0, 1, 1}, {1, 0, 1, -1}, {1, 0, -1, 1}, {1, 0, -1, -1}, {-1, 0, 1, 1}, {-1, 0, 1, -1}, {-1, 0, -1, 1}, {-1, 0, -1, -1}, {1, 1, 0, 1}, {1, 1, 0, -1}, {1, -1, 0, 1}, {1, -1, 0, -1}, {-1, 1, 0, 1}, {-1, 1, 0, -1}, {-1, -1, 0, 1}, {-1, -1, 0, -1}, {1, 1, 1, 0}, {1, 1, -1, 0}, {1, -1, 1, 0}, {1, -1, -1, 0}, {-1, 1, 1, 0}, {-1, 1, -1, 0}, {-1, -1, 1, 0}, {-1, -1, -1, 0}};
    static int[][] simplex = new int[][]{{0, 1, 2, 3}, {0, 1, 3, 2}, {0, 0, 0, 0}, {0, 2, 3, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {1, 2, 3, 0}, {0, 2, 1, 3}, {0, 0, 0, 0}, {0, 3, 1, 2}, {0, 3, 2, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {1, 3, 2, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {1, 2, 0, 3}, {0, 0, 0, 0}, {1, 3, 0, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {2, 3, 0, 1}, {2, 3, 1, 0}, {1, 0, 2, 3}, {1, 0, 3, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {2, 0, 3, 1}, {0, 0, 0, 0}, {2, 1, 3, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {2, 0, 1, 3}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0, 1, 2}, {3, 0, 2, 1}, {0, 0, 0, 0}, {3, 1, 2, 0}, {2, 1, 0, 3}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 1, 0, 2}, {0, 0, 0, 0}, {3, 2, 0, 1}, {3, 2, 1, 0}};
    final int[] perm = new int[]{151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180, 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};

    public SimplexReplacementv6() {
    }

    public SimplexReplacementv6(long seed) {
        this.setSeed(seed);
    }

    public void setSeed(long seed) {
        Random random = new Random(seed);

        for(int i = 0; i < this.perm.length; ++i) {
            this.perm[i] = random.nextInt(256);
        }

    }

    public double octaveNoise2D(double octaves, double persistence, double scale, double x, double y) {
        double total = 0.0D;
        double frequency = scale;
        double amplitude = 1.0D;
        double maxAmplitude = 0.0D;

        for(int i = 0; (double)i < octaves; ++i) {
            total += (this.rawNoise2D(x * frequency, y * frequency) + 1.0D) / 2.0D * amplitude;
            frequency *= 2.0D;
            maxAmplitude += amplitude;
            amplitude *= persistence;
        }

        return total / maxAmplitude;
    }

    public double octaveNoise3D(double octaves, double persistence, double scale, double x, double y, double z) {
        double total = 0.0D;
        double frequency = scale;
        double amplitude = 1.0D;
        double maxAmplitude = 0.0D;

        for(int i = 0; (double)i < octaves; ++i) {
            total += (this.rawNoise3D(x * frequency, y * frequency, z * frequency) + 1.0D) / 2.0D * amplitude;
            frequency *= 2.0D;
            maxAmplitude += amplitude;
            amplitude *= persistence;
        }

        return total / maxAmplitude;
    }

    public double octaveNoise4D(double octaves, double persistence, double scale, double x, double y, double z, double w) {
        double total = 0.0D;
        double frequency = scale;
        double amplitude = 1.0D;
        double maxAmplitude = 0.0D;

        for(int i = 0; (double)i < octaves; ++i) {
            total += this.rawNoise4D(x * frequency, y * frequency, z * frequency, w * frequency) * amplitude;
            frequency *= 2.0D;
            maxAmplitude += amplitude;
            amplitude *= persistence;
        }

        return total / maxAmplitude;
    }

    public double scaledOctaveNoise2d(double octaves, double persistence, double scale, double loBound, double hiBound, double x, double y) {
        return this.octaveNoise2D(octaves, persistence, scale, x, y) * (hiBound - loBound) / 2.0D + (hiBound + loBound) / 2.0D;
    }

    public double scaledOctaveNoise3D(double octaves, double persistence, double scale, double loBound, double hiBound, double x, double y, double z) {
        return this.octaveNoise3D(octaves, persistence, scale, x, y, z) * (hiBound - loBound) / 2.0D + (hiBound + loBound) / 2.0D;
    }

    public double scaledOctaveNoise4D(double octaves, double persistence, double scale, double loBound, double hiBound, double x, double y, double z, double w) {
        return this.octaveNoise4D(octaves, persistence, scale, x, y, z, w) * (hiBound - loBound) / 2.0D + (hiBound + loBound) / 2.0D;
    }

    public double scaledRawNoise2D(double loBound, double hiBound, double x, double y) {
        return this.rawNoise2D(x, y) * (hiBound - loBound) / 2.0D + (hiBound + loBound) / 2.0D;
    }

    public double scaledRawNoise3D(double loBound, double hiBound, double x, double y, double z) {
        return this.rawNoise3D(x, y, z) * (hiBound - loBound) / 2.0D + (hiBound + loBound) / 2.0D;
    }

    public double scaledRawNoise4D(double loBound, double hiBound, double x, double y, double z, double w) {
        return this.rawNoise4D(x, y, z, w) * (hiBound - loBound) / 2.0D + (hiBound + loBound) / 2.0D;
    }

    public double rawNoise2D(double x, double y) {
        double F2 = 0.5D * (Math.sqrt(3.0D) - 1.0D);
        double s = (x + y) * F2;
        int i = this.fastfloor(x + s);
        int j = this.fastfloor(y + s);
        double G2 = (3.0D - Math.sqrt(3.0D)) / 6.0D;
        double t = (double)(i + j) * G2;
        double X0 = (double)i - t;
        double Y0 = (double)j - t;
        double x0 = x - X0;
        double y0 = y - Y0;
        byte i1;
        byte j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }

        double x1 = x0 - (double)i1 + G2;
        double y1 = y0 - (double)j1 + G2;
        double x2 = x0 - 1.0D + 2.0D * G2;
        double y2 = y0 - 1.0D + 2.0D * G2;
        int ii = i & 255;
        int jj = j & 255;
        int gi0 = this.perm[ii + this.perm[jj]] % 12;
        int gi1 = this.perm[ii + i1 + this.perm[jj + j1]] % 12;
        int gi2 = this.perm[ii + 1 + this.perm[jj + 1]] % 12;
        double t0 = 0.5D - x0 * x0 - y0 * y0;
        double n0;
        if (t0 < 0.0D) {
            n0 = 0.0D;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * this.dot(grad3[gi0], x0, y0);
        }

        double t1 = 0.5D - x1 * x1 - y1 * y1;
        double n1;
        if (t1 < 0.0D) {
            n1 = 0.0D;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * this.dot(grad3[gi1], x1, y1);
        }

        double t2 = 0.5D - x2 * x2 - y2 * y2;
        double n2;
        if (t2 < 0.0D) {
            n2 = 0.0D;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * this.dot(grad3[gi2], x2, y2);
        }

        return 70.0D * (n0 + n1 + n2);
    }

    public double rawNoise3D(double x, double y, double z) {
        double F3 = 0.3333333333333333D;
        double s = (x + y + z) * F3;
        int i = this.fastfloor(x + s);
        int j = this.fastfloor(y + s);
        int k = this.fastfloor(z + s);
        double G3 = 0.16666666666666666D;
        double t = (double)(i + j + k) * G3;
        double X0 = (double)i - t;
        double Y0 = (double)j - t;
        double Z0 = (double)k - t;
        double x0 = x - X0;
        double y0 = y - Y0;
        double z0 = z - Z0;
        byte i1;
        byte j1;
        byte k1;
        byte i2;
        byte j2;
        byte k2;
        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } else {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else if (y0 < z0) {
            i1 = 0;
            j1 = 0;
            k1 = 1;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else if (x0 < z0) {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 1;
            j2 = 1;
            k2 = 0;
        }

        double x1 = x0 - (double)i1 + G3;
        double y1 = y0 - (double)j1 + G3;
        double z1 = z0 - (double)k1 + G3;
        double x2 = x0 - (double)i2 + 2.0D * G3;
        double y2 = y0 - (double)j2 + 2.0D * G3;
        double z2 = z0 - (double)k2 + 2.0D * G3;
        double x3 = x0 - 1.0D + 3.0D * G3;
        double y3 = y0 - 1.0D + 3.0D * G3;
        double z3 = z0 - 1.0D + 3.0D * G3;
        int ii = i & 255;
        int jj = j & 255;
        int kk = k & 255;
        int gi0 = this.perm[ii + this.perm[jj + this.perm[kk]]] % 12;
        int gi1 = this.perm[ii + i1 + this.perm[jj + j1 + this.perm[kk + k1]]] % 12;
        int gi2 = this.perm[ii + i2 + this.perm[jj + j2 + this.perm[kk + k2]]] % 12;
        int gi3 = this.perm[ii + 1 + this.perm[jj + 1 + this.perm[kk + 1]]] % 12;
        double t0 = 0.6D - x0 * x0 - y0 * y0 - z0 * z0;
        double n0;
        if (t0 < 0.0D) {
            n0 = 0.0D;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * this.dot(grad3[gi0], x0, y0, z0);
        }

        double t1 = 0.6D - x1 * x1 - y1 * y1 - z1 * z1;
        double n1;
        if (t1 < 0.0D) {
            n1 = 0.0D;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * this.dot(grad3[gi1], x1, y1, z1);
        }

        double t2 = 0.6D - x2 * x2 - y2 * y2 - z2 * z2;
        double n2;
        if (t2 < 0.0D) {
            n2 = 0.0D;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * this.dot(grad3[gi2], x2, y2, z2);
        }

        double t3 = 0.6D - x3 * x3 - y3 * y3 - z3 * z3;
        double n3;
        if (t3 < 0.0D) {
            n3 = 0.0D;
        } else {
            t3 *= t3;
            n3 = t3 * t3 * this.dot(grad3[gi3], x3, y3, z3);
        }

        return 32.0D * (n0 + n1 + n2 + n3);
    }

    public double rawNoise4D(double x, double y, double z, double w) {
        double F4 = (Math.sqrt(5.0D) - 1.0D) / 4.0D;
        double G4 = (5.0D - Math.sqrt(5.0D)) / 20.0D;
        double s = (x + y + z + w) * F4;
        int i = this.fastfloor(x + s);
        int j = this.fastfloor(y + s);
        int k = this.fastfloor(z + s);
        int l = this.fastfloor(w + s);
        double t = (double)(i + j + k + l) * G4;
        double X0 = (double)i - t;
        double Y0 = (double)j - t;
        double Z0 = (double)k - t;
        double W0 = (double)l - t;
        double x0 = x - X0;
        double y0 = y - Y0;
        double z0 = z - Z0;
        double w0 = w - W0;
        int c1 = x0 > y0 ? 32 : 0;
        int c2 = x0 > z0 ? 16 : 0;
        int c3 = y0 > z0 ? 8 : 0;
        int c4 = x0 > w0 ? 4 : 0;
        int c5 = y0 > w0 ? 2 : 0;
        int c6 = z0 > w0 ? 1 : 0;
        int c = c1 + c2 + c3 + c4 + c5 + c6;
        int i1 = simplex[c][0] >= 3 ? 1 : 0;
        int j1 = simplex[c][1] >= 3 ? 1 : 0;
        int k1 = simplex[c][2] >= 3 ? 1 : 0;
        int l1 = simplex[c][3] >= 3 ? 1 : 0;
        int i2 = simplex[c][0] >= 2 ? 1 : 0;
        int j2 = simplex[c][1] >= 2 ? 1 : 0;
        int k2 = simplex[c][2] >= 2 ? 1 : 0;
        int l2 = simplex[c][3] >= 2 ? 1 : 0;
        int i3 = simplex[c][0] >= 1 ? 1 : 0;
        int j3 = simplex[c][1] >= 1 ? 1 : 0;
        int k3 = simplex[c][2] >= 1 ? 1 : 0;
        int l3 = simplex[c][3] >= 1 ? 1 : 0;
        double x1 = x0 - (double)i1 + G4;
        double y1 = y0 - (double)j1 + G4;
        double z1 = z0 - (double)k1 + G4;
        double w1 = w0 - (double)l1 + G4;
        double x2 = x0 - (double)i2 + 2.0D * G4;
        double y2 = y0 - (double)j2 + 2.0D * G4;
        double z2 = z0 - (double)k2 + 2.0D * G4;
        double w2 = w0 - (double)l2 + 2.0D * G4;
        double x3 = x0 - (double)i3 + 3.0D * G4;
        double y3 = y0 - (double)j3 + 3.0D * G4;
        double z3 = z0 - (double)k3 + 3.0D * G4;
        double w3 = w0 - (double)l3 + 3.0D * G4;
        double x4 = x0 - 1.0D + 4.0D * G4;
        double y4 = y0 - 1.0D + 4.0D * G4;
        double z4 = z0 - 1.0D + 4.0D * G4;
        double w4 = w0 - 1.0D + 4.0D * G4;
        int ii = i & 255;
        int jj = j & 255;
        int kk = k & 255;
        int ll = l & 255;
        int gi0 = this.perm[ii + this.perm[jj + this.perm[kk + this.perm[ll]]]] % 32;
        int gi1 = this.perm[ii + i1 + this.perm[jj + j1 + this.perm[kk + k1 + this.perm[ll + l1]]]] % 32;
        int gi2 = this.perm[ii + i2 + this.perm[jj + j2 + this.perm[kk + k2 + this.perm[ll + l2]]]] % 32;
        int gi3 = this.perm[ii + i3 + this.perm[jj + j3 + this.perm[kk + k3 + this.perm[ll + l3]]]] % 32;
        int gi4 = this.perm[ii + 1 + this.perm[jj + 1 + this.perm[kk + 1 + this.perm[ll + 1]]]] % 32;
        double t0 = 0.6D - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
        double n0;
        if (t0 < 0.0D) {
            n0 = 0.0D;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * this.dot(grad4[gi0], x0, y0, z0, w0);
        }

        double t1 = 0.6D - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
        double n1;
        if (t1 < 0.0D) {
            n1 = 0.0D;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * this.dot(grad4[gi1], x1, y1, z1, w1);
        }

        double t2 = 0.6D - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
        double n2;
        if (t2 < 0.0D) {
            n2 = 0.0D;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * this.dot(grad4[gi2], x2, y2, z2, w2);
        }

        double t3 = 0.6D - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
        double n3;
        if (t3 < 0.0D) {
            n3 = 0.0D;
        } else {
            t3 *= t3;
            n3 = t3 * t3 * this.dot(grad4[gi3], x3, y3, z3, w3);
        }

        double t4 = 0.6D - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
        double n4;
        if (t4 < 0.0D) {
            n4 = 0.0D;
        } else {
            t4 *= t4;
            n4 = t4 * t4 * this.dot(grad4[gi4], x4, y4, z4, w4);
        }

        return 27.0D * (n0 + n1 + n2 + n3 + n4);
    }

    int fastfloor(double x) {
        return x > 0.0D ? (int)x : (int)x - 1;
    }

    double dot(int[] g, double x, double y) {
        return (double)g[0] * x + (double)g[1] * y;
    }

    double dot(int[] g, double x, double y, double z) {
        return (double)g[0] * x + (double)g[1] * y + (double)g[2] * z;
    }

    double dot(int[] g, double x, double y, double z, double w) {
        return (double)g[0] * x + (double)g[1] * y + (double)g[2] * z + (double)g[3] * w;
    }
}
