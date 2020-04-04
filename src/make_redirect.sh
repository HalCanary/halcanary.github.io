#! /bin/sh
URL="$1"
cat << EOM
<!doctype html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="refresh" content="0; url=${URL}">
<title>${URL}</title>
</head>
<body>
<a href="${URL}">
<p style="text-align:center;">
<span style="height: 4em;"></span>
<br>
${URL}
<br>
<span style="height: 4em;"></span>
</p>
</a>
</body>
</html>
EOM
