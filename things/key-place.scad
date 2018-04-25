
tau = 360; // openscad works in degrees not radians

// placement
alp = tau / 24;
bet = tau / 72;
tenting_angle = tau / 24;
// switch_hole
keyswitch_height = 14.4;
keyswitch_width = 14.4;
mount_width = keyswitch_width + 3;
mount_height = keyswitch_height + 3;
sa_profile_key_height = 12.7;
plate_thickness = 4;
// placement
cap_top_height = plate_thickness + sa_profile_key_height;
row_radius = cap_top_height + ( ((mount_height + 1/2) / 2) / sin(alp/2) );
column_radius = cap_top_height + ( ((mount_width + 2.0) / 2) / sin(bet/2) );

function ColumnOffset(column) = ((column >= 2) && (column < 3) ? [0, 2.82, -3.0] : (column >= 4) ? [0, -5.8, 5.64] : [0,0,0]);

module KeyPlace(col, row) {
     column_angle = bet * (2 - col);
     translate([0, 0, 13]) {
	  rotate(a=tenting_angle, v=[0,1,0]) {
	       translate(ColumnOffset(col)) {
		    translate([0, 0, column_radius]) {
			 rotate(a=column_angle, v=[0, 1, 0]) {
			      translate([0, 0, -column_radius]) {
				   translate([0, 0, row_radius]) {
					rotate(a=alp*(2-row), v=[1,0,0]) {
					     translate([0, 0, -row_radius]) {
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
