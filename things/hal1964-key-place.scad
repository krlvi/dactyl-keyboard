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
p2mm9 = 18 / 106;
column_x_translations = [ -(166+43)*p2mm9, -43*p2mm9, 0, -18*p2mm9, 25*p2mm9, 87*p2mm9 ];
column_r_translations = [ 1490*p2mm9, 1441*p2mm9, 1498*p2mm9,
                          1400*p2mm9, 1306*p2mm9, 1250*p2mm9 ];
base_r_translation = 1441 * p2mm9;
r_per_key = 150 * p2mm9;

// from Figure 4b. In the copy of this picture I obtained, keys might
// be 100px wide.
p2mm4b = 10 / 100;
_column_graph_pixels  = [ [133, 1067], [221, 965], [363, 878], [346, 793], [206, 695], [206, 695] ];
base_column_translation = [363, 878];
_column_graph_offsets = [for(c = _column_graph_pixels)
          [(c.x - base_column_translation.x) * p2mm4b,
           (c.y - base_column_translation.y) * p2mm4b]];
// N.B. these are listed from LOWEST degree (i.e. constant) to HIGHEST
column_scoop_coefficients = [ [56.951, -319.638, 84.518],
                                     [56.951, -319.638, 84.518],
                                     [56.951, -319.638, 84.518],
                                     [56.951, -319.638, 84.518],
                                     [56.951, -319.638, 84.518],
                                     [56.951, -319.638, 84.518] ];

function butlast(xs) = len(xs) >= 2 ? [for(i=[1:len(xs)-1]) xs[i]] : [];
function polynomial_at(coefs, x, m=1) = (len(coefs) == 0) ? 0 :
     m * coefs[0] + polynomial_at(butlast(coefs), x, m*x);

function column_scoop(col, row) =
     polynomial_at(column_scoop_coefficients[col], row) * p2mm4b;

module _ColumnPlace(col, row) {
     tents = [30, 30, 30, 20, -15, -15];
     rotate(a=tents[col], v=[0,1,0])
          translate([84,0,0])
          translate([0, -base_r_translation, 0])
          rotate(a=column_z_rotations[col], v=[0,0,1])
          translate([column_x_translations[col], column_r_translations[col], 0])
          rotate(a=tent_split_rotation, v=[0,0,1])
          children();
}


tau = 360; // openscad works in degrees not radians

// placement
alp = tau / 24;
bet = tau / 72;
tenting_angle = tau / 24;
thu_alp = tau / 24;
thu_bet = tau / 72;
// switch_hole
keyswitch_height = 14.0;
keyswitch_width = 14.0;
mount_width = keyswitch_width + 3;
mount_height = keyswitch_height + 3;
sa_profile_key_height = 12.7;
plate_thickness = 4;
web_thickness = 3.5;
// placement
cap_top_height = plate_thickness + sa_profile_key_height;
row_radius = cap_top_height + ( ((mount_height + 1/2) / 2) / sin(alp/2) );
column_radius = cap_top_height + ( ((mount_width + 2.0) / 2) / sin(bet/2) );
thu_row_radius = cap_top_height + ( ((mount_height + 1/2) / 2) / sin(thu_alp/2) );
thu_column_radius = cap_top_height + ( ((mount_width + 2.0) / 2) / sin(thu_bet/2) );
// connectors
post_size = 0.1;


module _RowPlace(col, row) {
     alpha = 360 / 24;
    // alphas = [13, 15, 18, 24, 30];
     alphas = [3,5,8,13,21,34,55];
     keyswitch_height = 14.0;
     keyswitch_width = 14.0;
     mount_width = keyswitch_width + 3;
     mount_height = keyswitch_height + 3;
     sa_profile_key_height = 12.7;
     plate_thickness = 4;
     web_thickness = 3.5;
     cap_top_height = plate_thickness + sa_profile_key_height;
//     row_radius = cap_top_height + ( ((mount_height + 1/2) / 2) / sin(alpha/2) );
     row_radius = cap_top_height + ( ((mount_height + 1/2) / 2) / sin(alphas[row]/2) );
//     echo(row, column_scoop(col, row));
//     translate([0, r_per_key * row, 0]) {
          cgo = _column_graph_offsets[col];
//          translate([0, 0, cgo.y])
               translate([0, 0, row_radius])
//               rotate(a=alpha*(2-row), v=[1,0,0])
               rotate(a=alphas[row]*(2-row), v=[1,0,0])
               translate([0, 0, -row_radius])
//               translate([0,0,column_scoop(col, row)])
               children();
//     }
}

module KeyPlace(col, row) {
     _ColumnPlace(col, row)
          _RowPlace(col, row)
          children();
}


module ThumbPlace(col, row) {
     thu_alp = tau / 24;
     thu_bet = tau / 72;
     translate([-53, -45, 60]) {
	  /* perhaps this should be tenting-angle, but look at the
          axis. that will change what the translation after it
          needs to be when you change tenting-angle. */
	  rotate(a=tau/24, v=[1,1,0]) {
	       rotate(a=tau*(1/8 - 3/32), v=[0,0,1]) {
		    translate([mount_width, 0, 0]) {
			 translate([0, 0, thu_column_radius]) {
			      rotate(a=col*thu_bet, v=[0,1,0]) {
				   translate([0, 0, -thu_column_radius]) {
					translate([0, 0, thu_row_radius]) {
					     rotate(a=row*thu_alp, v=[1,0,0]) {
						  translate([0, 0, -thu_row_radius]) {
						       children();
						  }
					     }
					}
				   }
			      }
			 }
		    }
	       }
	  }
     }
}


module WebPost() {
     translate ([0, 0, (plate_thickness - (web_thickness/2))]) {
          cube ([post_size, post_size, web_thickness], center=true);
     }
}

module Marker(x) {
     children();
}





for(c=[0:1:5]) for(r=[0:1:4]) {
    KeyPlace(c, r) cube([20, 20, 5], center=true);
}

