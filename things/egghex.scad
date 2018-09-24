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
               [x_offset+1/4*(0+ /* 2 if you want troughs at 0 */
                                    amplitude.y*sin((y*dy)*fy*360+py)+
                                    amplitude.z*sin((z*dz)*fz*360+pz)),
                y*dy,
                z*dz]];

/* maybe this *_a, *_b stuff is weak sauce, but i don't know how to
   generate two triangles at a time and end up with a list of [[a1,
   b1, c1], [a2, b2, c2], [a3, b3, c3]] and not some other crazy shape
   which would not work right. for a bit i had [[a1, b1, c1, a2, b2,
   c2], [a3, b3, c3, a4, b4, c4]] and i assume some of those were not
   planar. openscad segfaulted somewhere in cgal's nef_3 module.
   https://github.com/CGAL/cgal/issues/3309
   https://github.com/openscad/openscad/issues/2464
*/

/** Point indices into a grid/eggcrate points vector
    which will result in a series of triangles facing +x.
    The a triangles are only half of them.
    sy, sz: number of samples in y and z directions.
*/
function x_grid_faces_a(sy, sz) =
     [for(z=[0:1:sz-1], y=[0:1:sy-1])
               [y+1+(z+0)*(sy+1),
                y+0+(z+0)*(sy+1),
                y+0+(z+1)*(sy+1)]];

/** Point indices into a grid/eggcrate points vector
    which will result in a series of triangles facing +x.
    The b triangles are only half of them.
    sy, sz: number of samples in y and z directions.
*/
function x_grid_faces_b(sy, sz) =
     [for(z=[0:1:sz-1], y=[0:1:sy-1])
               [y+1+(z+0)*(sy+1),
                y+0+(z+1)*(sy+1),
                y+1+(z+1)*(sy+1)]];

/** Point indices into a grid/eggcrate points vector,
    which will result in a series of triangles facing -x.
    The a triangles are only half of them.
    sy, sz: number of samples in y and z directions.
*/
function minusx_grid_faces_a(sy, sz) =
     [for(z=[0:1:sz-1], y=[0:1:sy-1])
               [y+0+(z+1)*(sy+1),
                y+0+(z+0)*(sy+1),
                y+1+(z+0)*(sy+1)]];


/** Point indices into a grid/eggcrate points vector,
    which will result in a series of triangles facing -x.
    The b triangles are only half of them.
    sy, sz: number of samples in y and z directions.
*/
function minusx_grid_faces_b(sy, sz) =
     [for(z=[0:1:sz-1], y=[0:1:sy-1])
                    [y+1+(z+1)*(sy+1),
                     y+0+(z+1)*(sy+1),
                     y+1+(z+0)*(sy+1)]];

/** Point indices into a pair of grid points vectors concatenated,
    which will result in a series of triangles connecting the two grids.
    Facing -z, +y, +z, -y, respectively.
    The a triangles are only half of them.
    sy, sz: number of samples in y and z directions.
*/
function x_box_edge_faces_a(sy, sz) =
     concat(
          /* the bottom (-z side) */
          [for(y=[0:1:sy-1], z=[0:1:0])
                    [y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                     y+0+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                     y+1+(z+0)*(sy+1)+0*(sy+1)*(sz+1)]],
          /* the back (+y side) */
          [for(y=[sy:1:sy], z=[0:1:sz-1])
                    [y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                     y+0+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                     y+0+(z+1)*(sy+1)+0*(sy+1)*(sz+1)]],
          /* the top (+z side) */
          [for(y=[0:1:sy-1], z=[sz:1:sz])
                    [y+1+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                     y+0+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                     y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1)]],
          /* the front (-y side) */
          [for(y=[0:1:0], z=[0:1:sz-1])
                    [y+0+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                     y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                     y+0+(z+1)*(sy+1)+0*(sy+1)*(sz+1)]]);

/** Point indices into a pair of grid points vectors concatenated,
    which will result in a series of triangles connecting the two grids.
    Facing -z, +y, +z, -y, respectively.
    The b triangles are only half of them.
    sy, sz: number of samples in y and z directions.
*/
function x_box_edge_faces_b(sy, sz) =
     concat(
          /* the bottom (-z side) */
          [for(y=[0:1:sy-1], z=[0:1:0])
                    [y+1+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                     y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                     y+1+(z+0)*(sy+1)+0*(sy+1)*(sz+1)]],
          /* the back (+y side) */
          [for(y=[sy:1:sy], z=[0:1:sz-1])
                    [y+0+(z+1)*(sy+1)+1*(sy+1)*(sz+1),
                     y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                     y+0+(z+1)*(sy+1)+0*(sy+1)*(sz+1)]],
          /* the top (+z side) */
          [for(y=[0:1:sy-1], z=[sz:1:sz])
                    [y+1+(z+0)*(sy+1)+0*(sy+1)*(sz+1),
                     y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                     y+1+(z+0)*(sy+1)+1*(sy+1)*(sz+1)]],
          /* the front (-y side) */
          [for(y=[0:1:0], z=[0:1:sz-1])
                    [y+0+(z+0)*(sy+1)+1*(sy+1)*(sz+1),
                     y+0+(z+1)*(sy+1)+1*(sy+1)*(sz+1),
                     y+0+(z+1)*(sy+1)+0*(sy+1)*(sz+1)]]);







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
     gf = concat(x_grid_faces_a(sy, sz), x_grid_faces_b(sy, sz));
     bgf = concat(minusx_grid_faces_a(sy, sz),
                  minusx_grid_faces_b(sy, sz));
     bef = concat(x_box_edge_faces_a(sy, sz),
                  x_box_edge_faces_b(sy, sz));
     polyhedron(points=concat(plus_x_points, minus_x_points),
                faces=concat(gf, bef, face_off(bgf, (sy+1)*(sz+1))),
          convexity=ceil(size.y*frequency.y + size.z*frequency.z)*2 );
}

