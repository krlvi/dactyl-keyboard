use <eggcrate.scad>;

/** Points of a plane, to which the +x axis is normal
    sy, sz: number of samples in y and z directions.
    dy, dz: size of samples in y and z directions, in units.
    x_offset: location of troughs, in units.
    
    Note (if eggcrate.scad still exists when you read this) that this
    one subtly differs from the one in eggcrate.scad: the loop indices
    are reversed, and we leave off the last y, because it is the same
    row of points as the first row in the next plane around the
    hexagonal prism.
 */
function x_grid_points_for_hex(sy, sz, dy, dz, x_offset, last) =
     [for(y=[0:1:(last!=0?sy:sy-1)], z=[0:1:sz])
               [x_offset, y*dy, z*dz]];

function ease(frac, total, x) =
     [for(dontcare=[0:0])
               let(a=floor(frac*total),
                   b=floor((1-frac)*total))
                    (x <= a) ? (x / a) :
                    (x >= b) ? 1 - ((x-b) / a) : 1][0];

/** Points of a wobbly plane, to which the +x axis is normal.
    sy, sz: number of samples in y and z directions.
    dy, dz: size of samples in y and z directions, in units.
    fy, fz: frequency of sine functions in y and z directions -
            given in units, not in samples.
    py, pz: phase in y and z directions - degrees.
    x_offset: location of midpoint of waves, in units.
    amplitude: depth from peak to trough, in units.

    As above, we loop through y first and leave off a row in some
    cases.

*/
function x_eggcrate_points_for_hex(sy, sz, dy, dz, fy, fz,
                           py, pz, ay, az, x_offset, last) =
     [for(y=[0:1:sy-1], z=[0:1:sz])
               let(ease_frac_y = min(max(0.25, 8/sy), 0.5),
                   ease_frac_z = min(max(0.25, 8/sz), 0.5),
                   y_ease = ease(ease_frac_y, sy, y),
                   z_ease = ease(ease_frac_z, sz, z))

               [x_offset+1/4*(0+ /* use 2 if you want troughs at 0 */
                              y_ease*z_ease*ay*sin((y*dy)*fy*360+py)+
                              y_ease*z_ease*az*sin((z*dz)*fz*360+pz)),
                y*dy - (sy/2 * dy),
                z*dz]];

function hex_prism_top_and_bottom_faces(sy, sz) =
     concat([for(y=[0:1:sy-1])
                      [
                       (y+1)*(sz+1)-1,
                           y*(sz+1)-1,
                       /* top center */
                       sy*(sz+1)]],
            [for(y=[0:1:sy-2])
                      [y*(sz+1),
                       (y+1)*(sz+1),
                       /* bottom center */
                       sy*(sz+1)+1]],
            [
                 /* last slice of top */
                 [1*(sz+1)-1, sy*(sz+1)-1, sy*(sz+1)],
                 /* last slice of bottom */
                 [(sy-1)*(sz+1), 0, sy*(sz+1)+1]]);

function hex_prism_grid_points(rmin, sy, sz, dy, dz) =
     concat([for(a=[0:60:360-1],
                      v=x_grid_points_for_hex(sy, sz, dy, dz, rmin, 0))
                      [[cos(a), -sin(a), 0],
                       [sin(a), cos(a), 0],
                       [0, 0, 1]] * v]);

function hex_prism_eggcrate_points(rmin, sy, sz, dy, dz, fy, fz,
                                   py, pz, ay, az) =
     concat([for(a=[0:60:360-1],
                      v=x_eggcrate_points_for_hex(
                           sy, sz, dy, dz, fy, fz,
                           py+[0,120,240,0,240,120][floor(a/60)], pz+(a>=180?180:0),
                           ay, az, rmin, a >= 300 ? 1 : 0))
                      [[cos(a), -sin(a), 0],
                       [sin(a), cos(a),  0],
                       [0,      0,       1]] * v]);

function minusx_last_strip_a(sy, sz) =
     [for(y=[0:1:sy-1])
               [
                y+0+0*(sy+1),
                    y+0+(sz-1)*(sy+1),
                y+1+0*(sy+1)
                    ]];

function minusx_last_strip_b(sy, sz) =
     [for(y=[0:1:sy-1])
               [
                y+0+(sz-1)*(sy+1),
                    y+1+(sz-1)*(sy+1),
                y+1+0*(sy+1)
                    ]];

module hex_prism(rmin, h, res, waves, amp) {
     side = rmin * (2*tan(180/6));
     s = [0, floor(side / res.y), floor(h / res.z)];
     f = [0, waves.y/side, waves.z/h];
     p = [0, 0, 0];
     points = concat(
          hex_prism_eggcrate_points(rmin, s.y, s.z, res.y, res.z,
                                    f.y, f.z, p.y, p.z, amp.y, amp.z),
          [[0, 0, (floor(h/res.z)*res.z)],
           [0, 0, 0]]);
     faces = concat(
          minusx_grid_faces_a(s.z, s.y*6-1),
          minusx_grid_faces_b(s.z, s.y*6-1),
          minusx_last_strip_a(s.z, s.y*6),
          minusx_last_strip_b(s.z, s.y*6),
          hex_prism_top_and_bottom_faces(s.y*6, s.z)
          );
     polyhedron(points=points, faces=faces, convexity=20);
}

module clamp_surfaces(rmin, h, res, waves, amp, thickness=3) {
     side = rmin * (2*tan(180/6));
     but_not_the_whole_side = 0.6;
     for(i=[0:60:360-1])
          rotate(a=i, v=[0,0,1])
               translate([rmin - thickness/2 - (amp.y+amp.z)/2, 0, h/2])
               cube([thickness, side * but_not_the_whole_side, h], center=true);
}

module _of_grid(bounds, which, rmin, gap, res, waves, amp) {
     rmaj = rmin / cos(180/6);
     side = rmin * (2*tan(180/6));
     row_y = 3/2 * side + gap * sin(60);
     /* rmin: make sure to cover entire width */
     columns = ceil((bounds.x + rmin) / (2*rmin));
     column = which % columns;
     row = floor(which / columns);
     x_offset = row % 2 != 0 ? rmin + gap/2 : 0;
     translate([column * (rmin * 2 + gap) + x_offset,
                row * row_y, 0])
          children();
}


module hex_prism_of_grid(bounds, which, rmin, gap, res, waves, amp) {
     _of_grid(bounds, which, rmin, gap, res, waves, amp)
          hex_prism(rmin, bounds.z, res, waves, amp);
}

module clamp_surfaces_of_grid(bounds, which, rmin, gap, res, waves, amp) {
     _of_grid(bounds, which, rmin, gap, res, waves, amp)
          clamp_surfaces(rmin, bounds.z, res, waves, amp);
}

hex_prism(20, 50, [3,3,3], [1, 3, 5], [0, 4, 4]);
color([1,0,0]) clamp_surfaces(20, 50, [3,3,3], [1,3,5], [0,4,4], 2);
