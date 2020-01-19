// A key placement based on British patent 1,016,993
// https://geekhack.org/index.php?topic=63415.0

// explanatory text and Fig 3
main_tent = 30;
pinky_tent = main_tent - 45;
// from index finger out
column_z_rotations = [ 34.56, 34.56, 29.14, 21.84, 13.96, 11.68 ];
tent_split_rotation = -(13.96 + 21.84) / 2;
// I measured a picture of Figure 9 from British patent 1,016,993; the
// keys drawn in there were about 106 px wide. And real keys are 20mm
// wide.
p2mm9 = 20 / 106;
column_x_translations = [ -(166+43)*p2mm9, -43*p2mm9, 0, 0, 0, 160*p2mm9 ];
column_r_translations = [ 1390*p2mm9, 1441*p2mm9, 1498*p2mm9,
                          1456*p2mm9, 1306*p2mm9, 1142*p2mm9 ];
base_r_translation = 1441 * p2mm9;
r_per_key = 150 * p2mm9;

/* // from Figure 4b. In the copy of this picture I obtained, keys might */
/* // be 100px wide. */
/* p2mm4b = 100; */
/* _column_graph_offsets = [ [133, 1067], [221, 965], [363, 878], [346, 793], [206, 695] ]; */
/* _column_graph_rel     = _column_graph_offsets - [133, 1067]; */

module _ColumnPlace(col, row) {
     rotate(a=((col >= 4) ? pinky_tent : main_tent), v=[0,1,0]) {
          translate([84,0,0]) {
               rotate(a=tent_split_rotation, v=[0,0,1]) {
                    translate([0, -base_r_translation, 0]) {
                         rotate(a=column_z_rotations[col], v=[0,0,1]) {
                              translate([column_x_translations[col], column_r_translations[col], 0]) {
                                   children();
                              }
                         }
                    }
               }
          }
     }
}

module _RowPlace(col, row) {
     translate([0, r_per_key * row, 0]) {
          children();
     }
}

module KeyPlace(col, row) {
     _ColumnPlace(col, row)
          _RowPlace(col, row)
          children();
}

for(c=[0:1:5]) for(r=[0:1:4]) {
    KeyPlace(c, r) cube([20, 20, 5], center=true);
}
