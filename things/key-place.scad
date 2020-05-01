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
bet = tau / 68;
tenting_angle = tau / 10;
thu_alp = tau / 10;
thu_bet = tau / 80;
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
column_radius = cap_top_height + ( ((mount_width + 3.0) / 2) / sin(bet/2) );
thu_row_radius = cap_top_height + ( ((mount_height + 1/2) / 2) / sin(thu_alp/2) );
thu_column_radius = cap_top_height + ( ((mount_width + 2.0) / 2) / sin(thu_bet/2) );
// connectors
post_size = 0.1;

function ColumnOffset(column) = ((column >= 2) && (column < 3) ? [0, 5.82, -4.0] : (column >= 5) ? [0, -12.8, 5.64] : (column >= 4) ? [0, -8.8, 5.64] : [0,0,0]);

module KeyPlace(col, row) {
     row_alphas = [ tau/16, tau/24, tau/18, tau/13, tau/12 ];
     // must be >= 1.0
     column_radius_factors = [ 1.05, 1.05, 1.12, 1.03, 1.0, 1.0 ];
     row_compensation = [ [0,-2,3], [0,0,0], [0,0,0], [0,0,0], [0,2,0] ];
     alp = row_alphas[max(floor(row),0)];
     row_radius = (cap_top_height + ( ((mount_height + 1/2) / 2) / sin(alp/2) )) * column_radius_factors[max(floor(col),0)];
     column_angle = bet * (2 - col);
     column_splay = [0, 0, 5, 9, 14, 14];
     column_splay_radius = 40; // this has an interplay with the
                               // measure added to mount_width above
                               // in column_radius
     translate([0, 0, 16])
	  rotate(a=tenting_angle, v=[0,1,0])
          translate(ColumnOffset(col))
          translate([0, 0, column_radius])
          rotate(a=column_angle, v=[0, 1, 0])
          translate([0, 0, -column_radius])
          translate([0, -column_splay_radius, 0])
          rotate(a=-column_splay[max(floor(col),0)], v=[0,0,1])
          translate([0, column_splay_radius, 0])
          translate(row_compensation[max(floor(row),0)])
          translate([0, 0, row_radius])
          rotate(a=alp*(2-row), v=[1,0,0])
          translate([0, 0, -row_radius])
          children();
}

module ThumbPlace(col, row) {
     thumb_column_splay = [0, 6, 13, 15, 15];
     thumb_column_splay_radius = 10; // this has an interplay with the
                               // measure added to mount_width above
                               // in column_radius
     thumb_column_y_compensation = [0, -1, -5, -6, -6];
     translate([-40, -50, 60])
          rotate(a=tau/20, v=[0,0,1])
          rotate(a=tau/30, v=[0,1,0])
          translate([mount_width, 0, 0])
          translate([0, 0, thu_column_radius])
          rotate(a=col*thu_bet, v=[0,1,0])
          translate([0, 0, -thu_column_radius])
          translate([0, -thumb_column_splay_radius, 0])
          translate([0, thumb_column_y_compensation[max(floor(col),0)], 0])
          rotate(a=thumb_column_splay[max(floor(col),0)], v=[0,0,1])
          translate([0, thumb_column_splay_radius, 0])
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

module WebLogHBL() {
     translate ([-web_thickness/2, 0, (plate_thickness - web_thickness)])
          cube ([web_thickness, post_size, post_size], center=true);
}
module WebLogHBR() {
     translate ([web_thickness/2, 0, (plate_thickness - web_thickness)])
          cube ([web_thickness, post_size, post_size], center=true);
}
module WebLogHTL() {
     translate ([-web_thickness/2, 0, plate_thickness])
          cube ([web_thickness, post_size, post_size], center=true);
}
module WebLogHTR() {
     translate ([web_thickness/2, 0, plate_thickness])
          cube ([web_thickness, post_size, post_size], center=true);
}


module Marker(x) {
     children();
}
