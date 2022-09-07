
import base64
import datetime
import os
import re
import subprocess
import sys

assert sys.version_info > (3,)
try:
    import lxml.html, lxml.html.builder
except ModuleNotFoundError:
    import subprocess
    subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'lxml'])
    import lxml.html, lxml.html.builder

#MARKDOWN_CMD = ['cmark']
#
#if float(re.sub(r'cmark (\d+\.\d+)\..*', r'\1', subprocess.check_output(['cmark', '--version']), flags=re.S)) >= 0.29:
#    MARKDOWN_CMD = ['cmark', '--unsafe']

MARKDOWN_CMD = ['pandoc',
                '--from=commonmark',
                '--to=html',
                '--wrap=preserve',
                '--no-highlight']

def to_str(src):
    return str(src, encoding='utf-8')

def to_bytes(src):
    return src.encode('utf-8')

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

def read_file(filepath, binary=False):
    assert os.path.exists(filepath)
    kwargs = dict(encoding='utf-8') if not binary else dict()
    with open(filepath, 'rb' if binary else 'r', **kwargs) as f:
        return f.read()

def write_file(filepath, filecontent):
    directory = os.path.dirname(filepath)
    if os.path.exists(filepath):
        if filecontent == read_file(filepath):
            return False
    if not os.path.exists(directory):
        os.makedirs(directory)
    kwargs = dict(encoding='utf-8')
    with open(filepath, 'w', **kwargs) as o:
        o.write(filecontent)
    return True

SRC = os.path.dirname(__file__)

ROOT = os.path.relpath(SRC + '/../docs')

STYLE = re.sub(r'(^|:|;|{)\s+', r'\1', read_file(SRC + '/style.css'), flags=re.M).strip()

ICON = to_str(base64.b64encode(read_file(ROOT + '/images/2020-HWC3-favicon.png', binary=True)))

SVG = read_file(SRC + '/hal_canary_3.svg').strip()

HEAD = """<head>
<meta charset="utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>{title}</title>
<link rel="icon" href="data:image/png;base64,{icon}"/>
<style>
{style}
</style>
</head>
<!-- Copyright {years} Hal Canary. ALL RIGHTS RESERVED. -->"""

YEARS = '{:04d}-{:04d}'.format(1997, datetime.date.today().year)

#def head(title, icon=ICON, style=STYLE, years=YEARS):
#    return HEAD.format(title=title, icon=ICON, style=STYLE, years=years)

def head(title, icon=ICON, style=STYLE, years=YEARS):
    E = lxml.html.builder
    ICON = 'data:image/png;base64,' + to_str(base64.b64encode(read_file(ROOT + '/images/2020-HWC3-favicon.png', binary=True)))
    return lxml.html.tostring(E.HEAD(
        '\n',
        E.META(charset='utf-8'),
        '\n',
        E.META(name='viewport', content='width=device-width, initial-scale=1.0'),
        '\n',
        E.TITLE(title),
        '\n',
        E.LINK(rel='icon', href=ICON),
        '\n',
        E.STYLE('\n' + STYLE + '\n'),
        '\n',
        E.LINK(title="/vv/", href="/vv/rss.rss", rel="alternate", type="application/atom+xml"),
        '\n',
        lxml.html.HtmlComment(' Copyright {} Hal Canary. ALL RIGHTS RESERVED. '.format(years)),
        '\n',
    ), encoding='unicode')
