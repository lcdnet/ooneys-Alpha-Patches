#!/bin/bash
set e
echo "Resetting source code to vanilla"

if [ -d "../clean_src/net.minecraft" ]; then
  mkdir -p ../src/net.minecraft
  rsync -a --delete ../clean_src/net.minecraft/ ../src/net.minecraft/
else
  echo "Error: clean_src/net.minecraft not found."
  exit 1
fi

echo "Applying patches.."
if [ -d "../patches" ] && [ "$(ls -A ../patches)" ]; then
  for f in ../patches/*.patch; do
    echo "Applying $f"
    # Applies patch inside java dir tree
    patch -p1 -d ../src < "$f"
  done
  echo "Code updated with patches."
else
  echo "No patches found. Code left vanilla."
fi

