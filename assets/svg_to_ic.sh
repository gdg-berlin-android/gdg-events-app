#!/bin/bash
#
# generate ic_* from images
#

re="^(.*),([0-9]+)x(.+)";

sizes='../app/src/main/res/mipmap-xxxhdpi/ic_launcher.png,192x192
../app/src/main/res/mipmap-xxhdpi/ic_launcher.png,144x144
../app/src/main/res/mipmap-xhdpi/ic_launcher.png,96x96
../app/src/main/res/mipmap-mdpi/ic_launcher.png,48x48
../app/src/main/res/mipmap-hdpi/ic_launcher.png,72x72
../app/src/main/ic_launcher-web.png,512x512'


for s in ${sizes}; do
  if [[ "${s}" =~ ${re} ]]; then
    o=${BASH_REMATCH[1]};
    w=${BASH_REMATCH[2]};
    h=${BASH_REMATCH[3]};

    echo 'Working on "' ${o} '" ...'

    inkscape logo.svg -w${w} -h${h} --export-background-opacity=0 --export-png=${o}
  else
    echo 'Internal error for "'$s'", please check "sizes" variable.'
  fi
done
