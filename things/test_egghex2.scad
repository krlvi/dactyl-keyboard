use <egghex2.scad>;

module a_hex_for_test() {
     indicator_r = 2;
     union() {
          hex_prism(20, 40, [0, 3, 3], [0, 2, 2], [0, 5, 5], 0, 0, 0, 0);
          *for(n=[0:5]) {
               c = ["red", "yellow", "green", "cyan", "blue", "magenta"][n];
               rotate(a=n*60, v=[0,0,1])
                    translate([20, 0, 40]) color(c) sphere(r=indicator_r);
          }
     }
}
          
module seven_hexes_close_packed() {
     slop = 2;
     minor_radius = 40;
     tx = minor_radius + slop;
     a_hex_for_test();
     for(a=[0:60:360-1]) {
          rotate(a=a, v=[0,0,1])
               translate([tx, 0, 0])
               rotate(a=-a, v=[0,0,1])
               a_hex_for_test();
     }
}

module hex_matches_for_test() {
     slop = 2;
     rmin = 40;
     for(n=[0:2]) {
          a = n * 120;
          translate([3*rmin*n, 0, 0]) {
               rotate(a=a, v=[0,0,1])
                    a_hex_for_test();
               translate([rmin + slop, 0, 0])
                    rotate(a=a, v=[0,0,1])
                    a_hex_for_test();
          }
     }
}

module hex_test_object_four() {
     for(i=[0:0]) {
          hex_prism_of_grid([220, 180, 200], i, 42, 1,
                            [2,2,2], [5,5,5], [10, 10, 10],
                            1, 12, 0.2, 1);
          }
}

module hex_test_object_five() {
     hex_prism(10, 13, [0, 1, 2], [0, 3, 4], [0, 6, 6],
          1, 4, 0.2, 1);
}

module split_curved_plate_for_test() {
     intersection() {
          translate([10,20,100])
               difference() {
               sphere(r=100);
               sphere(r=97);
               }
          seven_hexes_close_packed();
     }
}


//hex_matches_for_test();
split_curved_plate_for_test();
