/** Points of a plane, to which the +x axis is normal
    sy, sz: number of samples in y and z directions.
    dy, dz: size of samples in y and z directions, in units.
    x_offset: location of troughs, in units.
 */
function x_grid_points(sy, sz, dy, dz, x_offset) =
     [for(z=[0:1:sz], y=[0:1:sy])
               [x_offset, y*dy, z*dz]];

/** Points of a wobbly plane, to which the +x axis is normal.
    sy, sz: number of samples in y and z directions.
    dy, dz: size of samples in y and z directions, in units.
    fy, fz: frequency of sine functions in y and z directions -
            given in units, not in samples.
    py, pz: phase in y and z directions - units not samples.
    x_offset: location of midpoint of waves, in units.
    amplitude: depth from peak to trough, in units.
*/
function x_eggcrate_points(sy, sz, dy, dz, fy, fz, py, pz,
                            x_offset, amplitude) =
     [for(z=[0:1:sz], y=[0:1:sy])
               [x_offset+amplitude/4*(0+ /* 2 if you want troughs at 0 */
                                    sin((y*dy+py)*fy*360)+
                                    sin((z*dz+pz)*fz*360)),
                y*dy,
                z*dz]];

/** Point indices into a grid/eggcrate points vector
    which will result in a series of triangles facing +x.
    sy, sz: number of samples in y and z directions.
*/
function x_grid_faces(sy, sz) =
     [for(z=[0:1:sz-1], y=[0:1:sy-1])
               concat(
                    [y+1+(z+0)*(sy+1),
                     y+0+(z+0)*(sy+1),
                     y+0+(z+1)*(sy+1)],
                    [y+1+(z+0)*(sy+1),
                     y+0+(z+1)*(sy+1),
                     y+1+(z+1)*(sy+1)])];

/** Point indices into a grid/eggcrate points vector,
    which will result in a series of triangles facing -x.
    sy, sz: number of samples in y and z directions.
*/
function minusx_grid_faces(sy, sz) =
     [for(z=[0:1:sz-1], y=[0:1:sy-1])
               concat(
                    [y+0+(z+1)*(sy+1),
                     y+0+(z+0)*(sy+1),
                     y+1+(z+0)*(sy+1)],
                    [y+1+(z+1)*(sy+1),
                     y+0+(z+1)*(sy+1),
                     y+1+(z+0)*(sy+1)])];

/** Point indices into a pair of grid points vectors concatenated,
    which will result in a series of triangles connecting the two grids.
    Facing -z, +y, +z, -y, respectively.
    sy, sz: number of samples in y and z directions.
*/
function x_box_edge_faces(sy, sz) =
     concat(
          /* the bottom (-z side) */
          [for(y=[0:1:sy-1], z=[0:1:0])
                    concat(
                         [y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                          y+0+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                          y+1+(z+0)*(sy+1)+0*(sy+1)*(sz+1)],
                         [y+1+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                          y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                          y+1+(z+0)*(sy+1)+0*(sy+1)*(sz+1)])],
          /* the back (+y side) */
          [for(y=[sy:1:sy], z=[0:1:sz-1])
                    concat(
                         [y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                          y+0+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                          y+0+(z+1)*(sy+1)+0*(sy+1)*(sz+1)],
                         [y+0+(z+1)*(sy+1)+1*(sy+1)*(sz+1),
                          y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                          y+0+(z+1)*(sy+1)+0*(sy+1)*(sz+1)])],
          /* the top (+z side) */
          [for(y=[0:1:sy-1], z=[sz:1:sz])
                    concat(
                         [y+1+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                          y+0+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                          y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1)],
                         [y+1+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                          y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                          y+1+(z+0)*(sy+1)+1*(sy+1)*(sz+1)])],
          /* the front (-y side) */
          [for(y=[0:1:0], z=[0:1:sz-1])
                    concat(
                         [y+0+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                          y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                          y+0+(z+1)*(sy+1)+0*(sy+1)*(sz+1)],
                         [y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                          y+0+(z+1)*(sy+1)+1*(sy+1)*(sz+1),
                          y+0+(z+1)*(sy+1)+0*(sy+1)*(sz+1)])]);

function face_off(faces, off) =
     [for(f=faces) [for(v=f) v+off]];

module x_single_eggcrate_box(size, sample_size, frequency, amplitude,
                             phase=[0,0,0]) {
     sy = floor(size.y / sample_size.y);
     sz = floor(size.z / sample_size.z);
     plus_x_points = x_eggcrate_points(sy, sz,
                                       sample_size.y, sample_size.z,
                                       frequency.y, frequency.z,
                                       phase.y, phase.z,
                                       0, amplitude);
     minus_x_points = x_grid_points(sy, sz,
                                    sample_size.y, sample_size.z,
                                    -size.x);
     gf = x_grid_faces(sy, sz);
     bgf = minusx_grid_faces(sy, sz);
     bef = x_box_edge_faces(sy, sz);
     polyhedron(points=concat(plus_x_points, minus_x_points),
                faces=concat(gf, bef, face_off(bgf, (sy+1)*(sz+1))));
}

module x_double_eggcrate_box(size, sample_size, frequency, amplitude,
                             phase=[0,0,0]) {
     sy = floor(size.y / sample_size.y);
     sz = floor(size.z / sample_size.z);
     plus_x_points = x_eggcrate_points(sy, sz,
                                       sample_size.y, sample_size.z,
                                       frequency.y, frequency.z,
                                       phase.y, phase.z,
                                       0, amplitude);
     minus_x_points = x_eggcrate_points(sy, sz,
                                        sample_size.y, sample_size.z,
                                        frequency.y, frequency.z,
                                        phase.y, phase.z,
                                        -size.x, amplitude);
     gf = x_grid_faces(sy, sz);
     bgf = minusx_grid_faces(sy, sz);
     bef = x_box_edge_faces(sy, sz);
     polyhedron(points=concat(plus_x_points, minus_x_points),
                faces=concat(gf, bef, face_off(bgf, (sy+1)*(sz+1))));
}

x_double_eggcrate_box(size=[100, 180, 200],
                    sample_size=[2, 2, 2],
                    frequency=[1/3,1/40,1/40],
                    amplitude=20) {}
