#! /usr/bin/env python
import re
import math

head = '''<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="theme-color" content="#000000">
<title>{0}</title>
<script>
window.addEventListener('load', () => {{
  const ontoggle = (evt) => {{
    if (evt.currentTarget.open) {{
      location.hash = '#' + evt.currentTarget.id;
      for (var det of dets) {{
        if (evt.currentTarget != det) {{
          det.open = false;
        }}
      }}
    }}
  }};
  const dets = document.getElementsByClassName("det");
  for (var det of dets) {{ det.addEventListener("toggle", ontoggle); }}
  let h = location.hash.substring(1);
  if (h) {{
    var x = document.getElementById(h);
    if (x) {{
      x.open = "true"
    }}
  }}
  document.body.style.fontFamily = "sans-serif";

  var b = document.createElement("button");
  b.addEventListener("click", toggleFontStyle);
  b.append("Toggle Font Style")
  document.getElementById("stylish").append(b);
}});
const toggleFontStyle = () => {{
  const s = document.body.style;
  s.fontFamily = (s.fontFamily == "sans-serif") ? "serif" : "sans-serif";
}};
</script>
<style>
body{{font-family:sans-serif;}}
@media screen{{body{{max-width:35em;margin:1em auto;padding:0 0.5em;}}}}
@media print{{summary,button{{display:none;}}}}
pre{{overflow-x:auto;}}
p{{text-indent:1.25em;margin-top:0;margin-bottom:0;line-height:1.5;}}
p:first-of-type{{text-indent:0;}}
details{{margin:0.5em 0;}}
h1{{text-align:center;}}
@media(prefers-color-scheme:dark){{
body{{background-color:#000;color:#FFF;}}
a:visited{{color:#C0F;}}
a:link,a:hover,a:active{{color:#0CF;}}}}
</style>
</head>
<body>
<div id="stylish"></div>
'''

tail = '\n</body>\n</html>\n'

with open('src.txt') as f:
    content = f.read()
book_title = 'Adventures of Huckleberry Finn'
h1 = ''
m = re.search('<h1>.*?</h1>', content, flags=re.S)
if m:
    h1 = m.group(0)
    content = re.sub('<h1>.*?</h1>', '', content, 1, re.S)
content = content.split('<hr>')
book = []
for i, chapter in enumerate(content):
    m = re.search('<h2>(.*?)</h2>', chapter, flags=re.S)
    assert m
    title = m.group(1)
    book.append((title, chapter))

with open('index.html', 'w') as o:
    o.write(head.format(book_title))
    o.write(h1)
    o.write('<hr>\n')
    for i, (title, chapter) in enumerate(book):
        isopen = ' open' if 0 == i else ''
        o.write('<details class="det" id="{:d}" {}>\n<summary>{}</summary>\n'.format(
            i, isopen, title))
        o.write(chapter)
        o.write('<hr>\n</details>\n\n')
    o.write(tail)
