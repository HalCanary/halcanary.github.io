#!/usr/bin/env python
# coding=utf-8

import sys, os, subprocess, datetime, base64, re

PY3 = sys.version_info > (3,)

def to_str(src):
    return str(src, encoding='utf-8') if PY3 else src

def to_bytes(src):
    return src.encode('utf-8') if PY3 else src

def pipe_process(src, cmd):
    try:
        subproc = subprocess.Popen(cmd, stdin=subprocess.PIPE, stdout=subprocess.PIPE)
    except OSError:
        sys.stderr.write('\n%r failed.\n\n' % cmd)
        sys.exit(1)
    subproc.stdin.write(to_bytes(src))
    subproc.stdin.close()
    result = subproc.stdout.read()
    subproc.wait()
    return to_str(result)

def get_first_h1(s):
    m = re.search("(?s)<h1>(.*?)</h1>", s)
    return m.group(1) if m else None

def write_file(filepath, filecontent):
    directory = os.path.dirname(filepath)
    if os.path.exists(filepath):
        if filecontent == read_file(filepath):
            return
    if not os.path.exists(directory):
        os.makedirs(directory)
    kwargs = dict(encoding='utf-8') if PY3 else dict()
    with open(filepath, 'w', **kwargs) as o:
        o.write(filecontent)

def read_file(filepath, binary=False):
    assert os.path.exists(filepath)
    kwargs = dict(encoding='utf-8') if PY3 and not binary else dict()
    with open(filepath, 'rb' if binary else 'r', **kwargs) as f:
        return f.read()

page_template = """<!DOCTYPE html>
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
<div class="tophead">
{svg}
</div>
{content}
</body>
</html>
"""

pages = [
    'portfolio'
]

def main():
    root = os.path.dirname(__file__) + '/../'
    style = re.sub(r'(^|:)  *', r'\1', read_file(root + 'src/style.css'), flags=re.M).strip()
    years = '1997-{:04d}'.format(datetime.date.today().year)
    icon = base64.b64encode(read_file(root + 'images/2020-HWC3-favicon.png', binary=True))
    if (sys.version_info > (3, 0)):
        icon = str(icon, encoding='us-ascii')
    svg = read_file(root + 'src/hal_canary_3.svg').strip()

    for dst in pages:
        src = 'src/pages/%s.md' % dst
        content = pipe_process(read_file(root + src), ['markdown'])
        title = get_first_h1(content)
        assert title
        write_file(root + dst + '/index.html', page_template.format(
            years=years,
            title=title,
            icon=icon,
            style=style,
            svg=svg,
            content=content))

if __name__ == '__main__':
    main()
