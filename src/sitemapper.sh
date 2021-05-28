#! /bin/sh
cd "$(dirname "$0")/.."
find docs -name \*.html | \
    sed 's/index.html$//;s@^docs/@https://halcanary.org/@' | \
    sort -u > docs/sitemap.txt
