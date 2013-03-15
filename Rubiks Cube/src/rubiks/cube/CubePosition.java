/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rubiks.cube;

/**
 *
 * @author leif
 */
public class CubePosition {
   
    // the cubelets
     RCCubelet[][][] cubelet = new RCCubelet[3][3][3];
    public CubePosition clone(){
        CubePosition result=new CubePosition();
        for (int x=0; x<3; x++){
            for (int y=0; y<3; y++){
                for (int z=0; z<3; z++){
                    result.cubelet[x][y][z]=new RCCubelet(cubelet[x][y][z]);
                }
            }
        }
        return result;
    }
    public boolean equals(Object obj){
        if (obj instanceof CubePosition){
            CubePosition cb=(CubePosition)obj;
            for (int x=0; x<3; x++){
                for (int y=0; y<3; y++){
                    for (int z=0; z<3; z++){
                        if (!cubelet[x][y][z].equals(cb.cubelet[x][y][z])){
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
    protected static final int[][][][] moves = {
            // AXIS_X
            {
                    { { 0, 0, 0}, { 0, 0, 2}, { 0, 2, 2}, { 0, 2, 0},
                            { 0, 0, 1}, { 0, 1, 2}, { 0, 2, 1}, { 0, 1, 0}},
                    { { 1, 0, 0}, { 1, 0, 2}, { 1, 2, 2}, { 1, 2, 0},
                            { 1, 0, 1}, { 1, 1, 2}, { 1, 2, 1}, { 1, 1, 0}},
                    { { 2, 0, 0}, { 2, 0, 2}, { 2, 2, 2}, { 2, 2, 0},
                            { 2, 0, 1}, { 2, 1, 2}, { 2, 2, 1}, { 2, 1, 0}}},

            // AXIS_Y
            {
                    { { 0, 0, 0}, { 2, 0, 0}, { 2, 0, 2}, { 0, 0, 2},
                            { 1, 0, 0}, { 2, 0, 1}, { 1, 0, 2}, { 0, 0, 1}},
                    { { 0, 1, 0}, { 2, 1, 0}, { 2, 1, 2}, { 0, 1, 2},
                            { 1, 1, 0}, { 2, 1, 1}, { 1, 1, 2}, { 0, 1, 1}},
                    { { 0, 2, 0}, { 2, 2, 0}, { 2, 2, 2}, { 0, 2, 2},
                            { 1, 2, 0}, { 2, 2, 1}, { 1, 2, 2}, { 0, 2, 1}}},

            // AXIS_Z
            {
                    { { 0, 0, 0}, { 0, 2, 0}, { 2, 2, 0}, { 2, 0, 0},
                            { 0, 1, 0}, { 1, 2, 0}, { 2, 1, 0}, { 1, 0, 0}},
                    { { 0, 0, 1}, { 0, 2, 1}, { 2, 2, 1}, { 2, 0, 1},
                            { 0, 1, 1}, { 1, 2, 1}, { 2, 1, 1}, { 1, 0, 1}},
                    { { 0, 0, 2}, { 0, 2, 2}, { 2, 2, 2}, { 2, 0, 2},
                            { 0, 1, 2}, { 1, 2, 2}, { 2, 1, 2}, { 1, 0, 2}}}};

    //=========================================================================
    // Constructor, creates the cubelets. Since all the cubelets are equal,
    // the cube is initially in its "solved" state.
    //=========================================================================
    public CubePosition() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    cubelet[x][y][z] = new RCCubelet();
                }
            }
        }
    }

    //=========================================================================
    // Can be used to query the color of one face of one cubelet.
    // xPos, yPos, zPos: the coordinates of the cubelet
    // face: the face of that cubelet
    // return value: the color
    //=========================================================================
    public int getColor(int xPos, int yPos, int zPos, int face) {
        return cubelet[xPos][yPos][zPos].getColor(face);
    }

    //=========================================================================
    // Rotates one slice of the cube or the whole cube.
    // axis: the axis around which to rotate
    // direction: the direction in which to rotate
    // slice: the depth of the slice to rotate
    // wholeCube: true -> the whole cube is to be rotated. ignore "slice"
    //=========================================================================
    public void rotate(int axis, int direction, int slice, boolean wholeCube) {
        RCCubelet buffer; // saves one cubelet in circular swaps
        int[][] m = moves[axis][slice]; // the move
        RCCubelet[][][] c = cubelet; // shorter name, faster

        // rotate the whole cube? then rotate each slice, then quit
        if (wholeCube) {
            for (int i = 0; i < 3; i++) {
                rotate(axis, direction, i, false);
            }
            return;
        }

        if (direction == 1) {
            // move corner cubelets
            buffer = cubelet[m[0][0]][m[0][1]][m[0][2]];
            c[m[0][0]][m[0][1]][m[0][2]] = c[m[1][0]][m[1][1]][m[1][2]];
            c[m[1][0]][m[1][1]][m[1][2]] = c[m[2][0]][m[2][1]][m[2][2]];
            c[m[2][0]][m[2][1]][m[2][2]] = c[m[3][0]][m[3][1]][m[3][2]];
            c[m[3][0]][m[3][1]][m[3][2]] = buffer;

            // move edge cubelets
            buffer = c[m[4][0]][m[4][1]][m[4][2]];
            c[m[4][0]][m[4][1]][m[4][2]] = c[m[5][0]][m[5][1]][m[5][2]];
            c[m[5][0]][m[5][1]][m[5][2]] = c[m[6][0]][m[6][1]][m[6][2]];
            c[m[6][0]][m[6][1]][m[6][2]] = c[m[7][0]][m[7][1]][m[7][2]];
            c[m[7][0]][m[7][1]][m[7][2]] = buffer;

        } else {
            // move corner cubeletss
            buffer = c[m[3][0]][m[3][1]][m[3][2]];
            c[m[3][0]][m[3][1]][m[3][2]] = c[m[2][0]][m[2][1]][m[2][2]];
            c[m[2][0]][m[2][1]][m[2][2]] = c[m[1][0]][m[1][1]][m[1][2]];
            c[m[1][0]][m[1][1]][m[1][2]] = c[m[0][0]][m[0][1]][m[0][2]];
            c[m[0][0]][m[0][1]][m[0][2]] = buffer;

            // move edge cubelets
            buffer = c[m[7][0]][m[7][1]][m[7][2]];
            c[m[7][0]][m[7][1]][m[7][2]] = c[m[6][0]][m[6][1]][m[6][2]];
            c[m[6][0]][m[6][1]][m[6][2]] = c[m[5][0]][m[5][1]][m[5][2]];
            c[m[5][0]][m[5][1]][m[5][2]] = c[m[4][0]][m[4][1]][m[4][2]];
            c[m[4][0]][m[4][1]][m[4][2]] = buffer;
        }

        // rotate the cubelets that have been moved
        for (int i = 0; i < 8; i++) {
            c[m[i][0]][m[i][1]][m[i][2]].rotate(axis, direction);
        }

        // P.S.: I know, the comments here are a little sparse. I guess
        //       you'll just have to trust me. :-)
    }

    //=========================================================================
    // Brings the cube to disorder. This is done by performing a large number
    // of random moves, because not all of the cube's possible permutations
    // can be solved.
    // moves: how many moves
    //=========================================================================
    public void scramble(int moves) {
        for (int i = 0; i < moves; i++) {
            int axis = (int) (Math.random() * 3);
            int direction = (Math.random() < 0.5) ? 1 : -1;
            int slice = (int) (Math.random() * 3);

            rotate(axis, direction, slice, false);
        }
    }

    //=========================================================================
    // Can be used to query whether the cube is in its "solved" state. This is
    // done by comparing each of the cubelets' faces that form a face of the
    // cube with exactly one of them. There must be no differences.
    // return value: true if solved
    //=========================================================================
    public boolean isSolved() {
        // check faces FACE_XV and FACE_XH
        for (int y = 0; y < 3; y++) {
            for (int z = 0; z < 3; z++) {
                if (getColor(2, y, z, RCCubelet.FACE_XV) != getColor(2, 0, 0,
                        RCCubelet.FACE_XV)
                        || getColor(0, y, z, RCCubelet.FACE_XH) != getColor(0,
                                0, 0, RCCubelet.FACE_XH)) { return false; }
            }
        }

        // check faces FACE_YV and FACE_YH
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                if (getColor(x, 2, z, RCCubelet.FACE_YV) != getColor(0, 2, 0,
                        RCCubelet.FACE_YV)
                        || getColor(x, 0, z, RCCubelet.FACE_YH) != getColor(0,
                                0, 0, RCCubelet.FACE_YH)) { return false; }
            }
        }

        // check faces FACE_YV and FACE_YH
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (getColor(x, y, 2, RCCubelet.FACE_ZV) != getColor(0, 0, 2,
                        RCCubelet.FACE_ZV)
                        || getColor(x, y, 0, RCCubelet.FACE_ZH) != getColor(0,
                                0, 0, RCCubelet.FACE_ZH)) { return false; }
            }
        }

        return true;
    }

}
