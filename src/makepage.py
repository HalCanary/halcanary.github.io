#!/usr/bin/env python
# coding=utf-8

import sys, os, subprocess, datetime, base64, re

def markdown(src):
    try:
        subproc = subprocess.Popen(
            ['markdown'], stdin=subprocess.PIPE, stdout=subprocess.PIPE)
    except OSError:
        sys.stderr.write('\nMARKDOWN FAILED.\n\n')
        sys.exit(1)
    subproc.stdin.write(src)
    subproc.stdin.close()
    result = subproc.stdout.read()
    subproc.wait()
    return result

def get_first_h1(s):
    m = re.search("(?s)<h1>(.*?)</h1>", s)
    return m.group(1) if m else None

template = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Copyright {years} Hal Canary. ALL RIGHTS RESERVED. -->
<title>{title}</title>
<link rel="icon" href="data:image/png;base64,{icon}">
<style>
{style}
</style>
</head>
<body>
<div style="text-align:center; margin:1ex 0 0 0">
{svg}
</div>
{content}
</body>
</html>
"""

pages = [
    ('portfolio', 'src/portfolio.md'),
]

def main():
    root = os.path.dirname(__file__) + '/../'

    with open(root + 'src/style.css') as f:
        style = re.sub(r'(^|:)  *', r'\1', f.read(), flags=re.M).strip()

    years = '1997-{:04d}'.format(datetime.date.today().year)

    with open(root + 'images/2020-HWC3-favicon.png') as f:
        icon = base64.b64encode(f.read())

    with open(root + 'src/hal_canary_3.svg') as f:
        svg = f.read().strip()
    for dst, src in pages:
        if not os.path.exists(root + dst):
            os.makedirs(root + dst)
        with open(root + src) as f:
            content = markdown(f.read())
        title = get_first_h1(content)
        assert title
        with open(root + dst + '/index.html', 'w') as o:
            o.write(template.format(years=years,
                                    title=title,
                                    icon=icon,
                                    style=style,
                                    svg=svg,
                                    content=content))

if __name__ == '__main__':
    main()
