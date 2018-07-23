/*
--- BEGIN AGPLv3-preamble ---
Dactyl Marshmallow ergonomic keyboard generator
Copyright (C) 2015, 2018 Matthew Adereth and Jared Jennings

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
--- END AGPLv3-preamble ---
*/
/*
*/
/* Split the space inside a cube into two halves, interspersed with
   each other. Little slices of thickness glue_tolerance are neither
   in half A nor half B.

   Say we have a big flat piece, a cube 300x150x5. We can only print
   180x180x180. We take the intersection of the cube with
   vertical_prisms_a with spacing, say, 80. For each of a series of
   tall, narrow, deep cubes (80x180x180) we take the intersection and
   write it to a separate file. Now we do the same with
   vertical_prisms_b. Now we've got four or five pieces written to
   separate files, that we can print separately and glue together.
   */

module vertical_prism(width, depth, height) {
     /* A hexagonal prism, imagined as a rectangular prism with two halves
      * of hexagonal prisms on the sides. width >= height. */
     epsilon = height * 0.0001;
     minor_radius = depth / 2;
     major_radius = 2 * minor_radius / sqrt(3);
     rect_width = width - 2 * major_radius;
     linear_extrude(height=height, center=true) {
          polygon(points=[
                       [  rect_width/2 + major_radius, 0],
                       [  rect_width/2 + major_radius/2 , -(minor_radius+epsilon)],
                       [-(rect_width/2 + major_radius/2), -(minor_radius+epsilon)],
                       [-(rect_width/2 + major_radius), 0],
                       [-(rect_width/2 + major_radius/2), minor_radius+epsilon],
                       [  rect_width/2 + major_radius/2 , minor_radius+epsilon]]);
     }
}

module vertical_prisms_slice(x_size, y_size, z_size, spacing, glue_tolerance, bp, x_iteration) {
     /* constants */
     epsilon = z_size * 0.0001;
     shear = 0.3;
     y_spacing = spacing / 5;
     zigzag_length = spacing / 4;
     /* the following are chosen not arbitrarily, but because our
      * prism is hexagonal */
     minor_radius = y_spacing / 2;
     major_radius = 2 * minor_radius / sqrt(3);
     prism_width = spacing / 2 + major_radius / 2;
     prism_depth = y_spacing;
     x_scale_factor = (prism_width - glue_tolerance) / prism_width;
     x_offset = bp ? spacing/2 : 0;
     y_offset = bp ? minor_radius : 0;
     col = bp ? "plum" : "lightgreen";
     color(col) {
          x = (-x_size/2) + x_offset + spacing*x_iteration;
          for(y=[-y_size/2+y_offset:2*minor_radius:y_size/2]) {
               for(z=[-z_size/2:zigzag_length:z_size/2]) {
                    translate([x, y, z]) {
                         multmatrix([[x_scale_factor,0,shear,0],
                                     [0,1,0,0],
                                     [0,0,1,0],
                                     [0,0,0,1]])
                              vertical_prism(prism_width, prism_depth, zigzag_length/2+epsilon);
                    }
                    translate([x, y, z+zigzag_length/2]) {
                         multmatrix([[x_scale_factor,0,-shear,0],
                                     [0,1,0,0],
                                     [0,0,1,0],
                                     [0,0,0,1]])
                              vertical_prism(prism_width, prism_depth, zigzag_length/2+epsilon);
                    }
               }
          }
     }
}

module vertical_prisms_stitch_hole_a(x_size, y_size, z_size, spacing, glue_tolerance, bp, x_iteration) {
     shear = 0.3;
     y_spacing = spacing / 5;
     hole_factor = 1/30;
     min_hole_radius = 0.7;
     hole_radius = (((hole_factor * y_spacing) < min_hole_radius) ?
                    min_hole_radius : (hole_factor * y_spacing));
     zigzag_length = spacing / 4;
     minor_radius = y_spacing / 2;
     major_radius = 2 * minor_radius / sqrt(3);
     prism_width = spacing / 2 + major_radius / 2;
     prism_depth = y_spacing;
     x_offset = bp ? spacing/2 : 0;
     y_offset = bp ? minor_radius : 0;
     col = bp ? "lemonchiffon" : "paleturquoise";
     color(col) {
          center_x = (-x_size/2) + x_offset + spacing*x_iteration;
          x_offset = prism_width/2 - (prism_width - spacing/2) - zigzag_length/4 * shear;
          x1 = center_x - x_offset;
          x2 = center_x + x_offset;
          for(y=[-y_size/2+y_offset:2*minor_radius:y_size/2]) {
               translate([x1, y, 0]) {
                    cylinder(r=hole_radius, h=z_size*1.2, center=true, $fn=6);
               }
               translate([x2, y, 0]) {
                    cylinder(r=hole_radius, h=z_size*1.2, center=true, $fn=6);
               }
          }
     }
}

module vertical_prisms_helper(x_size, y_size, z_size, spacing, glue_tolerance, bp) {
     for(x_iteration=[0:1:(x_size/spacing)]) {
          difference() {
               vertical_prisms_slice(x_size, y_size, z_size, spacing, glue_tolerance, bp, x_iteration);
               vertical_prisms_stitch_hole_a(x_size, y_size, z_size, spacing, glue_tolerance, bp, x_iteration);
          }
     }
}

module vertical_prisms_a(x_size, y_size, z_size, spacing, glue_tolerance) {
     vertical_prisms_helper(x_size, y_size, z_size, spacing, glue_tolerance, 0);
}

module vertical_prisms_b(x_size, y_size, z_size, spacing, glue_tolerance) {
     vertical_prisms_helper(x_size, y_size, z_size, spacing, glue_tolerance, 1);
}

//vertical_prisms_a(20, 20, 20, 5, 0.1);
//vertical_prisms_b(20, 20, 20, 5, 0.1);
//vertical_prisms_slice(20, 20, 20, 5, 0.1, 0, 1);
union() {
     vertical_prisms_a(220, 180, 200, 4.8*14, 0.2);
     vertical_prisms_b(220, 180, 200, 4.8*14, 0.2);
     //vertical_prisms_stitch_hole_a(220, 180, 200, 4.8*14, 0.2, 0, 0);
}
