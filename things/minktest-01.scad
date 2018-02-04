union () {
  intersection () {
    difference () {
      difference () {
        difference () {
          minkowski () {
            intersection () {
              difference () {
                union () {
                  cube ([30, 30, 30], center=true);
                }
                union () {
                  cube ([29.99, 29.99, 29.99], center=true);
                }
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=15, r=10);
          }
          minkowski () {
            intersection () {
              difference () {
                union () {
                  cube ([30, 30, 30], center=true);
                }
                union () {
                  cube ([29.99, 29.99, 29.99], center=true);
                }
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=15, r=8.5);
          }
        }
        union () {
          cube ([30, 30, 30], center=true);
        }
      }
      translate ([0, -15, 0]) {
        mirror ([0, 1, 0]) {
          mirror ([1, 0, 0]) {
            translate ([-0.001, 0, 0]) {
              translate ([0.25, 0, 0]) {
                rotate (a=90.0, v=[0, 1, 0]) {
                  intersection () {
                    translate ([0, 18.0, 0]) {
                      cube ([36.0, 36.0, 36.0], center=true);
                    }
                    cylinder (h=0.5, r=17.0, center=true);
                  }
                }
              }
            }
          }
        }
      }
      translate ([20, 0, 0]) {
        cube ([40, 80, 40], center=true);
      }
    }
  }
  hull () {
    translate ([0, 15, 0]) {
      translate ([-8/3, 0, 0]) {
        translate ([4/3, 0, 0]) {
          rotate (a=90.0, v=[0, 1, 0]) {
            intersection () {
              translate ([0, 9.5, 0]) {
                cube ([19.0, 19.0, 19.0], center=true);
              }
              cylinder (h=8/3, r=8.5, center=true);
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
                union () {
                  cube ([30, 30, 30], center=true);
                }
                union () {
                  cube ([29.99, 29.99, 29.99], center=true);
                }
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=15, r=10);
          }
          minkowski () {
            intersection () {
              difference () {
                union () {
                  cube ([30, 30, 30], center=true);
                }
                union () {
                  cube ([29.99, 29.99, 29.99], center=true);
                }
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=15, r=8.5);
          }
        }
        union () {
          cube ([30, 30, 30], center=true);
        }
      }
      translate ([0, 15, 0]) {
        translate ([-8/3, 0, 0]) {
          translate ([4/3, 0, 0]) {
            rotate (a=90.0, v=[0, 1, 0]) {
              intersection () {
                translate ([0, 18.0, 0]) {
                  cube ([36.0, 36.0, 36.0], center=true);
                }
                cylinder (h=8/3, r=17.0, center=true);
              }
            }
          }
        }
      }
    }
  }
  translate ([0, 15, 0]) {
    union () {
      translate ([-8/3, 0, 0]) {
        translate ([4/3, 0, 0]) {
          rotate (a=90.0, v=[0, 1, 0]) {
            intersection () {
              translate ([0, 9.5, 0]) {
                cube ([19.0, 19.0, 19.0], center=true);
              }
              cylinder (h=8/3, r=8.5, center=true);
            }
          }
        }
      }
      translate ([-8/3, 0, 0]) {
        union () {
          translate ([4, 0, 0]) {
            rotate (a=45.0, v=[-1, 0, 0]) {
              translate ([0, 0, 5.666666666666667]) {
                rotate (a=90.0, v=[0, 1, 0]) {
                  cylinder ($fn=8, h=8, r=1.0625, center=true);
                }
              }
            }
          }
          translate ([4, 0, 0]) {
            rotate (a=90.0, v=[-1, 0, 0]) {
              translate ([0, 0, 5.666666666666667]) {
                rotate (a=90.0, v=[0, 1, 0]) {
                  cylinder ($fn=8, h=8, r=1.0625, center=true);
                }
              }
            }
          }
          translate ([4, 0, 0]) {
            rotate (a=135.0, v=[-1, 0, 0]) {
              translate ([0, 0, 5.666666666666667]) {
                rotate (a=90.0, v=[0, 1, 0]) {
                  cylinder ($fn=8, h=8, r=1.0625, center=true);
                }
              }
            }
          }
        }
      }
    }
  }
  hull () {
    translate ([0, -15, 0]) {
      mirror ([0, 1, 0]) {
        mirror ([1, 0, 0]) {
          translate ([5.833333333333333, 0, 0]) {
            translate ([1.5833333333333335, 0, 0]) {
              rotate (a=90.0, v=[0, 1, 0]) {
                intersection () {
                  translate ([0, 9.5, 0]) {
                    cube ([19.0, 19.0, 19.0], center=true);
                  }
                  cylinder (h=3.166666666666667, r=8.5, center=true);
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
                union () {
                  cube ([30, 30, 30], center=true);
                }
                union () {
                  cube ([29.99, 29.99, 29.99], center=true);
                }
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=15, r=10);
          }
          minkowski () {
            intersection () {
              difference () {
                union () {
                  cube ([30, 30, 30], center=true);
                }
                union () {
                  cube ([29.99, 29.99, 29.99], center=true);
                }
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=15, r=8.5);
          }
        }
        union () {
          cube ([30, 30, 30], center=true);
        }
      }
      translate ([0, -15, 0]) {
        mirror ([0, 1, 0]) {
          mirror ([1, 0, 0]) {
            translate ([8.5, 0, 0]) {
              translate ([1.5833333333333335, 0, 0]) {
                rotate (a=90.0, v=[0, 1, 0]) {
                  intersection () {
                    translate ([0, 18.0, 0]) {
                      cube ([36.0, 36.0, 36.0], center=true);
                    }
                    cylinder (h=3.166666666666667, r=17.0, center=true);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  translate ([0, -15, 0]) {
    mirror ([0, 1, 0]) {
      mirror ([1, 0, 0]) {
        difference () {
          translate ([0.5, 0, 0]) {
            translate ([8/3, 0, 0]) {
              rotate (a=90.0, v=[0, 1, 0]) {
                intersection () {
                  translate ([0, 9.5, 0]) {
                    cube ([19.0, 19.0, 19.0], center=true);
                  }
                  cylinder (h=16/3, r=8.5, center=true);
                }
              }
            }
          }
          translate ([0.5, 0, 0]) {
            union () {
              translate ([4, 0, 0]) {
                rotate (a=45.0, v=[-1, 0, 0]) {
                  translate ([0, 0, 5.666666666666667]) {
                    rotate (a=90.0, v=[0, 1, 0]) {
                      cylinder ($fn=8, h=9.0, r=1.5625, center=true);
                    }
                  }
                }
              }
              translate ([4, 0, 0]) {
                rotate (a=90.0, v=[-1, 0, 0]) {
                  translate ([0, 0, 5.666666666666667]) {
                    rotate (a=90.0, v=[0, 1, 0]) {
                      cylinder ($fn=8, h=9.0, r=1.5625, center=true);
                    }
                  }
                }
              }
              translate ([4, 0, 0]) {
                rotate (a=135.0, v=[-1, 0, 0]) {
                  translate ([0, 0, 5.666666666666667]) {
                    rotate (a=90.0, v=[0, 1, 0]) {
                      cylinder ($fn=8, h=9.0, r=1.5625, center=true);
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
