#! /bin/sh
{ [ "$1" ] && [ "$2" ]; } || exit 1
mkdir -p "$2"
cat > "${2}/index.html" << EOM
<!doctype html>
<html lang="en">
<meta charset="UTF-8">
<meta http-equiv="refresh" content="0; url=${1}">
<title>${1}</title>
<a href="${1}">${1}</a>
</html>
EOM
