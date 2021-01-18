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
<link rel="stylesheet" href="style.css">
<script src="style_toggle.js"></script>
</head>
<body>
<script>
init_toggle();
init_chapter();
</script>
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
    o.write('<p>\n')
    for i, (title, _) in enumerate(book):
        if i > 0:
            o.write('| ')
        o.write('<a href="#{:d}">{}</a>\n'.format(i, title))
    o.write('</p>\n<hr>\n')
    for i, (title, chapter) in enumerate(book):
        o.write('<div style="display:none" id="{:d}">'.format(i))
        o.write(chapter)
        if i + 1 < len(book):
            next_title, _ = book[i + 1]
            o.write('\n<hr>\n<p class="next"><a href="#{:d}">Next: {}</a></p>\n'.format(
                i + 1, next_title))
        o.write('<hr>\n</div>\n\n')
    o.write(tail)
