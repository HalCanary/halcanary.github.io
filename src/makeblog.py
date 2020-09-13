#!/usr/bin/env python
# coding=utf-8

import sys, os, math, subprocess, re, datetime, tempfile, hashlib, shutil

BASE_DIR = 'vv'
URL_BASE = 'https://halcanary.org'

MARKDOWN_CMD = ['cmark']
if float(re.sub(r'cmark (\d+\.\d+)\..*', r'\1', subprocess.check_output(['cmark', '--version']), flags=re.S)) >= 0.29:
    MARKDOWN_CMD = ['cmark', '--unsafe']

current_year = '{:04d}'.format(datetime.date.today().year)

with open(os.path.dirname(__file__) + '/style.css') as f:
    style = re.sub(r'(^|:)  *', r'\1', f.read(), flags=re.M).strip()

header = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>{title}</title>
<link rel="icon" href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACAAQAAAADrRVxmAAAACXBIWXMAAAMfAAADHwHmEQywAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAVZJREFUSMfd1EuOwyAMAFAQ0rAZNUfIUTga5GbsZtkrcINhyQLBYMgHG6ZddJcoSpOXDzY2ZZls7F5Q9iAzhyu/XsAomCm4AdRHYPUUNAH7GTgdRbZd+k6FCZgGZQPw/DV45dgIWw+rc3rrRvGrHcBrjmDzqocAIBCIsr+ERcYFwYNAfMj0QPC9pC+JIWNgpfJvQGXTQwJYCFgMegSHIWPIA/DaplO49UIuEy7qUR5gWakJHA+oXZhNZYBYQNQnlgaBKSthrRjRwJXfcu+ZA28A5fmF7yXWwKg+BgB9AJyxPtgGib8FQSCKjEcZQR7XqUUaTgiCgJME9vRPMEwREJk+McCKodUBDUvjiDTSfU4v2NPvwCqc7QHif3Bz6Ga9vZIYga5yVs9rO1Qf+uOnSw6yhnKu2Yurx+C/Ru8TVLuQl9HPtmx9CqCvTlYwxxz3eqhv3Htl/wEokJpySHNGkgAAAABJRU5ErkJggg==">
<style>
{style}
</style>
</head>
<!-- Copyright {years} Hal Canary. ALL RIGHTS RESERVED. -->
<body>
"""

top_posts_header = """
<h1>Voder-Vocoder</h1>
<div class="byline centered">The Log of Hal Canary</div>

<div class="postbox">
  <div id="search-link-halcanary-org">
    <div class="rightside"><a href="#" onclick="document.getElementById('search-link-halcanary-org').style.display='none'; document.getElementById('search-halcanary-org').style.display='block'; return false;">search</a></div>
  </div>
  <div id="search-halcanary-org" style="display:none;">
    <form method="get" action="https://www.google.com/search">
      <div class="rightside">
        <input name="domains" value="halcanary.org" type="hidden" />
        <input name="sitesearch" value="halcanary.org" type="hidden" />
        <input id="search" name="q" size="30" maxlength="255" />
        <input value="Search" type="submit" />
      </div>
    </form>
  </div>
