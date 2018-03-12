union () {
  intersection () {
    difference () {
      difference () {
        difference () {
          minkowski () {
            intersection () {
              difference () {
                cube ([30, 20, 54], center=true);
                cube ([29.996999999999996, 19.997999999999998, 53.99459999999999], center=true);
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=24, r=20);
          }
          minkowski () {
            intersection () {
              difference () {
                cube ([30, 20, 54], center=true);
                cube ([29.996999999999996, 19.997999999999998, 53.99459999999999], center=true);
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=24, r=19);
          }
        }
        cube ([30, 20, 54], center=true);
      }
      translate ([0, -10, 0]) {
        mirror ([0, 1, 0]) {
          mirror ([1, 0, 0]) {
            translate ([-0.001, 0, 0]) {
              translate ([0.15, 0, 0]) {
                rotate (a=90.0, v=[0, 1, 0]) {
                  intersection () {
                    translate ([0, 39, 0]) {
                      cube ([78, 78, 78], center=true);
                    }
                    cylinder (h=0.3, r=38.3, center=true);
                  }
                }
              }
            }
          }
        }
      }
    }
    translate ([-20, 0, 0]) {
      cube ([40, 80, 40], center=true);
    }
  }
  difference () {
    hull () {
      translate ([0, 10, 0]) {
        translate ([-1.3, 0, 0]) {
          translate ([0.65, 0, 0]) {
            rotate (a=90.0, v=[0, 1, 0]) {
              intersection () {
                translate ([0, 20, 0]) {
                  cube ([40, 40, 40], center=true);
                }
                cylinder (h=1.3, r=19.3, center=true);
              }
            }
          }
        }
      }
      intersection () {
        difference () {
          difference () {
            minkowski () {
              intersection () {
                difference () {
                  cube ([30, 20, 54], center=true);
                  cube ([29.996999999999996, 19.997999999999998, 53.99459999999999], center=true);
                }
                cube ([400, 400, 0.2], center=true);
              }
              sphere ($fn=24, r=20);
            }
            minkowski () {
              intersection () {
                difference () {
                  cube ([30, 20, 54], center=true);
                  cube ([29.996999999999996, 19.997999999999998, 53.99459999999999], center=true);
                }
                cube ([400, 400, 0.2], center=true);
              }
              sphere ($fn=24, r=19);
            }
          }
          cube ([30, 20, 54], center=true);
        }
        translate ([0, 10, 0]) {
          translate ([-1.3, 0, 0]) {
            translate ([0.65, 0, 0]) {
              rotate (a=90.0, v=[0, 1, 0]) {
                intersection () {
                  translate ([0, 39, 0]) {
                    cube ([78, 78, 78], center=true);
                  }
                  cylinder (h=1.3, r=38.3, center=true);
                }
              }
            }
          }
        }
      }
    }
    translate ([0, 10, 0]) {
      translate ([-1.9500000000000002, 0, 0]) {
        translate ([1.3, 0, 0]) {
          rotate (a=90.0, v=[0, 1, 0]) {
            intersection () {
              translate ([0, 19.808999999999997, 0]) {
                cube ([39.62, 39.62, 39.62], center=true);
              }
              cylinder (h=2.6, r=19.11, center=true);
            }
          }
        }
      }
    }
  }
  translate ([0, 10, 0]) {
    union () {
      difference () {
        translate ([-1.3, 0, 0]) {
          translate ([0.65, 0, 0]) {
            rotate (a=90.0, v=[0, 1, 0]) {
              intersection () {
                translate ([0, 20, 0]) {
                  cube ([40, 40, 40], center=true);
                }
                cylinder (h=1.3, r=19.3, center=true);
              }
            }
          }
        }
        translate ([-14.25, 0, 0]) {
          translate ([14.2505, 0, 0]) {
            rotate (a=90.0, v=[0, 1, 0]) {
              intersection () {
                translate ([0, 16.199, 0]) {
                  cube ([32.400000000000006, 32.400000000000006, 32.400000000000006], center=true);
                }
                cylinder (h=28.501, r=15.500000000000002, center=true);
              }
            }
          }
        }
      }
      difference () {
        intersection () {
          union () {
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, -7.794228634059947, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, -7.794228634059947, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, -7.794228634059947, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, -7.794228634059947, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, -7.794228634059947, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 0.0, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 0.0, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 0.0, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 0.0, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 0.0, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 7.794228634059947, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 7.794228634059947, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 7.794228634059947, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 7.794228634059947, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 7.794228634059947, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 15.588457268119894, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 15.588457268119894, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 15.588457268119894, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 15.588457268119894, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 15.588457268119894, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 23.38268590217984, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 23.38268590217984, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 23.38268590217984, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 23.38268590217984, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 23.38268590217984, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 31.17691453623979, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 31.17691453623979, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 31.17691453623979, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 31.17691453623979, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 31.17691453623979, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                  }
                }
              }
            }
          }
          translate ([0, 0, 0]) {
            translate ([8.075, 0, 0]) {
              rotate (a=90.0, v=[0, 1, 0]) {
                intersection () {
                  translate ([0, 17.15, 0]) {
                    cube ([34.3, 34.3, 34.3], center=true);
                  }
                  cylinder (h=16.15, r1=16.45, r2=0, center=true);
                }
              }
            }
          }
        }
        intersection () {
          union () {
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, -7.794228634059947, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, -7.794228634059947, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, -7.794228634059947, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, -7.794228634059947, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, -7.794228634059947, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, -7.794228634059947, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 0.0, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 0.0, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 0.0, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 0.0, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 0.0, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 0.0, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 7.794228634059947, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 7.794228634059947, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 7.794228634059947, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 7.794228634059947, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 7.794228634059947, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 7.794228634059947, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 15.588457268119894, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 15.588457268119894, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 15.588457268119894, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 15.588457268119894, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 15.588457268119894, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 15.588457268119894, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 23.38268590217984, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 23.38268590217984, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 23.38268590217984, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 23.38268590217984, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 23.38268590217984, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 23.38268590217984, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, -34.5]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 3.083333333333333]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 31.17691453623979, -27.749999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 2.3333333333333326]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, -20.999999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 1.5833333333333328]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 31.17691453623979, -14.249999999999996]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.833333333333333]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, -7.499999999999997]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, 0.08333333333333304]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 31.17691453623979, -0.7499999999999973]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -0.6666666666666669]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, 6.000000000000003]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -1.416666666666667]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 31.17691453623979, 12.750000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.166666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, 19.500000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -2.9166666666666665]) {
              translate ([0, 8.500000000000002, 0]) {
                translate ([0, 31.17691453623979, 26.250000000000004]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
            translate ([0, 0, -3.666666666666667]) {
              translate ([0, 4.000000000000002, 0]) {
                translate ([0, 31.17691453623979, 33.0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=6, h=7.1615223689149765, r1=7.1615223689149765, r2=0, center=true);
                  }
                }
              }
            }
          }
          translate ([-0.001, 0, 0]) {
            translate ([7.601000000000001, 0, 0]) {
              rotate (a=90.0, v=[0, 1, 0]) {
                intersection () {
                  translate ([0, 16.200000000000003, 0]) {
                    cube ([32.400000000000006, 32.400000000000006, 32.400000000000006], center=true);
                  }
                  cylinder (h=15.202000000000002, r1=15.500000000000002, r2=0, center=true);
                }
              }
            }
          }
        }
        cube ([18, 0.001, 38], center=true);
      }
    }
  }
  difference () {
    hull () {
      translate ([0, -10, 0]) {
        mirror ([0, 1, 0]) {
          mirror ([1, 0, 0]) {
            translate ([0.3, 0, 0]) {
              translate ([0.65, 0, 0]) {
                rotate (a=90.0, v=[0, 1, 0]) {
                  intersection () {
                    translate ([0, 20, 0]) {
                      cube ([40, 40, 40], center=true);
                    }
                    cylinder (h=1.3, r=19.3, center=true);
                  }
                }
              }
            }
          }
        }
      }
      intersection () {
        difference () {
          difference () {
            minkowski () {
              intersection () {
                difference () {
                  cube ([30, 20, 54], center=true);
                  cube ([29.996999999999996, 19.997999999999998, 53.99459999999999], center=true);
                }
                cube ([400, 400, 0.2], center=true);
              }
              sphere ($fn=24, r=20);
            }
            minkowski () {
              intersection () {
                difference () {
                  cube ([30, 20, 54], center=true);
                  cube ([29.996999999999996, 19.997999999999998, 53.99459999999999], center=true);
                }
                cube ([400, 400, 0.2], center=true);
              }
              sphere ($fn=24, r=19);
            }
          }
          cube ([30, 20, 54], center=true);
        }
        translate ([0, -10, 0]) {
          mirror ([0, 1, 0]) {
            mirror ([1, 0, 0]) {
              translate ([0.3, 0, 0]) {
                translate ([0.65, 0, 0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    intersection () {
                      translate ([0, 39, 0]) {
                        cube ([78, 78, 78], center=true);
                      }
                      cylinder (h=1.3, r=38.3, center=true);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    translate ([0, -10, 0]) {
      mirror ([0, 1, 0]) {
        mirror ([1, 0, 0]) {
          translate ([-0.35000000000000003, 0, 0]) {
            translate ([1.3, 0, 0]) {
              rotate (a=90.0, v=[0, 1, 0]) {
                intersection () {
                  translate ([0, 19.808999999999997, 0]) {
                    cube ([39.62, 39.62, 39.62], center=true);
                  }
                  cylinder (h=2.6, r=19.11, center=true);
                }
              }
            }
          }
        }
      }
    }
  }
  translate ([0, -10, 0]) {
    mirror ([0, 1, 0]) {
      mirror ([1, 0, 0]) {
        translate ([0.3, 0, 0]) {
          union () {
            difference () {
              translate ([0, 0, 0]) {
                translate ([0.65, 0, 0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    intersection () {
                      translate ([0, 20, 0]) {
                        cube ([40, 40, 40], center=true);
                      }
                      cylinder (h=1.3, r=19.3, center=true);
                    }
                  }
                }
              }
              translate ([-14.25, 0, 0]) {
                translate ([14.2505, 0, 0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    intersection () {
                      translate ([0, 17.148999999999997, 0]) {
                        cube ([34.3, 34.3, 34.3], center=true);
                      }
                      cylinder (h=28.501, r=16.45, center=true);
                    }
                  }
                }
              }
            }
            difference () {
              intersection () {
                union () {
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, -7.794228634059947, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, -7.794228634059947, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, -7.794228634059947, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, -7.794228634059947, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, -7.794228634059947, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 0.0, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 0.0, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 0.0, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 0.0, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 0.0, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 7.794228634059947, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 7.794228634059947, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 7.794228634059947, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 7.794228634059947, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 7.794228634059947, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 15.588457268119894, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 15.588457268119894, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 15.588457268119894, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 15.588457268119894, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 15.588457268119894, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 23.38268590217984, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 23.38268590217984, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 23.38268590217984, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 23.38268590217984, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 23.38268590217984, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 31.17691453623979, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 31.17691453623979, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 31.17691453623979, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 31.17691453623979, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 31.17691453623979, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=10.3, r1=10.3, r2=0, center=true);
                        }
                      }
                    }
                  }
                }
                translate ([0, 0, 0]) {
                  translate ([8.55, 0, 0]) {
                    rotate (a=90.0, v=[0, 1, 0]) {
                      intersection () {
                        translate ([0, 18.1, 0]) {
                          cube ([36.2, 36.2, 36.2], center=true);
                        }
                        cylinder (h=17.1, r1=17.400000000000002, r2=0, center=true);
                      }
                    }
                  }
                }
              }
              intersection () {
                union () {
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, -7.794228634059947, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, -7.794228634059947, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, -7.794228634059947, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, -7.794228634059947, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, -7.794228634059947, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, -7.794228634059947, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 0.0, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 0.0, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 0.0, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 0.0, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 0.0, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 0.0, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 7.794228634059947, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 7.794228634059947, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 7.794228634059947, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 7.794228634059947, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 7.794228634059947, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 7.794228634059947, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 15.588457268119894, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 15.588457268119894, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 15.588457268119894, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 15.588457268119894, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 15.588457268119894, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 15.588457268119894, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 23.38268590217984, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 23.38268590217984, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 23.38268590217984, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 23.38268590217984, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 23.38268590217984, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 23.38268590217984, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, -34.5]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 3.083333333333333]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 31.17691453623979, -27.749999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 2.3333333333333326]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, -20.999999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 1.5833333333333328]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 31.17691453623979, -14.249999999999996]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.833333333333333]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, -7.499999999999997]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, 0.08333333333333304]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 31.17691453623979, -0.7499999999999973]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -0.6666666666666669]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, 6.000000000000003]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -1.416666666666667]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 31.17691453623979, 12.750000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.166666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, 19.500000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -2.9166666666666665]) {
                    translate ([0, 8.500000000000002, 0]) {
                      translate ([0, 31.17691453623979, 26.250000000000004]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                  translate ([0, 0, -3.666666666666667]) {
                    translate ([0, 4.000000000000002, 0]) {
                      translate ([0, 31.17691453623979, 33.0]) {
                        rotate (a=90.0, v=[0, 1, 0]) {
                          cylinder ($fn=6, h=9, r1=9, r2=0, center=true);
                        }
                      }
                    }
                  }
                }
                translate ([-0.001, 0, 0]) {
                  translate ([8.075999999999999, 0, 0]) {
                    rotate (a=90.0, v=[0, 1, 0]) {
                      intersection () {
                        translate ([0, 17.15, 0]) {
                          cube ([34.3, 34.3, 34.3], center=true);
                        }
                        cylinder (h=16.151999999999997, r1=16.45, r2=0, center=true);
                      }
                    }
                  }
                }
              }
              cube ([20.6, 0.001, 38], center=true);
            }
          }
        }
      }
    }
  }
}
