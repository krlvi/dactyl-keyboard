#!/bin/sh

default_file="src/dactyl_keyboard/dactyl.clj"
default_obn=dactyl-blank-all

if [ "$1" = "--help" ]; then
    cat >&2 <<EOF
Usage 1: sh editloop.sh [--no-fstl]
Usage 2: sh editloop.sh [--no-fstl] file.clj output-basename 

Watches a Clojure file, loading it in a Clojure repl whenever it changes.
This emits an scad file, which editloop then renders into an stl, then
converts to binary. Then it kills fstl and reruns it on the new binary
stl. 

Default file is $default_file
Default output basename is $default_obn 

EOF
    exit 1
    fi

USE_FSTL=1
if [ "$1" = "--no-fstl" ]; then
	USE_FSTL=0
	shift
fi
if [ -n "$1" ]; then
    SOURCE="$1"
    shift
else
    SOURCE="$default_file"
fi
if [ -n "$1" ]; then
    OBN="$1"
    shift
else
    OBN="$default_obn"
fi
OD=things
SCAD="${OD}/${OBN}.scad"
ASTL="${OD}/${OBN}.stl"
BSTL="${OD}/${OBN}-binary.stl"

step () {
    local name=$1
    shift
    local eta=unknown
    local sec=unknown
    local elapsedfn=".elapsed.$name"
    if [ -f $elapsedfn ]; then
	sec=$(cat $elapsedfn)
	eta=$(date -d "$sec seconds")
    fi
    echo
    echo
    echo "$(datef): (eta $eta, $sec seconds) $@"
    local t1=$(date +%s.%N)
    "$@"
    local t2=$(date +%s.%N)
    echo "$t2 $t1 - p" | dc > "$elapsedfn"
}

datef () {
    date +%H:%M:%S "$@"
}

while inotifywait -r -e close_write $(dirname $SOURCE) --exclude '.*#.*' --exclude '.*~' --exclude '.*\.swp'; do
    echo
    echo
    echo "$(datef): !!!!!!!!!!!!!!!!!!!!!!!!! something modified; rebuilding $SOURCE"
    echo
    echo "(load-file \"$SOURCE\")" | step make-scad lein repl
    echo "\$\? was $?"
    if [ "$USE_FSTL" -eq 1 ]; then
        step render-stl openscad -o "$ASTL" --render "$SCAD"
        convert_stl "$ASTL"
        killall fstl
        fstl "$BSTL" &
    fi
    echo
    echo
    echo
    echo
    echo
    echo "$(datef): ????????????????????????? go"
done