</div>
"""
top_posts_footer = """
<div class="centered"><a href="archives/">MORE</a></div>
</body>
</html>
"""

def hashpath(path):
    if path is None:
        return None
    h = hashlib.sha256()
    with open(path,'r') as f:
        data = f.read(h.block_size)
        while data != '':
            h.update(data)
            data = f.read(h.block_size)
    return h.digest()
def mkdirs(path):
    try:
        os.makedirs(path)
    except:
        pass
class UpdatingFile(object):
    def __init__(self, path):
        self.tempfilepath = None
        self.path = path
        self.o = None
        path_dir = os.path.dirname(path)
        mkdirs(path_dir)
        fd, self.tempfilepath = tempfile.mkstemp()
        self.o = os.fdopen(fd, "w")
    def __del__(self):
        self.close()
    def close(self):
        if self.o is not None:
            self.o.close()
            self.o = None
            if (not os.path.isfile(self.path)) or (
                hashpath(self.tempfilepath) !=
                hashpath(self.path)):
                if shutil:
                    shutil.copyfile(self.tempfilepath, self.path)
                os.remove(self.tempfilepath)
                return True
            else:
                if self.tempfilepath is not None:
                    os.remove(self.tempfilepath)
                return False
    def write(self,*args, **kwargs):
        if self.o is not None:
            self.o.write(*args, **kwargs)
        else:
            sys.stderr.write(*args, **kwargs)


class Post(object):
    @staticmethod
    def markdown(inf):
        try:
            subproc = subprocess.Popen(MARKDOWN_CMD, stdin=subprocess.PIPE, stdout=subprocess.PIPE)
        except OSError:
            sys.stderr.write('\n%r FAILED.\n\n' % MARKDOWN_CMD)
            sys.exit(1)
        shutil.copyfileobj(inf, subproc.stdin)
        subproc.stdin.close()
        s = subproc.stdout.read()
        subproc.wait()
        return s

    @staticmethod
    def write(o, post_info, level):
        category_fmt = '<a href="/{1}/category/{0}/" class="categorylink">#{0}</a>'
        categories = '; '.join(category_fmt.format(cat, BASE_DIR) for cat in post_info['category_list'])
        categories = '      <div>{}</div>'.format(categories) if categories else ''
        # keys = [ 'src', 'permalinkpart', 'title', 'summary', 'date', 'category_list', 'post' ]
        summ = '    <p>{}</p>'.format(post_info['summary']) if post_info['summary'] else ''
        post_template = """
<article class="postbox">
  <header>
    <!-- SRC= {src} -->
    <h{level} class="blogtitle">{title}</h{level}>
{summ}
    <div class="byline">
      <div>by: Hal Canary</div>
      <div>posted: {date}</div>
      <div>link: <a href="/{permalinkpart}">{URL_BASE}/{permalinkpart}</a></div>
{categories}
    </div>
  </header>
  <div class="content">
{post}
  </div>
