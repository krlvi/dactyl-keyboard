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

module vertical_prism(height, major_radius) {
     cylinder(h=height, r=major_radius, $fn=6, center=true);
}

module vertical_prisms_slice(x_size, y_size, z_size, spacing, glue_tolerance, bp, x_iteration) {
     major_radius = spacing / (2 + 2 * sin(180/6));
     x_scale_factor = (major_radius - glue_tolerance) / major_radius;
     minor_radius = major_radius * cos(180/6);
     diameter = major_radius + minor_radius;
     width = diameter / 2;
     side_length = major_radius * 2 * sin(180/6);
     zigzag_length = spacing;
     x_offset = bp ? spacing/2 : 0;
     y_offset = bp ? minor_radius : 0;
     col = bp ? "plum" : "lightgreen";
     color(col) {
          x = (-x_size/2) + x_offset + spacing*x_iteration;
          for(y=[-y_size/2+y_offset:2*minor_radius:y_size/2]) {
               for(z=[-z_size/2:zigzag_length:z_size/2]) {
                    translate([x, y, z]) {
                         multmatrix([[x_scale_factor,0,0.5,0],
                                     [0,1,0,0],
                                     [0,0,1,0],
                                     [0,0,0,1]])
                              vertical_prism(zigzag_length/2, major_radius);
                    }
                    translate([x, y, z+zigzag_length/2]) {
                         multmatrix([[x_scale_factor,0,-0.5,0],
                                     [0,1,0,0],
                                     [0,0,1,0],
                                     [0,0,0,1]])
                              vertical_prism(zigzag_length/2, major_radius);
                    }
               }
          }
     }
}

module vertical_prisms_helper(x_size, y_size, z_size, spacing, glue_tolerance, bp) {
     for(x_iteration=[0:1:(x_size/spacing)]) {
          vertical_prisms_slice(x_size, y_size, z_size, spacing, glue_tolerance, bp, x_iteration);
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
