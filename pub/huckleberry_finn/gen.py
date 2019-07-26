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
</script>
'''

tail = '\n<hr>\n</body>\n</html>\n'


with open('src.txt') as f:
    content = f.read()
book_title = 'Adventures of Huckleberry Finn'

content = content.split('<hr>')
book = []
for i, chapter in enumerate(content):
    path = '%0*d.html' % (int(math.log(len(content), 10) + 1), i)
    m = re.search('<h2>(.*?)</h2>', chapter, flags=re.S)
    assert m
    title = m.group(1)
    book.append((path, title, chapter))

for i, (path, title, chapter) in enumerate(book):
    with open(path, 'w') as o:
        o.write(head.format(book_title + ': ' + title))
        o.write('\n<!-- BEGIN CONTENT -->\n')
        o.write(chapter)
        o.write('\n<!-- END CONTENT -->\n')
        if i + 1 < len(book):
            next_path, next_title, _ = book[i + 1]
            o.write('\n<hr>\n<p class="next"><a href="{}">Next: {}</a></p>\n'.format(next_path, next_title))
        o.write(tail)
with open('index.html', 'w') as o:
    o.write(head.format(book_title))
    o.write('<p>\n')
    for path, title, _ in book:
        o.write('<a href="{}">{}</a><br>\n'.format(path, title))
    o.write('</p>\n')
    o.write(tail)
