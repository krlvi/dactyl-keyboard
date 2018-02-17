union () {
  intersection () {
    difference () {
      difference () {
        difference () {
          minkowski () {
            intersection () {
              difference () {
                cube ([20, 15, 30], center=true);
                cube ([19.997999999999998, 14.998499999999998, 29.996999999999996], center=true);
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=16, r=15);
          }
          minkowski () {
            intersection () {
              difference () {
                cube ([20, 15, 30], center=true);
                cube ([19.997999999999998, 14.998499999999998, 29.996999999999996], center=true);
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=16, r=14);
          }
        }
        cube ([20, 15, 30], center=true);
      }
      translate ([0, -15/2, 0]) {
        mirror ([0, 1, 0]) {
          mirror ([1, 0, 0]) {
            translate ([-0.001, 0, 0]) {
              translate ([0.25, 0, 0]) {
                rotate (a=90.0, v=[0, 1, 0]) {
                  intersection () {
                    translate ([0, 29, 0]) {
                      cube ([58, 58, 58], center=true);
                    }
                    cylinder (h=0.5, r=28.5, center=true);
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
  hull () {
    translate ([0, 15/2, 0]) {
      translate ([-2, 0, 0]) {
        translate ([1, 0, 0]) {
          rotate (a=90.0, v=[0, 1, 0]) {
            intersection () {
              translate ([0, 15, 0]) {
                cube ([30, 30, 30], center=true);
              }
              cylinder (h=2, r=14.5, center=true);
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
                cube ([20, 15, 30], center=true);
                cube ([19.997999999999998, 14.998499999999998, 29.996999999999996], center=true);
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=16, r=15);
          }
          minkowski () {
            intersection () {
              difference () {
                cube ([20, 15, 30], center=true);
                cube ([19.997999999999998, 14.998499999999998, 29.996999999999996], center=true);
              }
              cube ([400, 400, 0.2], center=true);
            }
            sphere ($fn=16, r=14);
          }
        }
        cube ([20, 15, 30], center=true);
      }
      translate ([0, 15/2, 0]) {
        translate ([-2, 0, 0]) {
          translate ([1, 0, 0]) {
            rotate (a=90.0, v=[0, 1, 0]) {
              intersection () {
                translate ([0, 29, 0]) {
                  cube ([58, 58, 58], center=true);
                }
                cylinder (h=2, r=28.5, center=true);
              }
            }
          }
        }
      }
    }
  }
  translate ([0, 15/2, 0]) {
    union () {
      translate ([-2, 0, 0]) {
        translate ([1, 0, 0]) {
          rotate (a=90.0, v=[0, 1, 0]) {
            intersection () {
              translate ([0, 15, 0]) {
                cube ([30, 30, 30], center=true);
              }
              cylinder (h=2, r=14.5, center=true);
            }
          }
        }
      }
      union () {
        rotate (a=45.0, v=[-1, 0, 0]) {
          translate ([0, 0, 7.700000000000001]) {
            rotate (a=-45.0, v=[-1, 0, 0]) {
              rotate (a=135.0, v=[1, 0, 0]) {
                difference () {
                  translate ([0, 0, 0]) {
                    rotate (a=45.0, v=[1, 0, 0]) {
                      rotate (a=45.0, v=[1, 0, 0]) {
                        rotate (a=45.0, v=[0, 1, 0]) {
                          cube ([5.65685424949238, 5.65685424949238, 5.65685424949238], center=true);
                        }
                      }
                    }
                  }
                  translate ([-200, 0, 0]) {
                    cube ([400, 400, 400], center=true);
                  }
                }
              }
            }
          }
        }
        rotate (a=90.0, v=[-1, 0, 0]) {
          translate ([0, 0, 7.700000000000001]) {
            rotate (a=-90.0, v=[-1, 0, 0]) {
              rotate (a=135.0, v=[1, 0, 0]) {
                difference () {
                  translate ([0, 0, 0]) {
                    rotate (a=45.0, v=[1, 0, 0]) {
                      rotate (a=45.0, v=[1, 0, 0]) {
                        rotate (a=45.0, v=[0, 1, 0]) {
                          cube ([5.65685424949238, 5.65685424949238, 5.65685424949238], center=true);
                        }
                      }
                    }
                  }
                  translate ([-200, 0, 0]) {
                    cube ([400, 400, 400], center=true);
                  }
                }
              }
            }
          }
        }
        rotate (a=135.0, v=[-1, 0, 0]) {
          translate ([0, 0, 7.700000000000001]) {
            rotate (a=-135.0, v=[-1, 0, 0]) {
              rotate (a=135.0, v=[1, 0, 0]) {
                difference () {
                  translate ([0, 0, 0]) {
                    rotate (a=45.0, v=[1, 0, 0]) {
                      rotate (a=45.0, v=[1, 0, 0]) {
                        rotate (a=45.0, v=[0, 1, 0]) {
                          cube ([5.65685424949238, 5.65685424949238, 5.65685424949238], center=true);
                        }
                      }
                    }
                  }
                  translate ([-200, 0, 0]) {
                    cube ([400, 400, 400], center=true);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  difference () {
    hull () {
      translate ([0, -15/2, 0]) {
        mirror ([0, 1, 0]) {
          mirror ([1, 0, 0]) {
            translate ([0.5, 0, 0]) {
              translate ([1, 0, 0]) {
                rotate (a=90.0, v=[0, 1, 0]) {
                  intersection () {
                    translate ([0, 15, 0]) {
                      cube ([30, 30, 30], center=true);
                    }
                    cylinder (h=2, r=14.5, center=true);
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
                  cube ([20, 15, 30], center=true);
                  cube ([19.997999999999998, 14.998499999999998, 29.996999999999996], center=true);
                }
                cube ([400, 400, 0.2], center=true);
              }
              sphere ($fn=16, r=15);
            }
            minkowski () {
              intersection () {
                difference () {
                  cube ([20, 15, 30], center=true);
                  cube ([19.997999999999998, 14.998499999999998, 29.996999999999996], center=true);
                }
                cube ([400, 400, 0.2], center=true);
              }
              sphere ($fn=16, r=14);
            }
          }
          cube ([20, 15, 30], center=true);
        }
        translate ([0, -15/2, 0]) {
          mirror ([0, 1, 0]) {
            mirror ([1, 0, 0]) {
              translate ([0.5, 0, 0]) {
                translate ([1, 0, 0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    intersection () {
                      translate ([0, 29, 0]) {
                        cube ([58, 58, 58], center=true);
                      }
                      cylinder (h=2, r=28.5, center=true);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    translate ([0, -15/2, 0]) {
      mirror ([0, 1, 0]) {
        mirror ([1, 0, 0]) {
          translate ([-1, 0, 0]) {
            translate ([2, 0, 0]) {
              rotate (a=90.0, v=[0, 1, 0]) {
                intersection () {
                  translate ([0, 14.86, 0]) {
                    cube ([29.72, 29.72, 29.72], center=true);
                  }
                  cylinder (h=4, r=14.36, center=true);
                }
              }
            }
          }
        }
      }
    }
  }
  translate ([0, -15/2, 0]) {
    mirror ([0, 1, 0]) {
      mirror ([1, 0, 0]) {
        difference () {
          intersection () {
            union () {
              translate ([0.5, 0, 0]) {
                translate ([1, 0, 0]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    intersection () {
                      translate ([0, 15, 0]) {
                        cube ([30, 30, 30], center=true);
                      }
                      cylinder (h=2, r=14.5, center=true);
                    }
                  }
                }
              }
              translate ([-0.001, 0, 0]) {
                translate ([0.5, 0, 0]) {
                  union () {
                    rotate (a=45.0, v=[-1, 0, 0]) {
                      translate ([0, 0, 7.700000000000001]) {
                        rotate (a=-45.0, v=[-1, 0, 0]) {
                          rotate (a=135.0, v=[1, 0, 0]) {
                            difference () {
                              translate ([0, 0, 0]) {
                                rotate (a=45.0, v=[1, 0, 0]) {
                                  rotate (a=45.0, v=[1, 0, 0]) {
                                    rotate (a=45.0, v=[0, 1, 0]) {
                                      cube ([10.65685424949238, 10.65685424949238, 10.65685424949238], center=true);
                                    }
                                  }
                                }
                              }
                              translate ([-200, 0, 0]) {
                                cube ([400, 400, 400], center=true);
                              }
                            }
                          }
                        }
                      }
                    }
                    rotate (a=90.0, v=[-1, 0, 0]) {
                      translate ([0, 0, 7.700000000000001]) {
                        rotate (a=-90.0, v=[-1, 0, 0]) {
                          rotate (a=135.0, v=[1, 0, 0]) {
                            difference () {
                              translate ([0, 0, 0]) {
                                rotate (a=45.0, v=[1, 0, 0]) {
                                  rotate (a=45.0, v=[1, 0, 0]) {
                                    rotate (a=45.0, v=[0, 1, 0]) {
                                      cube ([10.65685424949238, 10.65685424949238, 10.65685424949238], center=true);
                                    }
                                  }
                                }
                              }
                              translate ([-200, 0, 0]) {
                                cube ([400, 400, 400], center=true);
                              }
                            }
                          }
                        }
                      }
                    }
                    rotate (a=135.0, v=[-1, 0, 0]) {
                      translate ([0, 0, 7.700000000000001]) {
                        rotate (a=-135.0, v=[-1, 0, 0]) {
                          rotate (a=135.0, v=[1, 0, 0]) {
                            difference () {
                              translate ([0, 0, 0]) {
                                rotate (a=45.0, v=[1, 0, 0]) {
                                  rotate (a=45.0, v=[1, 0, 0]) {
                                    rotate (a=45.0, v=[0, 1, 0]) {
                                      cube ([10.65685424949238, 10.65685424949238, 10.65685424949238], center=true);
                                    }
                                  }
                                }
                              }
                              translate ([-200, 0, 0]) {
                                cube ([400, 400, 400], center=true);
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
            translate ([0.5, 0, 0]) {
              translate ([3.353553390593274, 0, 0]) {
                rotate (a=90.0, v=[0, 1, 0]) {
                  intersection () {
                    translate ([0, 15, 0]) {
                      cube ([30, 30, 30], center=true);
                    }
                    cylinder (h=6.707106781186548, r=14.5, center=true);
                  }
                }
              }
            }
          }
          translate ([-0.001, 0, 0]) {
            translate ([0.5, 0, 0]) {
              union () {
                rotate (a=45.0, v=[-1, 0, 0]) {
                  translate ([0, 0, 7.700000000000001]) {
                    rotate (a=-45.0, v=[-1, 0, 0]) {
                      rotate (a=135.0, v=[1, 0, 0]) {
                        difference () {
                          translate ([0, 0, 0]) {
                            rotate (a=45.0, v=[1, 0, 0]) {
                              rotate (a=45.0, v=[1, 0, 0]) {
                                rotate (a=45.0, v=[0, 1, 0]) {
                                  cube ([6.65685424949238, 6.65685424949238, 6.65685424949238], center=true);
                                }
                              }
                            }
                          }
                          translate ([-200, 0, 0]) {
                            cube ([400, 400, 400], center=true);
                          }
                        }
                      }
                    }
                  }
                }
                rotate (a=90.0, v=[-1, 0, 0]) {
                  translate ([0, 0, 7.700000000000001]) {
                    rotate (a=-90.0, v=[-1, 0, 0]) {
                      rotate (a=135.0, v=[1, 0, 0]) {
                        difference () {
                          translate ([0, 0, 0]) {
                            rotate (a=45.0, v=[1, 0, 0]) {
                              rotate (a=45.0, v=[1, 0, 0]) {
                                rotate (a=45.0, v=[0, 1, 0]) {
                                  cube ([6.65685424949238, 6.65685424949238, 6.65685424949238], center=true);
                                }
                              }
                            }
                          }
                          translate ([-200, 0, 0]) {
                            cube ([400, 400, 400], center=true);
                          }
                        }
                      }
                    }
                  }
                }
                rotate (a=135.0, v=[-1, 0, 0]) {
                  translate ([0, 0, 7.700000000000001]) {
                    rotate (a=-135.0, v=[-1, 0, 0]) {
                      rotate (a=135.0, v=[1, 0, 0]) {
                        difference () {
                          translate ([0, 0, 0]) {
                            rotate (a=45.0, v=[1, 0, 0]) {
                              rotate (a=45.0, v=[1, 0, 0]) {
                                rotate (a=45.0, v=[0, 1, 0]) {
                                  cube ([6.65685424949238, 6.65685424949238, 6.65685424949238], center=true);
                                }
                              }
                            }
                          }
                          translate ([-200, 0, 0]) {
                            cube ([400, 400, 400], center=true);
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
    }
  }
}