</article>
"""
        o.write(post_template.format(level=level, categories=categories, summ=summ,
                                     URL_BASE=URL_BASE, **post_info))

    def __init__(self, path):
        # Required:
        #    TITLE
        #    DATE
        #    POSTID
        # Suggested:
        #    SUMMARY
        #    CATEGORIES
        #    MODE
        # Everything else is ignored
        if not os.path.isfile(path):
            raise Exception(path + " is not a file")

        self.src = os.path.basename(path)
        self.category_list = []
        self.summary = ''
        self.date = None
        self.day = None
        self.month = None
        self.permalinkpart = None
        self.post = None
        self.title = None
        self.year = None

        postid = None
        mode = 'markdown'
        with open(path,'r') as f:
            while True:
                sline = f.readline().strip()
                if sline == '':
                    break
                if '=' not in sline:
                    sys.stderr.write(sline + '\n') # error
                    continue
                key, value = sline.split('=',1)
                value = value.strip()
                if value == '':
                    continue
                if key == 'TITLE':
                    self.title = value
                elif key == 'DATE':
                    self.date = value
                elif key == 'POSTID':
                    postid = value
                elif key == 'SUMMARY':
                    self.summary = value
                elif key == 'CATEGORIES':
                    self.category_list = filter(len, value.split(';'))
                elif key == 'MODE':
                    mode = value
                elif key != 'COPYRIGHT':
                    sys.stderr.write('%s: %r=%r\n' % (path, key, value))

            if mode.lower() == 'markdown':
                self.post = Post.markdown(f).rstrip()
            else:
                self.post = f.read().rstrip()

        if self.date is None:
            raise Exception('DATE missing ({})'.format(path))
        if postid is None:
            raise Exception('POSTID missing ({})'.format(path))
        if self.title is None:
            raise Exception('TITLE missing ({})'.format(path))

        timestamp = datetime.datetime.strptime(self.date.split()[0], '%Y-%m-%d').date()
        self.year  = '{:04d}'.format(timestamp.year)
        self.month = '{:02d}'.format(timestamp.month)
        self.day   = '{:02d}'.format(timestamp.day)

        self.permalinkpart = '{}/{}/{}/{}/{}/'.format(BASE_DIR, self.year, self.month, self.day, postid)

    def write_to_file(self, output_path):
        o = UpdatingFile(output_path)
        o.write(header.format(title='Voder-Vocoder: ' + self.title, style=style, years=self.year + '-' + current_year))
        Post.write(o, vars(self), 1)
        o.write('\n<p>(<a href="/{}/archives/">back</a>)</p>\n</body>\n</html>\n'.format(BASE_DIR))
        if o.close():
            sys.stdout.write('modified %s\n' % o.path)

class RecentPosts(object):
    def __init__(self):
        self.posts = []
    def addPost(self, post):
        self.posts.append(post)
    def close(self):
        o = UpdatingFile('{}/index.html'.format(BASE_DIR))
        o.write(header.format(style=style, title='Voder-Vocoder', years='1997-' + current_year))
        o.write(top_posts_header)
        for post in self.posts:
            Post.write(o, vars(post), 2)
        o.write(top_posts_footer)
        if o.close():
            sys.stdout.write('modified %s\n' % o.path)

def addPost(indices, pathlist, name, post):
    info = {k: vars(post)[k] for k in ['permalinkpart', 'year', 'title', 'summary', 'date']}
    _, posts = indices.setdefault(pathlist, (name, {}))
    posts.setdefault(post.year, []).append(info)

def write_indices(indices):
    for pathlist, (name, posts) in indices.items():
        loc = '{}/{}/index.html'.format(BASE_DIR, '/'.join(pathlist))
        o = UpdatingFile(loc)
        o.write(header.format(style=style, title='Voder-Vocoder Archive', years='1997-' + current_year))
        name = '<br>' + name if name else ''
        o.write('<h1><a href="/{d}/" class="hiddenlink">Voder-Vocoder</a> Archive {name}</h1>'.format(
            d=BASE_DIR, name=name))
        for year in sorted(posts.keys(), reverse=True):
            year_posts = posts[year]
            if len(posts) > 1:
                o.write('<h2>{year}</h2>\n'.format(year=year))
            o.write('<ul>\n')
            for post in year_posts:
                s = '  <div>{}</div>'.format(post['summary']) if post['summary'] else ''
                fmt = '<li>\n  <a href="/{permalinkpart}">{title}</a>\n  {date}\n>{s}\n</li>\n'
                o.write(fmt.format(s=s, **post))
            o.write('</ul>\n\n')
        o.write('<hr class="black">\n' +
                '<p class="centered"><a href="../">UP</a></p>\n' +
                '</body>\n</html>\n')
        if o.close():
            sys.stdout.write('modified %s\n' % loc)

def listdir(path, reverse=False):
    assert os.path.isdir(path)
    files = os.listdir(path)
    files.sort(reverse=reverse)
    for fn in files:
        yield os.path.join(path, fn)

if __name__ == '__main__':
    src_dir = os.path.abspath(os.path.dirname(__file__))
    target_location = os.path.join(src_dir, os.pardir)
    source_location = os.path.join(src_dir, 'BlogSrc')

    directory = source_location
    os.chdir(target_location)
    assert os.path.isdir(directory)
    indices = {}
    recentPosts = RecentPosts()
    for i, path in enumerate(listdir(directory, reverse=True)):
        if path[-1] == '~':
            continue
        if os.path.isfile(path):
            post = Post(path)
            post.write_to_file(post.permalinkpart + 'index.html')

            if i < 10:
                recentPosts.addPost(post)
            ymd = [post.year, post.month, post.day]
            addPost(indices, tuple(ymd[0:1]),       '{}'.format(*ymd), post)
            addPost(indices, tuple(ymd[0:2]),    '{}-{}'.format(*ymd), post)
            addPost(indices, tuple(ymd[0:3]), '{}-{}-{}'.format(*ymd), post)
            addPost(indices, ('archives',), '', post)
            for cat in post.category_list:
                addPost(indices, ('category', cat), '#' + cat, post)
        else:
            sys.stderr.write("???? %s\n" % path)
    recentPosts.close()
    write_indices(indices)
