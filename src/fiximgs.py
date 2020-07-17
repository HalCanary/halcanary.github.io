#! /usr/bin/env python
import sys, subprocess, re, os, urllib


def widthheight(path):
    if any(path.startswith(x) for x in ['https://', 'http://']):
        try:
            fn, hdrs = urllib.urlretrieve(path)
            path = fn
        except:
            return None
    else:
        path = '.' + path
    if not os.path.exists(path):
        return None

    cmd = ['identify', '-format', 'width="%w" height="%h"', path]
    try:
        with open(os.devnull, 'w') as devnull:
            return subprocess.check_output(cmd, stderr=devnull)
    except:
        return None
        
def replace(m):
    alt, url = m.group(1), m.group(2)
    wh = widthheight(url)
    if wh:
        return '<img src="{url}" alt="{alt}" {wh}>'.format(url=url, alt=alt, wh=wh)
    else:
        return m.group(0)


def proc(path):
    with open(path) as f:
        c = f.read()
    r = re.sub('!\[([^][]*)\]\(([^()]*)\)', replace, c)
    if r != c:
        with open(path, 'w') as o:
            o.write(r)

for arg in sys.argv[1:]:
    proc(arg)
