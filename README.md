# Dactyl Marshmallow
The Dactyl is a parameterized, split-hand, concave, columnar, ergonomic keyboard, originally written by Matt Adereth. See [his README](https://github.com/adereth/dactyl-keyboard/README.md) for general info. Here's what's different about mine:

* The whole thing is split into pieces, so you can print each piece on a printer with a smaller build volume, and in a couple of hours not a dozen.
* An extra column for duplicate "y" and "b" keys. This comes at the expense of one of the thumb keys.
* Round, marshmallowy sides and bottom, in contrast to adereth's minimalist squarish shape.
* (In theory) more configurability, like for different numbers of rows and columns. The thumb is still kind of hardcoded, and there are still hacks.

<img src="https://raw.githubusercontent.com/jaredjennings/dactyl-keyboard/master/resources/concept-shot.png" alt="The whole right hand of the keyboard, assembled" />
<br/>
<img src="https://raw.githubusercontent.com/jaredjennings/dactyl-keyboard/master/resources/pieces.png" alt="The whole right hand of the keyboard, exploded view" />

## Assembly

### Generating a Design

**Setting up the Clojure environment**
* [Install the Clojure runtime](https://clojure.org)
* [Install the Leiningen project manager](http://leiningen.org/)
* [Install OpenSCAD](http://www.openscad.org/)

**Generating the design**
* Run `lein repl`
* Load the file `(load-file "src/dactyl_keyboard/dactyl.clj")`
* This will regenerate the `things/*.scad` files
* Use OpenSCAD to open a `.scad` file.

**Iterating**
* Run `sh editloop.sh --no-fstl src/dactyl_keyboard/dactyl.clj dactyl-blank-all`.
* Make changes to design, save files, watch as everything is rerun.

This is not as nice as some of the REPL integration you can do into
Emacs using CIDER, nor as fast as running your `(load-file)` again in
the same REPL, but if you rename a function, forget to change all the
calls to it, and start changing the new copy, a long-running REPL will
still have the old function with the old name hanging about, whereas a
newly run, gosh-why-does-this-take-30-seconds-to-start REPL will catch
your error.

**


### Printing
Pregenerated STL files are available in the [things/](things/) directory.

### Wiring
Masks for the flexible PCBs I used are available for the [left](resources/pcb-left.svg) and [right](resources/pcb-right.svg) side.

A [very rough guide for the brave is here](guide/README.org#wiring) - It will be improved over time (**TODO**)!

## License

Copyright © 2015, 2018 Matthew Adereth and Jared Jennings

The source code for generating the models (everything excluding the [things/](things/) and [resources/](resources/) directories is distributed under the [GNU AFFERO GENERAL PUBLIC LICENSE Version 3](LICENSE).  The generated models and PCB designs are distributed under the [Creative Commons Attribution-ShareAlike License Version 4.0](LICENSE-models).
