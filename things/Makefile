TARGETS = $(subst .scad,.stl,$(wildcard *.scad))

all: $(TARGETS)

%.stl: %.scad
	openscad --render -o $@ $^
