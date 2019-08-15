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

tail = '\n<hr>\n</body>\n</html>\n'

with open('src.txt') as f:
    content = f.read()
book_title = 'Adventures of Huckleberry Finn'

content = content.split('<hr>')
book = []
for i, chapter in enumerate(content):
    name = '%d' % i
    m = re.search('<h2>(.*?)</h2>', chapter, flags=re.S)
    assert m
    title = m.group(1)
    chapter = re.sub('<div>', '<div style="display:none" id="%s">' % name, chapter, 1)
    book.append((name, title, chapter))

with open('hf.html', 'w') as o:
    o.write(head.format(book_title))
    for name, title, chapter in book:
        o.write('<div><a href="#{}">{}</a></div>\n'.format(name, title))
        o.write(chapter)
        o.write('<hr>\n')
    o.write(tail)