/* this one mates with the x_single_eggcrate_box.
   it is up to you to translate it to get a glue tolerance. */
module x_mating_single_eggcrate_box(size, sample_size, frequency, amplitude,
                                    phase=[0,0,0]) {
     sy = floor(size.y / sample_size.y);
     sz = floor(size.z / sample_size.z);
     plus_x_points = x_grid_points(sy, sz,
                                   sample_size.y, sample_size.z,
                                   size.x);
     minus_x_points = x_eggcrate_points(sy, sz,
                                       sample_size.y, sample_size.z,
                                       frequency.y, frequency.z,
                                       phase.y, phase.z,
                                       0, amplitude);
     gf = concat(x_grid_faces_a(sy, sz), x_grid_faces_b(sy, sz));
     bgf = concat(minusx_grid_faces_a(sy, sz),
                  minusx_grid_faces_b(sy, sz));
     bef = concat(x_box_edge_faces_a(sy, sz),
                  x_box_edge_faces_b(sy, sz));
     polyhedron(points=concat(plus_x_points, minus_x_points),
                faces=concat(gf, bef, face_off(bgf, (sy+1)*(sz+1))),
          convexity=ceil(size.y*frequency.y + size.z*frequency.z)*2 );
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
     gf = concat(x_grid_faces_a(sy, sz), x_grid_faces_b(sy, sz));
     bgf = concat(minusx_grid_faces_a(sy, sz),
                  minusx_grid_faces_b(sy, sz));
     bef = concat(x_box_edge_faces_a(sy, sz),
                  x_box_edge_faces_b(sy, sz));
     polyhedron(points=concat(plus_x_points, minus_x_points),
                faces=concat(gf, bef, face_off(bgf, (sy+1)*(sz+1))),
                convexity=(size.y*frequency.y + size.z*frequency.z)*2 );
}

module x_double_string_holes(hole_frequency,
                      size, sample_size, frequency, amplitude,
                      phase=[0,0,0]) {
     hole_diameter = 0.7;
     away_from_edge = 1.5;
     hole_fn = 6;
     for(y=[away_from_edge:1/hole_frequency:size.y-away_from_edge]) {
          translate([-(away_from_edge) +
                     amplitude/4*(-1+sin((y+phase.y)*frequency.y*360)),
                     y, size.z/2 ])
               cylinder(
                    r=(hole_diameter/2),
                    h=size.z*1.1,
                    center=true,
                    fn=hole_fn);
          translate([-(size.x-away_from_edge) +
                     amplitude/4*(+1+sin((y+phase.y)*frequency.y*360)),
                     y, size.z/2 ])
               cylinder(
                    r=(hole_diameter/2),
                    h=size.z*1.1,
                    center=true,
                    fn=hole_fn);
     }
}

module x_space_filling_eggcrate_box(which, total_width, gap, hole_frequency,
                                      each_size, sample_size,
                                      frequency, amplitude, phase=[0,0,0]) {
     x = -(total_width/2) + which * (each_size.x+gap);
     translate([x,0,0]) {
          difference() {
               x_double_eggcrate_box(each_size, sample_size,
                                     frequency, amplitude, phase);
               x_double_string_holes(hole_frequency,
                                     each_size, sample_size,
                                     frequency, amplitude, phase);
          }
     }
}

module hexagonal_eggcrate_prism(minor_radius, height, sample_size,
     frequency_multiples, amplitude) {
     major_radius = 2*minor_radius/sqrt(3);
     y = major_radius + amplitude.y;
     frequency = [1, frequency_multiples.y/(2*minor_radius/sqrt(3)),
                  frequency_multiples.z/height];
     csv_fudge = 0.01; /* to keep large planes slightly out of alignment */
     intersection_for(n=[0:1:5]) {
          z_phase = (n >= 3) ? 180 : 0;
          y_phase=[0,120,240,0,240,120][n];
          rot = [0, 60, 120, 180, 240, 300, 360][n];
          col = [ [0.7, 0.2, 0.2],
                  [0.2, 0.7, 0.2],
                  [0.2, 0.2, 0.7],
                  [0.2, 0.7, 0.7],
                  [0.7, 0.2, 0.7],
                  [0.7, 0.7, 0.2] ][n];
          color(col)
               rotate(a=rot, v=[0,0,1])
               translate([0, 0, csv_fudge * (z_phase + y_phase) / 360])
               translate([minor_radius,-y,0])
               x_single_eggcrate_box(
                    [3*minor_radius,2*y, height],
                    sample_size, frequency, amplitude,
                    [0, y_phase, z_phase]);
     }
}

module three() {
     slop = 2;
     minor_radius = 90;
     frequency_multiples = [1,3,2];
     fy = frequency_multiples.y/(2*minor_radius/sqrt(3));
     amplitude = [0, 10, 0];
     tx = 2*minor_radius + slop;
     ty = 0; /* 1/2*(1/fy); */

     /* center */
     rotate(a=0, v=[0,0,1])
          translate([0, 0, 0])
          rotate(a=-0, v=[0,0,1])
          hexagonal_eggcrate_prism(minor_radius, 30, [1,3,3], frequency_multiples, amplitude);
          
     for(a=[0:60:360]) {
          rotate(a=a, v=[0,0,1])
               translate([tx, ty, 0])
               rotate(a=-a, v=[0,0,1])
          hexagonal_eggcrate_prism(minor_radius, 30, [1,3,3], frequency_multiples, amplitude);
     }
}

three();
