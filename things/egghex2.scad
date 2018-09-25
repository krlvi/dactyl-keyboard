/** Points of a plane, to which the +x axis is normal
    sy, sz: number of samples in y and z directions.
    dy, dz: size of samples in y and z directions, in units.
    x_offset: location of troughs, in units.
    
    Note (if eggcrate.scad still exists when you read this) that this
    one subtly differs from the one in eggcrate.scad: the loop indices
    are reversed, we center our y looping around y=0, and we leave off
    the last y, because it is the same row of points as the first row
    in the next plane around the hexagonal prism.
 */
function x_grid_points(sy, sz, dy, dz, x_offset) =
     [for(y=[floor(-sy/2):1:ceil(sy/2)-1], z=[0:1:sz])
               [x_offset, y*dy, z*dz]];

function ease(frac, total, x) =
     [for(dontcare=[0:1])
               let(a=floor(frac*total),
                   b=floor((1-frac)*total))
                    (x <= a) ? (x / a) :
                    (x >= b) ? 1 - ((x-b) / a) : 1][0];

for(x=[0:20]) echo(ease(0.3, 20, x));

/** Points of a wobbly plane, to which the +x axis is normal.
    sy, sz: number of samples in y and z directions.
    dy, dz: size of samples in y and z directions, in units.
    fy, fz: frequency of sine functions in y and z directions -
            given in units, not in samples.
    py, pz: phase in y and z directions - degrees.
    x_offset: location of midpoint of waves, in units.
    amplitude: depth from peak to trough, in units.
*/
function x_eggcrate_points(sy, sz, dy, dz, fy, fz, py, pz, ay, az,
                           x_offset) =
     [for(y=[0:1:sy-1], z=[0:1:sz])
               let(ease_frac = 0.2,
                   y_ease = ease(ease_frac, sy, y),
                   z_ease = ease(ease_frac, sz, z))

               [x_offset+1/4*(0+ /* use 2 if you want troughs at 0 */
                              y_ease*z_ease*ay*sin((y*dy)*fy*360+py)+
                              y_ease*z_ease*az*sin((z*dz)*fz*360+pz)),
                y*dy - sy/4,
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

/* This simplification to four triangle faces for the top and bottom
 * only works if the y-amplitude of the eggcrate is zero at the top
 * and bottom, and the z component of the eggcrate is zero. */
function hex_prism_end_faces_a(sy, sz) =
     [
          /* bottom */
          [0+0*sy*(sz+1),
           0+1*sy*(sz+1),
           0+2*sy*(sz+1)],
          [0+0*sy*(sz+1),
           0+2*sy*(sz+1),
           0+3*sy*(sz+1)],
          [0+0*sy*(sz+1),
           0+3*sy*(sz+1),
           0+4*sy*(sz+1)],
          [0+0*sy*(sz+1),
           0+4*sy*(sz+1),
           0+5*sy*(sz+1)],
          /* top */
          [sz+0*sy*(sz+1),
           sz+2*sy*(sz+1),
           sz+1*sy*(sz+1)],
          [sz+0*sy*(sz+1),
           sz+3*sy*(sz+1),
           sz+2*sy*(sz+1)],
          [sz+0*sy*(sz+1),
           sz+4*sy*(sz+1),
           sz+3*sy*(sz+1)],
          [sz+0*sy*(sz+1),
           sz+5*sy*(sz+1),
           sz+4*sy*(sz+1)],
          ];

function hex_prism_grid_points(sy, sz, dy, dz) =
     concat([for(a=[0:60:360],
                      v=x_grid_points(sy, sz, dy, dz, sy*dy*sqrt(3)/2))
                      [[cos(a), -sin(a), 0],
                       [sin(a), cos(a), 0],
                       [0, 0, 1]] * v]);

function hex_prism_eggcrate_points(sy, sz, dy, dz, fy, fz, py, pz, ay, az) =
     concat([for(a=[0:60:360],
                      v=x_eggcrate_points(sy, sz, dy, dz, fy, fz, py+a*2, pz+(a>180?180:0), ay, az, sy*dy*sqrt(3)/2))
                      [[cos(a), -sin(a), 0],
                       [sin(a), cos(a),  0],
                       [0,      0,       1]] * v]);

module hex_prism(rmin, h, res, waves, amp) {
     side = rmin / (2*tan(180/6));
     s = [0, side / res.y, h / res.z];
     f = [0, waves.y/side, waves.z/h];
     p = [0, 0, 0];
     points = hex_prism_eggcrate_points(s.y, s.z, res.y, res.z,
          f.y, f.z, p.y, p.z, amp.y, amp.z);
     faces = concat(minusx_grid_faces_a(s.z, s.y*6),
                    minusx_grid_faces_b(s.z, s.y*6),
                    hex_prism_end_faces_a(s.y, s.z)
                    );
     polyhedron(points=points, faces=faces);
}

module three() {
     slop = 2;
     minor_radius = 23*0.5*sqrt(3);
     tx = minor_radius + slop;

     hex_prism(23, 40, 0.5, [0, 5, 1], [0, 5, 5]);

     for(a=[0:60:360]) {
          rotate(a=a, v=[0,0,1])
               translate([tx, 0, 0])
               rotate(a=-a, v=[0,0,1])
               hex_prism(23, 40, 0.5, [0, 5, 2], [0, 2, 2]);
     }
}

three();
