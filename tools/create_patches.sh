#!/bin/bash
set -e

echo "generating patches.. (from /src/net/minecraft/)"

# Check dir
mkdir -p ../patches

# Remove old patches
rm -f ../patches/*.patch

# 1. Generate the initial patch file using git's standard no-index diff engine
git diff --no-index --binary --ignore-space-at-eol ../clean_src ../src > ../patches/raw_changes.patch || true

# 2. Use native Mac sed to completely delete the sections containing our asset paths.
# This cleanly slices out any blocks tracking files inside /src/cape or /src/title.
sed -E '/diff --git a\/..\/src\/(cape|title)/,/diff --git/ {
    /diff --git a\/..\/src\/(cape|title)/! {
        /diff --git/! d
    }
    /diff --git a\/..\/src\/(cape|title)/ d
}' ../patches/raw_changes.patch > ../patches/mod_changes.patch

# 3. Clean up the temporary raw file
rm -f ../patches/raw_changes.patch

echo "Patches successfully generated in /patches/ directory."
