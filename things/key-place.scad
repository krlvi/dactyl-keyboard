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

tau = 360; // openscad works in degrees not radians

// placement
bet = tau / 70;
tenting_angle = tau / 15;
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
column_radius = cap_top_height + ( ((mount_width + 2.0) / 2) / sin(bet/2) );
thu_row_radius = cap_top_height + ( ((mount_height + 1/2) / 2) / sin(thu_alp/2) );
thu_column_radius = cap_top_height + ( ((mount_width + 2.0) / 2) / sin(thu_bet/2) );
// connectors
post_size = 0.1;

function ColumnOffset(column) = ((column >= 2) && (column < 3) ? [0, 2.82, -3.0] : (column >= 4) ? [0, -5.8, 5.64] : [0,0,0]);

module KeyPlace(col, row) {
     alphas = [ tau/16, tau/24, tau/18, tau/13, tau/12 ];
     row_compensation = [ [0,2,3], [0,0,0], [0,0,0], [0,0,0], [0,0,0] ];
     alp = alphas[row]; // assumption: row > 0
     row_radius = cap_top_height + ( ((mount_height + 1/2) / 2) / sin(alp/2) );
     column_angle = bet * (2 - col);
     translate([0, 0, 2])
	  rotate(a=tenting_angle, v=[0,1,0])
          translate(ColumnOffset(col))
          translate([0, 0, column_radius])
          rotate(a=column_angle, v=[0, 1, 0])
          translate([0, 0, -column_radius])
          translate(row_compensation[row])
          translate([0, 0, row_radius])
          rotate(a=alp*(2-row), v=[1,0,0])
          translate([0, 0, -row_radius])
          children();
}

module ThumbPlace(col, row) {
     translate([-53, -45, 60])
	  /* perhaps this should be tenting-angle, but look at the
          axis. that will change what the translation after it
          needs to be when you change tenting-angle. */
	  rotate(a=tau/24, v=[1,1,0])
          rotate(a=tau*(1/8 - 3/32), v=[0,0,1])
          translate([mount_width, 0, 0])
          translate([0, 0, thu_column_radius])
          rotate(a=col*thu_bet, v=[0,1,0])
          translate([0, 0, -thu_column_radius])
          translate([0, 0, thu_row_radius])
          rotate(a=row*thu_alp, v=[1,0,0])
          translate([0, 0, -thu_row_radius])
          children();
}


module WebPost() {
     translate ([0, 0, (plate_thickness - (web_thickness/2))]) {
          cube ([post_size, post_size, web_thickness], center=true);
     }
}

module Marker(x) {
     children();
}
